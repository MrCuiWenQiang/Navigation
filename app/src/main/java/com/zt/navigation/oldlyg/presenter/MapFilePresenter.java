package com.zt.navigation.oldlyg.presenter;



import com.zt.navigation.oldlyg.contract.MapFileContract;
import com.zt.navigation.oldlyg.util.TpkUtil;


import cn.faker.repaymodel.mvp.BaseMVPPresenter;

public class MapFilePresenter extends BaseMVPPresenter<MapFileContract.View> implements MapFileContract.Presenter {


    public final int TYPE_SUCCESS=200;
    public final int TYPE_OLD=300;//需更新
    public final int TYPE_ERROR=400;//本地文件缺失

    @Override
    public void check() {
        if (isHaveMap()){
            getView().showHint(TYPE_SUCCESS,"你的本地地图包已是最新",null);
            return;
        }else {
            getView().showHint(TYPE_ERROR,"你的本地地图包缺失","修复");
            return;
        }
//        mapFilePath = Environment.getExternalStorageDirectory() + "/" + MapPresenter.appName + "/" + MapPresenter.name;

    }

    private boolean isHaveMap() {
       return TpkUtil.isHaveMap();
    }
}
