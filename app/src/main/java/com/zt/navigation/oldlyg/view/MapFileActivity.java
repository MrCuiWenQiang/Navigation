package com.zt.navigation.oldlyg.view;

import android.os.Bundle;
import android.os.Environment;

import com.zt.navigation.oldlyg.R;

import java.io.File;

import cn.faker.repaymodel.activity.BaseToolBarActivity;
import cn.faker.repaymodel.util.FileUtility;
import cn.faker.repaymodel.util.db.DBThreadHelper;

public class MapFileActivity extends BaseToolBarActivity {
    //路径为 /sd卡路径
    private static final String appName = "NavigationMap";
    private static final String name = "lianyungang_dxt.mpk";
    private File mapFile;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_mapfile;
    }

    @Override
    protected void initContentView() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (isHaveMap()) {
            showDialog("地图包已存在本地,无需更新");
            return;
        }
        mapFile = new File(Environment.getExternalStorageDirectory() + "/" + appName, name);
        DBThreadHelper.startThreadInPool(new DBThreadHelper.ThreadCallback() {
            @Override
            protected Object jobContent() throws Exception {
                FileUtility.copyFilesFromRaw(getContext(), R.raw.lianyungang_dxt, name, Environment.getExternalStorageDirectory() + "/" + appName);
                return null;
            }

            @Override
            protected void jobEnd(Object o) {
                if (isHaveMap()) {
                    showDialog("地图包解压完成");
                } else {
                    showDialog("地图包解压失败");
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
