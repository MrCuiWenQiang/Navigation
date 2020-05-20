package com.zt.navigation.oldlyg.presenter;


import android.text.TextUtils;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.ags.find.FindParameters;
import com.esri.core.tasks.ags.find.FindResult;
import com.esri.core.tasks.ags.find.FindTask;
import com.esri.core.tasks.query.QueryTask;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.CarInfoContract;
import com.zt.navigation.oldlyg.model.webbean.CarInfoBean;
import com.zt.navigation.oldlyg.task.AsyncQueryTask;
import com.zt.navigation.oldlyg.util.TokenManager;
import com.zt.navigation.oldlyg.view.SearchActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;
import cn.faker.repaymodel.util.db.DBThreadHelper;

public class CarInfoPresenter extends BaseMVPPresenter<CarInfoContract.View> implements CarInfoContract.Presenter {

    String[] fields = new String[]{"GKZYQDM"};
    //    String condition ;
    int[] layerIds = new int[]{64};

    @Override
    public void loadInfo(String id) {
        HashMap map = new HashMap<String, String>();
        map.put("DispatchId", id);
        map.put("token", TokenManager.token);
        HttpHelper.get(Urls.CARINFO, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                CarInfoBean carInfoBean = JsonUtil.convertJsonToObject(data, CarInfoBean.class);
                if (carInfoBean == null) {
                    onFailed(0, "暂时未查询到详情");
                } else {
                    getView().info_Success(carInfoBean);
                }
            }

            @Override
            public void onFailed(int status, String message) {
                getView().info_Fail(message);
            }
        });
    }

/*   @Override
    public void searchAddress(String name, String code) {
        if (TextUtils.isEmpty(code)) {
            getView().searchAddresss_Fail(0, "请输入地点");
            return;
        }
        final FindTask findTask = new FindTask(Urls.searchUrl);
        final FindParameters findParameters = new FindParameters();
        findParameters.setLayerIds(layerIds);
        findParameters.setReturnGeometry(true); //允许返回几何图形
        findParameters.setSearchText(code); // 设置查询关键字--必须设置
        findParameters.setSearchFields(fields); // 设置查询字段的名称
        DBThreadHelper.startThreadInPool(new DBThreadHelper.ThreadCallback<List<FindResult>>() {

            @Override
            protected List<FindResult> jobContent() throws Exception {
                return findTask.execute(findParameters);
            }

            @Override
            protected void jobEnd(List<FindResult> findResults) {
                if (findResults != null) {
                    if (findResults.size()>0) {
                        FindResult result = findResults.get(0);
                        if (result.getGeometry() instanceof  Point){
                            Point point = (Point) result.getGeometry();
                            getView().searchAddresss_Success(1,name,point);
                        }else  if (result.getGeometry() instanceof  Point){

                        }else {
                            getView().searchAddresss_Fail(1, "位置点不是标准数据");
                        }
                    } else {
                        getView().searchAddresss_Fail(1, "未查询到数据");
                    }
                } else {
//                    getView().search_Fail(type, "查询失败");
                    getView().searchAddresss_Fail(1, "未查询到数据");
                }
            }
        });

    }*/

    public void searchAddress(String name, String code) {

        AsyncQueryTask asyncQueryTask = new AsyncQueryTask();
//        asyncQueryTask.execute(Urls.searchUrl + "/19", null, "name ='" + "4号磅" + "' AND GSBM  = '" + "191" + "'");
        asyncQueryTask.execute(Urls.searchUrl + "/64", null, "UNAME ='" + name + "' AND GKZYQDM  = '" + code + "'");
        asyncQueryTask.setOnReturnDataListener(new AsyncQueryTask.OnReturnDataListener() {
            @Override
            public void onReturnData(FeatureResult result) {
                if (result != null) {
                    Iterator<Object> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        Feature feature = (Feature) iterator.next();
                        Geometry geometry = feature.getGeometry();
                  /*      if (geometry instanceof MultiPath){
                            MultiPath data = (MultiPath) geometry;
                            if (data.getPointCount()<=0){
                                getView().searchAddresss_Fail(1, "该地点未有坐标");
                                return;
                            }
                            geometry  =data.getPoint(0);
                        }*/
                        getView().searchAddresss_Success(1, name, geometry);
                    } else {
                        getView().searchAddresss_Fail(1, "未查询到数据");
                    }
                } else {
                    getView().searchAddresss_Fail(1, "未查询到数据");
                }
            }
        });
    }

}
