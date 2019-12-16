package com.zt.navigation.oldlyg.presenter;


import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.CarInfoContract;
import com.zt.navigation.oldlyg.model.webbean.CarInfoBean;

import java.util.HashMap;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;

public class CarInfoPresenter extends BaseMVPPresenter<CarInfoContract.View> implements CarInfoContract.Presenter {

    @Override
    public void loadInfo(String id) {
        HashMap map = new HashMap<String, String>();
        map.put("id", id);
        HttpHelper.get(Urls.CARINFO, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                CarInfoBean carInfoBean = JsonUtil.convertJsonToObject(data,CarInfoBean.class);
            }

            @Override
            public void onFailed(int status, String message) {

            }
        });
    }
}
