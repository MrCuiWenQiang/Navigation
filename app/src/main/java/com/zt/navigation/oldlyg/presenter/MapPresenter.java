package com.zt.navigation.oldlyg.presenter;


import android.content.Context;

import com.zt.navigation.oldlyg.contract.MapContract;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.util.FileUtility;
import cn.faker.repaymodel.util.db.DBThreadHelper;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.model.LocationUploadModel;
import com.zt.navigation.oldlyg.util.TokenManager;
import com.zt.navigation.oldlyg.util.TpkUtil;

public class MapPresenter extends BaseMVPPresenter<MapContract.View> implements MapContract.Presenter {

    public String getPath(){
        return TpkUtil.getPath();
    }
    public void mapFile(Context context) {
        if (TpkUtil.isHaveMap()) {
            return;
        }
        getView().showStartMapFile("解压离线地图包中...");
        DBThreadHelper.startThreadInPool(new DBThreadHelper.ThreadCallback<Integer>() {
            @Override
            protected Integer jobContent() throws Exception {
                FileUtility.copyFilesFromRaw(context, R.raw.lianyungang_dxt, TpkUtil.ZIPName, TpkUtil.getFathPath());
                return TpkUtil.decode();
            }

            @Override
            protected void jobEnd(Integer o) {
                if (o==0) {
                    getView().showEndMapFile("地图包解压完成");

                } else {
                    getView().showEndMapFile("地图包解压失败");
                }
            }
        });
    }

    private LocationUploadModel uploadModel = new LocationUploadModel();

    @Override
    public void updateLocation(double lan, double lon) {
        uploadModel.upload(TokenManager.token, lon, lan, null);
    }


}
