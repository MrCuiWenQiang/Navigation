package com.zt.navigation.oldlyg.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.tts.client.SpeechError;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.zt.navigation.oldlyg.MyApplication;
import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.MapContract;
import com.zt.navigation.oldlyg.model.bean.UserBean;
import com.zt.navigation.oldlyg.model.webbean.LoginBean;
import com.zt.navigation.oldlyg.presenter.MapPresenter;
import com.zt.navigation.oldlyg.tts.BDTTS;
import com.zt.navigation.oldlyg.tts.listener.BDListener;
import com.zt.navigation.oldlyg.util.AppSettingUtil;
import com.zt.navigation.oldlyg.util.MapUtil;
import com.zt.navigation.oldlyg.util.TokenManager;
import com.zt.navigation.oldlyg.view.deploy.IntentKey;
import com.zt.navigation.oldlyg.view.service.BroadcastService;

import java.util.ArrayList;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;
import cn.faker.repaymodel.util.LogUtil;
import cn.faker.repaymodel.util.ToastUtility;
import cn.faker.repaymodel.zxing.activity.CaptureActivity;

public class MapActivity extends BaseMVPAcivity<MapContract.View, MapPresenter> implements MapContract.View, View.OnClickListener {
    private final int PERMISSIONS_CODE_LOCATION = 200;
    private final int REQUEST_CODE_SCAN = 300;
    private final int REQUEST_CODE_NFC_SCAN = 320;

    private MapView mMapView;
    private TextView tv_search;

    private GraphicsLayer mGraphicsLayer;
    private GraphicsLayer hiddenSegmentsLayer;

    private LinearLayout ll_setting, ll_help, ll_listen,ll_message;
    private LinearLayout ll_cat, ll_scan;
    private LinearLayout ll_add, ll_subtract;
    private LinearLayout ll_local;
    private LinearLayout ll_task;


    private BroadcastReceiver receiver;

    private boolean isOne = false;//第一次进入 定位缩放自身
    private int layerIndex = -1;
    private int layerTopIndex = -1;
    private boolean mapType;

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
        ll_add = findViewById(R.id.ll_add);
        ll_subtract = findViewById(R.id.ll_subtract);
        ll_local = findViewById(R.id.ll_local);
        ll_setting = findViewById(R.id.ll_setting);
        ll_help = findViewById(R.id.ll_help);
        ll_listen = findViewById(R.id.ll_listen);
        ll_message = findViewById(R.id.ll_message);
        ll_cat = findViewById(R.id.ll_cat);
        ll_scan = findViewById(R.id.ll_scan);
//        ll_task_help = findViewById(R.id.ll_task_help);
        ll_task = findViewById(R.id.ll_task);

    }


    @Override
    public void initData(Bundle savedInstanceState) {
        if (getIntent() != null) {
            Intent it = getIntent();
            String carid = it.getStringExtra(IntentKey.KRY_CARNO);

            if (TextUtils.isEmpty(carid)) {
                if (TextUtils.isEmpty(TokenManager.getUserId())) {
                    showDialog("请传递车牌号参数", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                }
            } else {
                LoginBean userBean = new LoginBean();
                userBean.setUSERID(carid);
                TokenManager.saveValue(userBean);
            }
        }
        if (!TextUtils.isEmpty(TokenManager.getUserId())) {
            Intent intent = new Intent(this, BroadcastService.class);
            intent.putExtra(BroadcastService.KEY_BROAD, TokenManager.getUserId());
            bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {

                }
            }, Context.BIND_AUTO_CREATE);
        }

        initMap();
        initPermission();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean value = intent.getBooleanExtra("MAPTYPE", false);
                if (layerIndex < 0) return;
                if (value == mapType) return;

                mMapView.removeLayer(layerIndex);
                if (layerTopIndex!=-1){
                    mMapView.removeLayer(layerTopIndex);
                }
                selectMap();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action));
        registerReceiver(receiver, filter);
        mPresenter.toGetHinder();
        mPresenter.queryhighWay();

    }


    private void initMap() {
        selectMap();
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

    private void selectMap() {
        if (mapType = AppSettingUtil.getMapType()) {
            ArcGISLocalTiledLayer arcGISLocalTiledLayer = new ArcGISLocalTiledLayer("file://" + mPresenter.getPath());
            ArcGISLocalTiledLayer topGISTiledMapServiceLayer = new ArcGISLocalTiledLayer("file://" + mPresenter.getTDTPath());
            layerTopIndex = mMapView.addLayer(topGISTiledMapServiceLayer);
            layerIndex = mMapView.addLayer(arcGISLocalTiledLayer);
        } else {
            ArcGISDynamicMapServiceLayer arcGISTiledMapServiceLayer = new ArcGISDynamicMapServiceLayer(Urls.mapUrl);
            ArcGISDynamicMapServiceLayer topGISTiledMapServiceLayer = new ArcGISDynamicMapServiceLayer(Urls.topMapUrl);
            layerTopIndex = mMapView.addLayer(topGISTiledMapServiceLayer);

            layerIndex = mMapView.addLayer(arcGISTiledMapServiceLayer);
        }
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
                Manifest.permission.CAMERA
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
            mPresenter.mapFile(getContext());
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
        ll_add.setOnClickListener(this);
        ll_subtract.setOnClickListener(this);
        ll_local.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_help.setOnClickListener(this);
        ll_listen.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_cat.setOnClickListener(this);
        ll_scan.setOnClickListener(this);
//        ll_task_help.setOnClickListener(this);
        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float x, float y) {
                Point point = mMapView.toMapPoint(new Point(x,y));
                LogUtil.e("ss","------"+point.getY()+","+point.getX());
            }


        });
        /*Envelope envelope = new Envelope();
        envelope.setXMax(119.52);
        envelope.setYMax(34.645);
        envelope.setXMin(119.273);
        envelope.setYMin(34.645);*/

/*        Polygon polygon = new Polygon();
        polygon.startPath(119.273,34.645);
        polygon.lineTo(119.52,34.784);
        polygon.lineTo(119.52,34.645);
        polygon.lineTo(119.273,34.784);
        polygon.lineTo(119.273,34.645);
        mMapView.setExtent(polygon);*/
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
                        mMapView.zoomTo(new Point(lon, lat), 250);
                        isOne = false;
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

    private String[] selectItems = new String[]{"二维码扫描", "NFC扫描"};

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_search) {
            toAcitvity(SearchActivity.class);
        } else if (id == R.id.ll_add) {
            mMapView.zoomin();
        } else if (id == R.id.ll_local) {
            if (MyApplication.startPoint != null) {
                mMapView.zoomTo(MyApplication.startPoint, 10);
            } else {
                ToastUtility.showToast("请检查定位开关和权限是否打开!");
            }
        } else if (id == R.id.ll_subtract) {
            mMapView.zoomout();
        } else if (id == R.id.ll_setting) {
            toAcitvity(SettingActivity.class);
        } else if (id == R.id.ll_help) {
        } else if (id == R.id.ll_listen) {
            toAcitvity(FeedbackActivity.class);
        }else if (id == R.id.ll_message) {
            toAcitvity(MessageAcctivity.class);
        } else if (id == R.id.ll_cat) {
            toAcitvity(CarListActivity.class);
        } else if (id == R.id.ll_scan) {
            showListDialog(selectItems, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0: {
                            Intent intent = new Intent(getContext(), CaptureActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_SCAN);
                            dialogInterface.dismiss();
                            break;
                        }
                        case 1: {
                            PackageManager packageManager = getPackageManager();

                            boolean b1 = packageManager
                                    .hasSystemFeature(PackageManager.FEATURE_NFC);
                            if (b1){
                                Intent intent = new Intent(getContext(), NFCActivity.class);
                                startActivityForResult(intent, REQUEST_CODE_NFC_SCAN);
                            }else {
                                ToastUtility.showToast("该手机不支持NFC");
                            }
                            dialogInterface.dismiss();
                            break;
                        }
                    }
                }
            });
        }

       /* switch (v.getId()) {
            case R.id.tv_search: {
                toAcitvity(SearchActivity.class);
                break;
            }
            case R.id.ll_add: {
                mMapView.zoomin();
                break;
            }
            case R.id.ll_local: {
                if (MyApplication.startPoint != null) {
                    mMapView.zoomTo(MyApplication.startPoint, 10);
                } else {
                    ToastUtility.showToast("请检查定位开关和权限是否打开!");
                }
                break;
            }
            case R.id.ll_subtract: {
                mMapView.zoomout();
                break;
            }
            case R.id.ll_setting: {
                toAcitvity(SettingActivity.class);
                break;
            }
            case R.id.ll_help: {
                break;
            }
            case R.id.ll_listen: {
                toAcitvity(FeedbackActivity.class);
                break;
            }
            case R.id.ll_cat: {
                toAcitvity(CarListActivity.class);
                break;
            }
            case R.id.ll_scan: {
                Intent intent = new Intent(getContext(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                break;
            }
            case R.id.ll_task_help: {
                break;
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SCAN: { //扫描结果
                if (resultCode == CaptureActivity.CAPTURE_SCAN_CODE) {
                    String code = data.getStringExtra(CaptureActivity.CAPTURE_SCAN_RESULT);
                    Intent intent = new Intent(getContext(), CarListActivity.class);
                    intent.putExtra(CarListActivity.CAR_NO, code);
                    startActivity(intent);
                }
            }
            case REQUEST_CODE_NFC_SCAN: { //NFC扫描结果
                if (resultCode == NFCActivity.CAPTURE_SCAN_CODE) {
                    String code = data.getStringExtra(NFCActivity.NFC_SCAN_RESULT);
                    Intent intent = new Intent(getContext(), CarListActivity.class);
                    intent.putExtra(CarListActivity.CAR_NO, code);
                    startActivity(intent);
                }
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
            case 123://刚才的识别码
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.mapFile(getContext());
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
        if (receiver != null) {
            unregisterReceiver(receiver);
        }

    }


    @Override
    public void showStartMapFile(String name) {
        showLoading();
    }

    @Override
    public void showEndMapFile(String name) {
        dimiss();
        showDialog(name);
    }





    @Override
    public void toGetHinder_Success(String msg) {
        BDTTS bdtts = new BDTTS();
        bdtts.init(getContext(), new Handler(), new BDListener() {
            @Override
            public void onSpeechFinish(String s) {
                super.onSpeechFinish(s);
                bdtts.releas();
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                super.onError(s, speechError);
                bdtts.releas();
            }
        });
        bdtts.speak(msg);
    }



}
