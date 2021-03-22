package com.zt.navigation.oldlyg.util;

import android.os.Environment;
import android.text.TextUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

import cn.faker.repaymodel.util.LogUtil;
import cn.faker.repaymodel.util.error.ErrorUtil;

/**
 * 移动地图包管理工具 因考虑static特殊性以及工时不够 出此下策 建立新的管理类
 */
// TODO: 2020/6/8 因为天地图离线包由本地存储变更为网络存储 故删除大量本地操作逻辑
public class TpkTDTUtil {


    //路径为 /sd卡路径
    public static final String appName = "NavigationMap";
    public static final String name = "lianyungang_tdt.tpk";

    private static String mapFilePath;
    private static String mapFileFathPath;

    /**
     * 返回tpk路径
     *
     * @return
     */
    public static String getPath() {
        if (TextUtils.isEmpty(mapFilePath)) {
            mapFilePath = Environment.getExternalStorageDirectory() + "/" + appName + "/" + name;
        }
        return mapFilePath;
    }



    /**
     * 返回文件夹路径
     *
     * @return
     */
    public static String getFathPath() {
        if (TextUtils.isEmpty(mapFileFathPath)) {
            mapFileFathPath = Environment.getExternalStorageDirectory() + "/" + appName + "/";
        }
        return mapFileFathPath;
    }



    /**
     * 检测文件是否存在
     *
     * @return
     */
    public static boolean isHaveMap() {
        File mapFile = new File(Environment.getExternalStorageDirectory(), appName);
        if (mapFile.exists()) {
            mapFile = new File(mapFile, name);
            return mapFile.exists();
        } else {
            return false;
        }
    }
}
