package com.zt.navigation.oldlyg.presenter;


import android.text.TextUtils;

import com.esri.core.tasks.ags.find.FindParameters;
import com.esri.core.tasks.ags.find.FindResult;
import com.esri.core.tasks.ags.find.FindTask;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.CarInfoContract;
import com.zt.navigation.oldlyg.model.webbean.CarInfoBean;
import com.zt.navigation.oldlyg.util.TokenManager;
import com.zt.navigation.oldlyg.view.SearchActivity;

import java.util.HashMap;
import java.util.List;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;
import cn.faker.repaymodel.util.db.DBThreadHelper;

public class CarInfoPresenter extends BaseMVPPresenter<CarInfoContract.View> implements CarInfoContract.Presenter {

    String[] fields = new String[]{"name"};
    //    String condition ;
    int[] layerIds = new int[]{19, 20,21,22,29,33,34,36,37,38,39,64};

    @Override
    public void loadInfo(String id) {
        HashMap map = new HashMap<String, String>();
        map.put("DispatchId", id);
        map.put("token", TokenManager.token);
        HttpHelper.get(Urls.CARINFO, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                CarInfoBean carInfoBean = JsonUtil.convertJsonToObject(data,CarInfoBean.class);
                if (carInfoBean==null){
                    onFailed(0,"暂时未查询到详情");
                }else {
                    getView().info_Success(carInfoBean);
                }
            }

            @Override
            public void onFailed(int status, String message) {
                getView().info_Fail(message);
            }
        });
    }

    // TODO: 2020/4/26 此处代码冗余 应考虑标准mvp的model
    @Override
    public void searchAddresss(final int type, String text) {
        if (TextUtils.isEmpty(text)) {
            getView().searchAddresss_Fail(type, "请输入地点");
            return;
        }
        final FindTask findTask = new FindTask(Urls.searchUrl);
        final FindParameters findParameters = new FindParameters();
        findParameters.setLayerIds(layerIds);
        findParameters.setReturnGeometry(true); //允许返回几何图形
        findParameters.setSearchText(text); // 设置查询关键字--必须设置
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
                        getView().searchAddresss_Success(type, findResults.get(0));
                    } else {
                        getView().searchAddresss_Fail(type, "未查询到数据");
                    }
                } else {
//                    getView().search_Fail(type, "查询失败");
                    getView().searchAddresss_Fail(type, "未查询到数据");
                }
            }
        });

    }



}
