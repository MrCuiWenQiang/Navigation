package com.zt.navigation.oldlyg.presenter;


import android.content.Context;

import com.esri.core.geometry.Geometry;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.MapContract;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;
import cn.faker.repaymodel.util.FileUtility;
import cn.faker.repaymodel.util.db.DBThreadHelper;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.model.LocationUploadModel;
import com.zt.navigation.oldlyg.model.webbean.CarListBean;
import com.zt.navigation.oldlyg.task.AsyncQueryTask;
import com.zt.navigation.oldlyg.util.MapUtil;
import com.zt.navigation.oldlyg.util.TokenManager;
import com.zt.navigation.oldlyg.util.TpkUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapPresenter extends BaseMVPPresenter<MapContract.View> implements MapContract.Presenter {

    private boolean isUpload = false;//是否已上报位置
    private boolean isShowUpload = false;//是否已经显示到达港区对话框

    @Override
    public void attachView(MapContract.View view) {
        super.attachView(view);
    }

    public String getPath() {
        return TpkUtil.getPath();
    }

    public void mapFile(Context context) {
        if (TpkUtil.isHaveMap()) {
            return;
        }
        getView().showStartMapFile("解压离线地图包中...");
        DBThreadHelper.startThreadInPool(new DBThreadHelper.ThreadCallback<Integer>() {
            @Override
            protected Integer jobContent() throws Exception {
                FileUtility.copyFilesFromRaw(context, R.raw.lianyungang_dxt, TpkUtil.ZIPName, TpkUtil.getFathPath());
                return TpkUtil.decode();
            }

            @Override
            protected void jobEnd(Integer o) {
                if (o == 0) {
                    getView().showEndMapFile("地图包解压完成");

                } else {
                    getView().showEndMapFile("地图包解压失败");
                }
            }
        });
    }

    private LocationUploadModel uploadModel = new LocationUploadModel();

    @Override
    public void updateLocation(double lan, double lon) {
        uploadModel.upload(TokenManager.token, lon, lan, null);
        if (!isUpload && !isShowUpload) {
            if (MapUtil.isHave(lan, lon)) {
                getView().showArrive("你已到达港区,点击上报");
                isShowUpload = true;
            }
        }
    }

    @Override
    public void uploadArrive() {
        HashMap map = new HashMap<String, String>();
        map.put("token", TokenManager.token);
        map.put("userId", TokenManager.getUserId());
        HttpHelper.get(Urls.ARRIVE, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                isUpload = true;
                getView().uploadArriveSuccess("到港请求成功");
            }

            @Override
            public void onFailed(int status, String message) {
                isUpload = false;
                isShowUpload = false;
                getView().uploadArriveFail("到港请求失败");
            }
        });
    }

    private RouteTask mRouteTask = null;

    @Override
    public void toGetHinder() {
        if (mRouteTask == null) {
            try {
                mRouteTask = RouteTask
                        .createOnlineRouteTask(
                                Urls.mapNaviUrl,
                                null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AsyncQueryTask asyncQueryTask = new AsyncQueryTask();
        asyncQueryTask.execute(Urls.searchHinderUrl, null, "OBJECTID LIKE '%%'");
        asyncQueryTask.setOnReturnDataListener(new AsyncQueryTask.OnReturnDataListener() {
            @Override
            public void onReturnData(FeatureResult result) {
                if (result != null) {
                    Iterator<Object> iterator = result.iterator();
                    StringBuffer sb;
                    if (iterator.hasNext()) {
                        sb = new StringBuffer();
                        sb.append("接下来播报附近施工路段信息:");
                        while (iterator.hasNext()) {
                            Feature feature = (Feature) iterator.next();
                            Map<String, Object> attributes = feature.getAttributes();
                            Set<String> set = attributes.keySet();
                            for (String key : set) {
                               /* if (key.equals("NAME")) {
                                    sb.append(attributes.get(key));
                                }else */  if (key.equals("DESCRIPTION")) {
                                    sb.append(attributes.get(key));
                                    sb.append("。");
                                }
                            }
                        }
                        if (getView()!=null){
                            getView().toGetHinder_Success(sb.toString());
                        }
                    }
                }
            }
        });
    }


}
