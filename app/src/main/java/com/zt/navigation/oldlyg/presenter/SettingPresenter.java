package com.zt.navigation.oldlyg.presenter;


import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.SettingContract;
import com.zt.navigation.oldlyg.model.bean.UserBean;
import com.zt.navigation.oldlyg.model.webbean.LoginBean;
import com.zt.navigation.oldlyg.util.AppSettingUtil;
import com.zt.navigation.oldlyg.util.MapUtil;
import com.zt.navigation.oldlyg.util.TokenManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;
import cn.faker.repaymodel.util.PreferencesUtility;

public class SettingPresenter extends BaseMVPPresenter<SettingContract.View> implements SettingContract.Presenter {
    public String[] cats = new String[]{"小车路线", "大车路线"};
    public String[] maps = new String[]{"在线加载", "离线加载"};
    private String[] values;
    @Override
    public void settingCatType(int index, boolean value) {
        AppSettingUtil.setCatType(value);
        getView().settingCatType(cats[index]);
    }

    @Override
    public void settingMapType(int index, boolean value) {
        if (value != AppSettingUtil.getMapType()) {
            AppSettingUtil.setMapType(value);
        }
        getView().settingMapType(maps[index], value);
    }

    @Override
    public void showUsers() {
        if (values!=null){
            getView().showUsers(values);
            return;
        }
        String userJson = PreferencesUtility.getPreferencesAsString(LoginPresenter.USERLIST);
        List<UserBean> userBeans = JsonUtil.fromList(userJson, UserBean.class);
        List<String> us = new ArrayList<>();
        for (UserBean item :
                userBeans) {
            us.add(item.getCarNo());
        }
        values = us.toArray(new String[]{});
        getView().showUsers(values);
    }

    public void settingUser(String sv) {
        HashMap map = new HashMap<String, String>();
        map.put("user", sv);
        map.put("token", TokenManager.token);
        HttpHelper.get(Urls.LOGIN, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                List<LoginBean> loginBeans = JsonUtil.fromList(data, LoginBean.class);
                if (loginBeans.size() > 0) {
                    LoginBean bean = loginBeans.get(0);
                    getView().settingUser_Success("登录成功");
                    TokenManager.saveValue(bean);
                } else {
                    onFailed(-1, "登录失败:后台数据未返回成功");
                }

            }

            @Override
            public void onFailed(int status, String message) {
                getView().settingUser_Fail(message);
            }
        });
    }


    @Override
    public void updateLocation(double lan, double lon) {

            MapUtil.isHave(lan, lon, new HttpResponseCallback() {
                @Override
                public void onSuccess(String data) {
                    if (Boolean.valueOf(data)){
                        uploadArrive();
                    }else {
                        getView().showArrivefinal("未到达港区,不能上报");
                    }
                }

                @Override
                public void onFailed(int status, String message) {
                    getView().showArrivefinal(message);
                }
            });
    }

    @Override
    public void uploadArrive() {
        HashMap map = new HashMap<String, String>();
        map.put("token", TokenManager.token);
        map.put("userId", TokenManager.getUserId());
        HttpHelper.get(Urls.ARRIVE, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                getView().uploadArriveSuccess("到港请求成功");
            }

            @Override
            public void onFailed(int status, String message) {
                getView().uploadArriveFail("到港请求失败");
            }
        });
    }
}
