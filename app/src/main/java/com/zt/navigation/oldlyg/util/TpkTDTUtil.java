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
public class TpkTDTUtil {
    //路径为 /sd卡路径
    public static final String appName = "NavigationMap";
    public static final String name = "lianyungang_tdt.tpk";
    public static final String ZIPName = "lianyungang_tdt.zip";

    private static String mapFilePath;
    private static String mapFileFathPath;
    public static String mapZIPFilePath;

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
     * 返回压缩包路径
     *
     * @return
     */
    public static String getZipPath() {
        if (TextUtils.isEmpty(mapZIPFilePath)) {
            mapZIPFilePath = Environment.getExternalStorageDirectory() + "/" + appName + "/" + ZIPName;
        }
        return mapZIPFilePath;
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

    //解压
    public static int decode() {
        return decode(getZipPath(), getFathPath());
    }

    public static int decode(String zipFilePath, String filePath) {
        return uncompressZip4j(zipFilePath, filePath, null);
    }

    public static int uncompressZip4j(String zipFilePath, String filePath, String password) {
        File zipFile_ = new File(zipFilePath);
        File sourceFile = new File(filePath);
        int result = 0;
        try {
            ZipFile zipFile = new ZipFile(zipFile_);
//            zipFile.setFileNameCharset("GBK");  //设置编码格式（支持中文）
            if (!zipFile.isValidZipFile()) {     //检查输入的zip文件是否是有效的zip文件
                throw new ZipException("压缩文件不合法,可能被损坏.");
            }
            if (sourceFile.isDirectory() && !sourceFile.exists()) {
                sourceFile.mkdir();
            }
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(filePath); //解压
            LogUtil.e("uncompressZip4j:", " 解压成功");

        } catch (ZipException e) {
            ErrorUtil.showError(e);
            result = -1;
            return result;
        }
        return result;
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
