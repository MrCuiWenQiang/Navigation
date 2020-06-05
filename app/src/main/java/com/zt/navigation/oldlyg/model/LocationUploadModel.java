package com.zt.navigation.oldlyg.model;

import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.model.bean.LocationBean;
import com.zt.navigation.oldlyg.util.TokenManager;

import java.util.HashMap;
import java.util.List;

import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;

/**
 * 定位上传
 */
public class LocationUploadModel {
    private boolean isRun = false;
    public void upload(String token, double longitude, double latitude,String address) {
        if(isRun){
            return;
        }
        isRun = true;
        LocationBean locationBean = new LocationBean(TokenManager.getUserId(),String.valueOf(longitude),String.valueOf(latitude),address);

        HttpHelper.post(Urls.UPDATELOCATION+"?token="+TokenManager.token, locationBean, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                isRun = false;
            }

            @Override
            public void onFailed(int status, String message) {
                isRun = false;
            }
        });
    }

}
