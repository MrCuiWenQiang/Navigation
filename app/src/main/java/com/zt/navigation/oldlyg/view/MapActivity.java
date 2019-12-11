package com.zt.navigation.oldlyg.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Point;
import com.zt.navigation.oldlyg.MyApplication;
import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.MapContract;
import com.zt.navigation.oldlyg.presenter.MapPresenter;

import java.util.ArrayList;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;
import cn.faker.repaymodel.util.ToastUtility;

public class MapActivity extends BaseMVPAcivity<MapContract.View, MapPresenter> implements MapContract.View, View.OnClickListener {
    private final int PERMISSIONS_CODE_LOCATION = 200;

    private MapView mMapView;
    private TextView tv_search;

    private GraphicsLayer mGraphicsLayer;
    private GraphicsLayer hiddenSegmentsLayer;


    private boolean isOne = false;//第一次进入 定位缩放自身

    private double lat;
    private double lon;
    private LocationDisplayManager locationDisplayManager;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_map;
    }

    @Override
    protected void initContentView() {
        isShowBackButton(false);
        isShowToolView(false);
        changStatusIconCollor(false);
        mMapView = (MapView) findViewById(R.id.mapview);
        tv_search = findViewById(R.id.tv_search);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        initMap();
        initPermission();
    }



    private void initMap() {
        ArcGISDynamicMapServiceLayer arcGISTiledMapServiceLayer = new ArcGISDynamicMapServiceLayer(Urls.mapUrl);
        mMapView.addLayer(arcGISTiledMapServiceLayer);
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


    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_CODE_LOCATION);
        } else {
            location();
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_search.setOnClickListener(this);
    }


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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search: {
                toAcitvity(SearchActivity.class);
                break;
            }
        }
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
            default:
                break;
        }
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
        locationDisplayManager.stop();
    }


}
