package com.zt.navigation.oldlyg.presenter;


import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.CarInfoContract;
import com.zt.navigation.oldlyg.model.webbean.CarInfoBean;
import com.zt.navigation.oldlyg.util.TokenManager;

import java.util.HashMap;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;

public class CarInfoPresenter extends BaseMVPPresenter<CarInfoContract.View> implements CarInfoContract.Presenter {

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
}
