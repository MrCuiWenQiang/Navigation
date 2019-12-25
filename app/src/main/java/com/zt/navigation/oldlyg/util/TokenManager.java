package com.zt.navigation.oldlyg.util;

import android.text.TextUtils;

import com.zt.navigation.oldlyg.model.webbean.LoginBean;

import cn.faker.repaymodel.util.PreferencesUtility;

public class TokenManager {
    public static String token = "1";
    private static String userId;
    private static final String USERIDKEY = "USERIDKEY";

    public static String getUserId() {
        if (TextUtils.isEmpty(userId)) {
            userId = PreferencesUtility.getPreferencesAsString(USERIDKEY);
        }
        return userId;
    }

    public static void saveValue(LoginBean data) {
        PreferencesUtility.setPreferencesField(USERIDKEY, data.getUSERID());
    }
}
