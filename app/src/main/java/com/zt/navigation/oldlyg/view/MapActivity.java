package com.zt.navigation.oldlyg.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Feature;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.Symbol;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteDirection;
import com.esri.core.tasks.na.RouteResult;
import com.zt.navigation.oldlyg.MyApplication;
import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.MapContract;
import com.zt.navigation.oldlyg.presenter.MapPresenter;
import com.zt.navigation.oldlyg.tts.BDTTS;
import com.zt.navigation.oldlyg.view.adapter.AddressAdapter;
import com.zt.navigation.oldlyg.view.adapter.BootomAddressAdapter;
import com.zt.navigation.oldlyg.view.include.MainRouceListInclude;
import com.zt.navigation.oldlyg.view.include.NaviceStatusInclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;
import cn.faker.repaymodel.util.CustomUtility;
import cn.faker.repaymodel.util.DpUtil;
import cn.faker.repaymodel.util.LogUtil;
import cn.faker.repaymodel.util.ScreenUtil;
import cn.faker.repaymodel.util.ToastUtility;
import cn.faker.repaymodel.widget.view.BaseRecycleView;

public class MapActivity extends BaseMVPAcivity<MapContract.View, MapPresenter> implements MapContract.View
        , SearchView.OnQueryTextListener {
    private final int PERMISSIONS_CODE_LOCATION = 200;

    private MapView mMapView;
    private SearchView poi_searchView;
    private RecyclerView rv_search_list;
    private RecyclerView rv_query_list;
    private LinearLayout ll_search;

    private GraphicsLayer mGraphicsLayer;
    private GraphicsLayer hiddenSegmentsLayer;

    private Map<String, Point> search_Data = new HashMap<>();

    private AddressAdapter addressAdapter;
    private BootomAddressAdapter bootomAddressAdapter;

    private MainRouceListInclude rouceListInclude = new MainRouceListInclude(this);
    private NaviceStatusInclude naviceStatusInclude = new NaviceStatusInclude(this);

    private Point endPoint;//目的地
    private boolean isNai = false;//是否导航
    private boolean isOne = false;//第一次进入 定位缩放自身
    private BDTTS bdtts = new BDTTS();
    private String stopName = null;//目的地名称

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_map;
    }

    @Override
    protected void initContentView() {
        isShowBackButton(false);
        isShowToolView(false);

        mMapView = (MapView) findViewById(R.id.mapview);

        poi_searchView = findViewById(R.id.poi_searchView);
        poi_searchView.setIconifiedByDefault(false);
        poi_searchView.setOnQueryTextListener(this);
        poi_searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAcitvity(SearchActivity.class);
            }
        });

        rv_query_list = findViewById(R.id.rv_query_list);
        rv_query_list.setLayoutManager(new LinearLayoutManager(getContext()));

        rv_search_list = findViewById(R.id.rv_search_list);
        rv_search_list.setLayoutManager(new LinearLayoutManager(getContext()));
        ll_search = findViewById(R.id.ll_search);
        initMap();
        initWidth();
        initPermission();
        // TODO: 2019/11/13 权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_CODE_LOCATION);
        } else {
            location();
        }
    }

    private boolean isSearch = true;

    @Override
    public void initData(Bundle savedInstanceState) {
        addressAdapter = new AddressAdapter();
        rv_search_list.setAdapter(addressAdapter);
        addressAdapter.setOnItemClickListener(new BaseRecycleView.OnItemClickListener<String>() {
            @Override
            public void onItemClick(View view, String data, int position) {
                isSearch = false;
                showLoading();
                addressAdapter.setSuggestResults(null);
                poi_searchView.setQuery(data, true);
            }
        });

        bootomAddressAdapter = new BootomAddressAdapter();
        rv_query_list.setAdapter(bootomAddressAdapter);
        bootomAddressAdapter.setOnItemClickListener(new BootomAddressAdapter.OnAddressItemClickListener() {
            @Override
            public void onItemClick(View view, Point data, String name, int position) {
                showLoading();
                if (lon <= 0 || lat <= 0) {
                    ToastUtility.showToast("导航需要开启定位,请检查系统设置是否给予定位权限");
                } else {
                    stopName = name;
                    mPresenter.queryDirections(new Point(lon, lat), data, stopName);
                }
            }
        });
    }

    private void initMap() {
        ArcGISDynamicMapServiceLayer arcGISTiledMapServiceLayer = new ArcGISDynamicMapServiceLayer(Urls.mapUrl);
        mMapView.addLayer(arcGISTiledMapServiceLayer);
//        mMapView.zoomToScale(new Point(119.40169524800001, 34.749215468000045), 3);
        mGraphicsLayer = new GraphicsLayer();
        mMapView.addLayer(mGraphicsLayer);
        hiddenSegmentsLayer = new GraphicsLayer();
        mMapView.addLayer(hiddenSegmentsLayer);

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (status == STATUS.LAYER_LOADED) {
                    if (lat > 0 && lon > 0) {
                        mMapView.zoomTo(new Point(lon, lat), 1000);
                    } else {
                        isOne = true;
                    }
                }
            }
        });
    }

    /**
     * 初始化部分控件宽高
     */
    private void initWidth() {
        int windowWidth = ScreenUtil.getWindowWidth(getApplicationContext());
        int height = ScreenUtil.getWindowHeight(getContext());
        int width = windowWidth * 4 / 5;
        ViewGroup.LayoutParams ll_search_params = ll_search.getLayoutParams();
        ll_search_params.width = width;

        RelativeLayout.LayoutParams rv_search_list_params = (RelativeLayout.LayoutParams) rv_search_list.getLayoutParams();
        rv_search_list_params.width = (int) (width - DpUtil.dip2px(getApplicationContext(), 22));
        rv_search_list_params.setMargins(22, 0, 0, 0);
        rv_search_list_params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
        rv_search_list_params.addRule(RelativeLayout.BELOW, R.id.ll_search);

        // TODO: 2019/10/25 不显示
        RelativeLayout.LayoutParams rv_query_list_params = (RelativeLayout.LayoutParams) rv_query_list.getLayoutParams();
        rv_query_list_params.width = (int) (windowWidth - DpUtil.dip2px(getApplicationContext(), 22));
        rv_query_list_params.height = (int) (height * 1 / 3);
        rv_query_list_params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
        rv_query_list_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
    }

    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        } else {
//            bdtts.init(getContext(), new Handler());//暂时不接收信息
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        naviceStatusInclude.setOnClickStatu(new NaviceStatusInclude.OnClickStatu() {
            @Override
            public void onExit() {
                //关闭语音导航 定位模式
                isNai = false;
                bdtts.stop();
                rouceListInclude.clean();
                naviceStatusInclude.off();
                mGraphicsLayer.removeAll();
                hiddenSegmentsLayer.removeAll();

                poi_searchView.setVisibility(View.VISIBLE);
            }
        });
    }

    private double lat;
    private double lon;
    private LocationDisplayManager locationDisplayManager;

    private void location() {
        locationDisplayManager = mMapView.getLocationDisplayManager();
        locationDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
        locationDisplayManager.setLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String bdlat = location.getLatitude() + "";
                String bdlon = location.getLongitude() + "";
                if (bdlat.indexOf("E") == -1 | bdlon.indexOf("E") == -1) {
                    //这里做个判断是因为，可能因为gps信号问题，定位出来的经纬度不正常。
                    Log.i("定位", lat + "?" + lon);
                    lat = location.getLatitude();//纬度
                    lon = location.getLongitude();//经度
                    if (isNai) {
                        mPresenter.navigation(new Point(lon, lat), endPoint, stopName);
                        mMapView.setExtent(new Point(lon, lat), 250);
                    }
                    if (isOne) {
                        mMapView.zoomTo(new Point(lon, lat), 1000);
                        isOne = false;
                    }
                    MyApplication.startPoint = new Point(lon, lat);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
        locationDisplayManager.start();
    }

    @Override
    public void search_Success(int type, Iterator<Object> iterator) {
        dimiss();
        mGraphicsLayer.removeAll();
        hiddenSegmentsLayer.removeAll();
        rv_query_list.setVisibility(View.GONE);
        bootomAddressAdapter.setData(null, null);

        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) {
            Feature feature = (Feature) iterator.next();
            Map<String, Object> attributes = feature.getAttributes();
            Geometry geometry = feature.getGeometry();
            Set<String> set = attributes.keySet();
            int i = 0;
            String name = null;
            for (String key : set) {
                if (i == 6) {
                    name = String.valueOf(attributes.get(key));
                }
                i++;
            }
            Point point1;
            if (geometry instanceof Point) {
                point1 = (Point) geometry;
                search_Data.put(name, (Point) geometry);
                names.add(name);
                if (type == 1) {
                    PictureMarkerSymbol pinStarBlueSymbol = new PictureMarkerSymbol(
                            this, getResources().getDrawable(
                            R.mipmap.dian));
                    Graphic pinStarBlueGraphic = new Graphic(point1, pinStarBlueSymbol);
                    mGraphicsLayer.addGraphic(pinStarBlueGraphic);
                }
            } else {
                return;
            }
        }
        if (type == 0) {
            addressAdapter.setSuggestResults(names);
        } else {
            rv_query_list.setVisibility(View.VISIBLE);
            bootomAddressAdapter.setData(names, search_Data);
            poi_searchView.clearFocus();
        }
    }

    @Override
    public void search_Fail(int type, String text) {
        dimiss();
        ToastUtility.showToast(text);
    }

    SimpleLineSymbol segmentHider = new SimpleLineSymbol(Color.RED, 5);
    SimpleLineSymbol navHider = new SimpleLineSymbol(Color.GREEN, 5);

    @Override
    public void queryDirections_Success(RouteResult mResults, final Point startPoint, final Point endPoint) {
        this.endPoint = endPoint;
        dimiss();
        mGraphicsLayer.removeAll();
        hiddenSegmentsLayer.removeAll();
        rv_query_list.setVisibility(View.GONE);
        bootomAddressAdapter.setData(null, null);
        Route curRoute = mResults.getRoutes().get(0);
        SimpleLineSymbol routeSymbol = new SimpleLineSymbol(Color.BLUE, 3);
        PictureMarkerSymbol startd = new PictureMarkerSymbol(
                this, getResources().getDrawable(
                R.mipmap.startd));
        PictureMarkerSymbol destinationSymbol = new PictureMarkerSymbol(
                this, getResources().getDrawable(
                R.mipmap.dian));
        List<RouteDirection> routeDirections = curRoute.getRoutingDirections();
        for (RouteDirection rd : routeDirections) {
            HashMap<String, Object> attribs = new HashMap<String, Object>();
            attribs.put("text", rd.getText());
            attribs.put("time", Double.valueOf(rd.getMinutes()));
            attribs.put("length", Double.valueOf(rd.getLength()));

            Graphic routeGraphic = new Graphic(rd.getGeometry(), segmentHider,
                    attribs);
            hiddenSegmentsLayer.addGraphic(routeGraphic);
        }
        //  2019/11/13 路线展示
        rouceListInclude.setData(curRoute, new MainRouceListInclude.OnClickStartNai() {
            @Override
            public void start() {
                showLoading();
                mPresenter.navigation(startPoint, endPoint, stopName);
            }
        });

        Graphic routeGraphic = new Graphic(curRoute.getRouteGraphic()
                .getGeometry(), routeSymbol);
        Graphic start = new Graphic(
                ((Polyline) routeGraphic.getGeometry()).getPoint(0), startd);
        Graphic endGraphic = new Graphic(
                ((Polyline) routeGraphic.getGeometry()).getPoint(((Polyline) routeGraphic
                        .getGeometry()).getPointCount() - 1), destinationSymbol);
        mGraphicsLayer.addGraphics(new Graphic[]{start, routeGraphic, endGraphic});
        mMapView.setExtent(curRoute.getEnvelope(), 250);
    }

    @Override
    public void queryDirections_Fail(String msg) {
        dimiss();
        ToastUtility.showToast(msg);
    }

    @Override
    public void navigation_fail(String msg) {
        // TODO: 2019/11/13 导航失败  除第一次外不做它改
        dimiss();
        ToastUtility.showToast(msg);
    }

    private int nav_id = -1;

    @Override
    public void navigation_success(Route route, String msg) {
        dimiss();
        // TODO: 2019/11/13 开始语音播报 百度tts引入 初始化流程定制
        isNai = true;//防止开启的时候同时播报 故在此开启定位定时导航
        if (nav_id != -1) {
            hiddenSegmentsLayer.removeGraphic(nav_id);
        }
        poi_searchView.setVisibility(View.GONE);
        List<RouteDirection> routeDirections = route.getRoutingDirections();
        for (RouteDirection rd : routeDirections) {
            HashMap<String, Object> attribs = new HashMap<String, Object>();
            attribs.put("text", rd.getText());
            Graphic routeGraphic = new Graphic(rd.getGeometry(), navHider,
                    attribs);
            nav_id = hiddenSegmentsLayer.addGraphic(routeGraphic);
        }
        naviceStatusInclude.setData(msg);
        bdtts.speak(msg);
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        addressAdapter.setSuggestResults(null);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(poi_searchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
        }
        if (!TextUtils.isEmpty(s)) {
            showLoading();
            mPresenter.search(1, s);
            rouceListInclude.clean();
        } else {
            ToastUtility.showToast("请输入搜索词");
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (!isSearch) {
            return true;
        }
        addressAdapter.setSuggestResults(null);
        if (TextUtils.isEmpty(s)) {
            return true;
        }
        mPresenter.search(0, s);
        rouceListInclude.clean();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_CODE_LOCATION://刚才的识别码
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    location();//开始定位
                } else {
                    ToastUtility.showToast("未开启定位权限,请手动到设置去开启权限");
                }
                break;
            case 123:
//                bdtts.init(getContext(), new Handler());//暂时不接收信息
                break;
            default:
                break;
        }
    }

    protected void onResume() {
        super.onResume();
        mMapView.unpause();
        locationDisplayManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
        locationDisplayManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationDisplayManager.stop();
    }
}
