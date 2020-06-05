package com.zt.navigation.oldlyg.util;

import cn.faker.repaymodel.util.UUIDUtil;

public class NominateUtil {

    private static final String SIGN = "*-*.";

    public static String contName(String name){
        return name+SIGN+UUIDUtil.getUUID();
    }

    public static String tofindName(String txt){
        String newTxt = null;
        int index = txt.indexOf(SIGN);
        if (index>0){
            newTxt = txt.substring(0,index);
        }else {
            newTxt = txt;
        }
        return newTxt;
    }

}
