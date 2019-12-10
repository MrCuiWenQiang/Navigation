package com.zt.navigation.oldlyg;

import com.esri.core.geometry.Point;

import cn.faker.repaymodel.BasicApplication;
import cn.faker.repaymodel.util.LogUtil;
import cn.faker.repaymodel.util.PreferencesUtility;
import cn.faker.repaymodel.util.ToastUtility;

public class MyApplication extends BasicApplication {

    public static Point startPoint;
    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtility.setToast(getApplicationContext());
        LogUtil.isShow = true;
        PreferencesUtility.setPreferencesUtility(getApplicationContext(),"Navigation");
    }

}
