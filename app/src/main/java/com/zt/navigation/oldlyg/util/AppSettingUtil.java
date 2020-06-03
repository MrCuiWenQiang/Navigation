package com.zt.navigation.oldlyg.util;

import android.content.Context;
import android.content.SharedPreferences;

import cn.faker.repaymodel.util.PreferencesUtility;

/**
 * app常用设置值
 */
public class AppSettingUtil {
    private static final String CATTYPE = "cattype";
    private static final String MAPTYPE = "maptype";

    public static boolean getCatType() {
        return PreferencesUtility.getPreferencesAsBoolean(CATTYPE,true);
    }

    /**
     * fale为小车   true 为大车 默认大车
     * @param value
     */
    public static void setCatType(boolean value) {
        PreferencesUtility.setPreferencesField(CATTYPE, value);
    }

    public static boolean getMapType() {
        return PreferencesUtility.getPreferencesAsBoolean(MAPTYPE);
    }

    /**
     * fale为在线   true 为离线 默认在线
     * @param value
     */
    public static void setMapType(boolean value) {
        PreferencesUtility.setPreferencesField(MAPTYPE, value);
    }
}
