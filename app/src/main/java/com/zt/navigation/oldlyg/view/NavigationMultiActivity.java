package com.zt.navigation.oldlyg.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteDirection;
import com.esri.core.tasks.na.RouteResult;
import com.zt.navigation.oldlyg.MyApplication;
import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.NavigationMultiContract;
import com.zt.navigation.oldlyg.model.webbean.XSBean;
import com.zt.navigation.oldlyg.presenter.NavigationMultiPresenter;
import com.zt.navigation.oldlyg.tts.BDTTS;
import com.zt.navigation.oldlyg.util.AppSettingUtil;
import com.zt.navigation.oldlyg.view.adapter.StopsAdapter;
import com.zt.navigation.oldlyg.view.include.MainRouceListInclude;
import com.zt.navigation.oldlyg.view.include.NaviceStatusInclude;

import java.util.HashMap;
import java.util.List;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;
import cn.faker.repaymodel.util.ToastUtility;

/**
 * 多路线导航 用于派车单 多路线
 *
 * 思路因为多个目的地无法精确判断是否到达  提供多目的地选择
 */
public class NavigationMultiActivity extends BaseMVPAcivity<NavigationMultiContract.View, NavigationMultiPresenter> implements NavigationMultiContract.View {

    public static final String INTENT_KEY_END_NAME = "INTENT_KEY_END_NAME";
    public static final String INTENT_KEY_END = "INTENT_KEY_END";
    public static final String BUNDLE_NAME = "BUNDLE_NAME";

    private SimpleLineSymbol segmentHider = new SimpleLineSymbol(Color.RED, 5);
    SimpleLineSymbol navHider = new SimpleLineSymbol(Color.GREEN, 5);

    private MainRouceListInclude rouceListInclude = new MainRouceListInclude(this);
    private NaviceStatusInclude naviceStatusInclude = new NaviceStatusInclude(this);

    private GraphicsLayer mGraphicsLayer;
    private GraphicsLayer hiddenSegmentsLayer;

    private BDTTS bdtts = new BDTTS();

    private MapView mMapView;
    private List<String> end_name;//终点名称
    private List<Geometry> end_point;//终点
    private Geometry end_point_now;//终点
    private String end_name_now;//终点名称
    private boolean isNai;
    private int nav_id = -1;//图层id
    private TextView tv_xs;
    private RecyclerView lv_data;
    private StopsAdapter stopsAdapter;

    private LocationDisplayManager locationDisplayManager;
    private AudioManager audioManager;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_navigation_multi;
    }

    @Override
    protected void initContentView() {
        isShowBackButton(false);
        isShowToolView(false);
        changStatusIconCollor(false);

        mMapView = findViewById(R.id.mapview);
        lv_data = findViewById(R.id.lv_data);
        tv_xs = findViewById(R.id.tv_xs);
        lv_data.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        stopsAdapter = new StopsAdapter();
        lv_data.setAdapter(stopsAdapter);
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        initMap();
        initLocation();
        initAudio();

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(BUNDLE_NAME);
        end_name = bundle.getStringArrayList(INTENT_KEY_END_NAME);
        end_point = (List<Geometry>) bundle.getSerializable(INTENT_KEY_END);
        end_point_now = end_point.get(0);
        end_name_now = end_name.get(0);
        showLoading();
        stopsAdapter.setEnd_name(end_name);
        mPresenter.queryDirections(MyApplication.startPoint,end_point_now , end_name_now);//默认导航到第一个
        bdtts.init(getContext(), new Handler());//暂时不接收信息
    }

    private void initAudio() {
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume <= 0) {
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 2, AudioManager.FLAG_PLAY_SOUND);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        naviceStatusInclude.setOnClickStatu(new NaviceStatusInclude.OnClickStatu() {
            @Override
            public void onExit() {
                //关闭语音导航 定位模式；
                isNai = false;
                bdtts.stop();
                rouceListInclude.clean();
                naviceStatusInclude.off();
                mGraphicsLayer.removeAll();
                hiddenSegmentsLayer.removeAll();
                showLoading();
                lv_data.setVisibility(View.VISIBLE);
                mPresenter.queryDirections(MyApplication.startPoint,end_point_now , end_name_now);
            }
        });
        stopsAdapter.setListener(new StopsAdapter.OnItemClickListener() {
            @Override
            public void onclick(int postion) {
                end_point_now = end_point.get(postion);
                end_name_now = end_name.get(postion);
                showLoading();
                mPresenter.queryDirections(MyApplication.startPoint,end_point_now , end_name_now);//默认导航到第一个
            }
        });
    }

    private void initLocation() {
        locationDisplayManager = mMapView.getLocationDisplayManager();
        locationDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
        locationDisplayManager.setLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String bdlat = location.getLatitude() + "";
                String bdlon = location.getLongitude() + "";
                if (bdlat.indexOf("E") == -1 | bdlon.indexOf("E") == -1) {
                    //这里做个判断是因为，可能因为gps信号问题，定位出来的经纬度不正常。
                    double lat = location.getLatitude();//纬度
                    double lon = location.getLongitude();//经度
                    if (isNai) {
                        mPresenter.navigation(new Point(lon, lat), end_point_now, end_name_now);
                        mMapView.setExtent(new Point(lon, lat), 250);
                        mMapView.setScale(mMapView.getMaxScale());
                    }
                    mPresenter.updateLocation(lat, lon);
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

    private void initMap() {
        if (AppSettingUtil.getMapType()) {//地图类型暂时未动态切换
            ArcGISLocalTiledLayer arcGISLocalTiledLayer = new ArcGISLocalTiledLayer("file://" + mPresenter.getPath());
            mMapView.addLayer(arcGISLocalTiledLayer);
        } else {
            ArcGISDynamicMapServiceLayer topGISTiledMapServiceLayer = new ArcGISDynamicMapServiceLayer(Urls.topMapUrl);
            mMapView.addLayer(topGISTiledMapServiceLayer);
            ArcGISDynamicMapServiceLayer arcGISTiledMapServiceLayer = new ArcGISDynamicMapServiceLayer(Urls.mapUrl);
            mMapView.addLayer(arcGISTiledMapServiceLayer);

        }
        hiddenSegmentsLayer = new GraphicsLayer();
        mMapView.addLayer(hiddenSegmentsLayer);
        mGraphicsLayer = new GraphicsLayer();
        mMapView.addLayer(mGraphicsLayer);
    }

    @Override
    public void queryDirections_Success(RouteResult mResults, Point startPoint, Geometry endPoint) {

        hiddenSegmentsLayer.removeAll();
        mGraphicsLayer.removeAll();
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
        Graphic routeGraphic = new Graphic(curRoute.getRouteGraphic()
                .getGeometry(), routeSymbol);
        Graphic start = new Graphic(
                ((Polyline) routeGraphic.getGeometry()).getPoint(0), startd);
        Graphic endGraphic = new Graphic(
                ((Polyline) routeGraphic.getGeometry()).getPoint(((Polyline) routeGraphic
                        .getGeometry()).getPointCount() - 1), destinationSymbol);
        mGraphicsLayer.addGraphics(new Graphic[]{start, routeGraphic, endGraphic});
        mMapView.setExtent(curRoute.getEnvelope(), 250);

        //  2019/11/13 路线展示
        rouceListInclude.setData(curRoute, new MainRouceListInclude.OnClickStartNai() {
            @Override
            public void start() {

                showLoading();
                mPresenter.navigation(startPoint, end_point_now, end_name_now);
            }
        });
        dimiss();
    }

    @Override
    public void queryDirections_Fail(String msg) {
        dimiss();
        ToastUtility.showToast(msg);
    }

    @Override
    public void navigation_fail(String msg) {
        dimiss();
        ToastUtility.showToast(msg);
    }


    @Override
    public void navigation_success(Route route, String msg) {
        dimiss();
        isNai = true;//防止开启的时候同时播报 故在此开启定位定时导航
        if (nav_id != -1) {
            hiddenSegmentsLayer.removeGraphic(nav_id);
        }
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
        lv_data.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // TODO: 2020/5/21 两个导航页面都要对back key改造
        finish();
    }

    @Override
    public void navigation_arrive(double surplus) {

    }

    @Override
    public void showXS(XSBean minPoint) {
        tv_xs.setVisibility(View.VISIBLE);
        tv_xs.setText(minPoint.getContent());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP: {
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (currentVolume == 0) {
                    currentVolume = 2;
                }
                currentVolume++;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);
                return true;
            }
            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (currentVolume != 0) {
                    currentVolume--;
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onResume() {
        super.onResume();
        mMapView.unpause();
        if (locationDisplayManager != null) {
            locationDisplayManager.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
        if (locationDisplayManager != null) {
            locationDisplayManager.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.destroyDrawingCache();
        bdtts.releas();
        locationDisplayManager.stop();
    }
}
