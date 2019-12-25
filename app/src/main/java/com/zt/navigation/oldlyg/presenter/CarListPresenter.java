package com.zt.navigation.oldlyg.presenter;


import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.CarListContract;
import com.zt.navigation.oldlyg.model.webbean.CarListBean;

import java.util.HashMap;
import java.util.List;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;

public class CarListPresenter extends BaseMVPPresenter<CarListContract.View> implements CarListContract.Presenter {

    @Override
    public void loadData(String token, String userId, String userType) {
        HashMap map = new HashMap<String, String>();
        map.put("token", token);
        map.put("userId", userId);
        map.put("userType", userType);
        HttpHelper.get(Urls.CARLIST, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                List<CarListBean> cars = JsonUtil.fromList(data, CarListBean.class);
                if (cars != null && !cars.isEmpty()&&getView()!=null) {
                        getView().loadData_success(cars);
                } else {
                    onFailed(0, "暂无数据");
                }
            }

            @Override
            public void onFailed(int status, String message) {
                if (getView()!=null) {
                    getView().loadData_fail(status,message);
                }
            }
        });
    }
}
