package com.zt.navigation.oldlyg.presenter;


import android.content.Context;
import android.os.Environment;

import com.zt.navigation.oldlyg.contract.MapContract;

import java.io.File;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.util.FileUtility;
import cn.faker.repaymodel.util.db.DBThreadHelper;

import com.zt.navigation.oldlyg.R;

public class MapPresenter extends BaseMVPPresenter<MapContract.View> implements MapContract.Presenter {
    //路径为 /sd卡路径
    private static final String appName = "NavigationMap";
    private static final String name = "lianyungang_dxt.mpk";
    public  String mapFilePath;
    private File mapFile;

    public void mapFile(Context context) {
        mapFilePath = Environment.getExternalStorageDirectory() + "/" + appName+"/"+name;
        if (isHaveMap()) {
            return;
        }
        getView().showStartMapFile("解压离线地图包中...");
        mapFile = new File(Environment.getExternalStorageDirectory() + "/" + appName, name);
        DBThreadHelper.startThreadInPool(new DBThreadHelper.ThreadCallback() {
            @Override
            protected Object jobContent() throws Exception {
                FileUtility.copyFilesFromRaw(context, R.raw.lianyungang_dxt, name, Environment.getExternalStorageDirectory() + "/" + appName);
                return null;
            }

            @Override
            protected void jobEnd(Object o) {
                if (isHaveMap()) {
                    getView().showEndMapFile("地图包解压完成");

                } else {
                    getView().showEndMapFile("地图包解压失败");
                }
            }
        });
    }

    /**
     * 检测文件是否存在
     *
     * @return
     */
    private boolean isHaveMap() {
        mapFile = new File(Environment.getExternalStorageDirectory(), appName);
        if (mapFile.exists()) {
            mapFile = new File(mapFile, name);
            return mapFile.exists();
        } else {
            return false;
        }
    }
}
