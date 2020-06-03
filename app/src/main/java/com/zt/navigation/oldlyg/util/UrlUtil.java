package com.zt.navigation.oldlyg.util;

import android.text.TextUtils;

import com.zt.navigation.oldlyg.Urls;

import cn.faker.repaymodel.util.PreferencesUtility;

public class UrlUtil {
    private static String gisUrl = null;
    private static String netUrl = null;

    private static final String KEY_URL_NET = "KEY_URL_NET";
    private static final String KEY_URL_GIS = "KEY_URL_GIS";

    public static String getGisUrl() {
        if (TextUtils.isEmpty(gisUrl)) {
            gisUrl = PreferencesUtility.getPreferencesAsString(KEY_URL_GIS);
            if (TextUtils.isEmpty(gisUrl)) {
                gisUrl = Urls.baseMapUrl;
            }
        }
        return gisUrl;
    }

    public static String getNetUrl() {
        if (TextUtils.isEmpty(netUrl)) {
            netUrl = PreferencesUtility.getPreferencesAsString(KEY_URL_NET);
            if (TextUtils.isEmpty(netUrl)) {
                netUrl = Urls.URL;
            }
        }
        return netUrl;
    }


    public static void setGisUrl(String gisUrl) {
        UrlUtil.gisUrl = gisUrl;
        PreferencesUtility.setPreferencesField(KEY_URL_GIS, gisUrl);
    }

    public static void setNetUrl(String netUrl) {
        UrlUtil.netUrl = netUrl;
        PreferencesUtility.setPreferencesField(KEY_URL_NET, netUrl);
    }

    public static String getCarNavi(){
        String naViurl =null;
        if (AppSettingUtil.getCatType()){
            naViurl = Urls.maxNaviUrl;
        }else {
            naViurl = Urls.minNaviUrl;
        }
        return naViurl;
    }
}
