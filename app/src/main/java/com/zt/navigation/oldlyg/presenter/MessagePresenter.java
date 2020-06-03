package com.zt.navigation.oldlyg.presenter;


import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.MessageContract;
import com.zt.navigation.oldlyg.model.webbean.MessageBean;
import com.zt.navigation.oldlyg.util.TokenManager;

import java.util.HashMap;
import java.util.List;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;

public class MessagePresenter extends BaseMVPPresenter<MessageContract.View> implements MessageContract.Presenter {


    @Override
    public void loadData() {
        HashMap map = new HashMap<String, String>();
        map.put("type", TokenManager.getUserId());
        HttpHelper.get(Urls.MESSAGEHISTORY, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                List<MessageBean> mes = JsonUtil.fromList(data,MessageBean.class);
                if (mes!=null){
                    getView().loadDataSuccess(mes);
                }else {
                    getView().loadDataFail("暂无数据");
                }
            }

            @Override
            public void onFailed(int status, String message) {
                getView().loadDataFail(message);
            }
        });
    }
}
