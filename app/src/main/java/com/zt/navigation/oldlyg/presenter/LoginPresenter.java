package com.zt.navigation.oldlyg.presenter;


import android.text.TextUtils;

import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.LoginContract;
import com.zt.navigation.oldlyg.model.bean.UserBean;
import com.zt.navigation.oldlyg.model.webbean.LoginBean;
import com.zt.navigation.oldlyg.util.TokenManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;
import cn.faker.repaymodel.util.PreferencesUtility;

public class LoginPresenter extends BaseMVPPresenter<LoginContract.View> implements LoginContract.Presenter {
    private final String CARNO = "CARNO";
    private final String CARHEAD = "CARHEAD";
    public static final String USERLIST = "USERLIST";

    @Override
    public void attachView(LoginContract.View view) {
        super.attachView(view);
        String no = PreferencesUtility.getPreferencesAsString(CARNO);
        String carHead = PreferencesUtility.getPreferencesAsString(CARHEAD);
        if (!TextUtils.isEmpty(no)) {
            getView().init_data(carHead, no);
        }
    }

    @Override
    public void login(String carHead, String carNo) {
        if (TextUtils.isEmpty(carNo)) {
            getView().login_Fail("请填写车牌号+颜色（例如苏Z000003黄）");
            return;
        }
        HashMap map = new HashMap<String, String>();
        String user = carHead + carNo;
        map.put("user", user);
        map.put("token", TokenManager.token);
        HttpHelper.get(Urls.LOGIN, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                List<LoginBean> loginBeans = JsonUtil.fromList(data, LoginBean.class);
                if (loginBeans.size() > 0) {
                    LoginBean bean = loginBeans.get(0);
                    getView().login_Success("登录成功");
                    PreferencesUtility.setPreferencesField(CARNO, carNo);
                    PreferencesUtility.setPreferencesField(CARHEAD, carHead);
                    PreferencesUtility.setPreferencesField(CARHEAD, carHead);

                    String userJson = PreferencesUtility.getPreferencesAsString(USERLIST);
                    List<UserBean> userBeans = JsonUtil.fromList(userJson, UserBean.class);
                    if (userBeans == null) {
                        userBeans = new ArrayList<>();
                    }
                    if (userBeans.size() > 0) {
                        int length = userBeans.size();
                        for (int i = 0; i < length; i++) {
                            UserBean item = userBeans.get(i);
                            if (item.getCarNo().equals(user)) {
                                userBeans.remove(item);
                                --i;
                                --length;
                            }
                        }
                    }
                    userBeans.add(new UserBean(user));
                    PreferencesUtility.setPreferencesField(USERLIST, JsonUtil.convertObjectToJson(userBeans));

                    TokenManager.saveValue(bean);
                } else {
                    onFailed(-1, "登录失败:后台数据未返回成功");
                }

            }

            @Override
            public void onFailed(int status, String message) {
                getView().login_Fail(message);
            }
        });
    }
}
