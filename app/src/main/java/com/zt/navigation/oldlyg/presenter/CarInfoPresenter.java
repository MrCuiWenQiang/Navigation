package com.zt.navigation.oldlyg.presenter;


import android.nfc.tech.NfcA;
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

import java.util.ArrayList;
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



    public void searchAddress(List<String> names, List<String> codes) {

        AsyncQueryTask asyncQueryTask = new AsyncQueryTask();
        asyncQueryTask.execute(Urls.searchUrl + "/19", null, splitSQL(names, codes));
        asyncQueryTask.setOnReturnDataListener(new AsyncQueryTask.OnReturnDataListener() {
            @Override
            public void onReturnData(FeatureResult result) {
                if (result != null) {
                    Iterator<Object> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        List<Geometry> geometrys = new ArrayList<>();
                        while (iterator.hasNext()) {
                            Feature feature = (Feature) iterator.next();
                            Geometry geometry = feature.getGeometry();
                            geometrys.add(geometry);
                        }
                        getView().searchAddresss_Success(1, names, geometrys);
                    } else {
                        getView().searchAddresss_Fail(1, "未查询到数据");
                    }
                } else {
                    getView().searchAddresss_Fail(1, "未查询到数据");
                }
            }
        });
    }

//    String sql = "UNAME ='$name' AND GKZYQDM  = '$code'";
    String sql = "NAME ='$name' AND GSBM  = '$code'";

    private String splitSQL(List<String> names, List<String> codes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            if (i != 0) {
                sb.append(" OR ");
            }
            String name = names.get(i);
            String code = codes.get(i);
            String o = sql.replace("$name", name);
            String t = o.replace("$code", code);
            sb.append(t);
        }
        return sb.toString();
    }
}
