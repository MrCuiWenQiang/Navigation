package com.zt.navigation.oldlyg.presenter;


import com.zt.navigation.oldlyg.contract.SettingContract;
import com.zt.navigation.oldlyg.util.AppSettingUtil;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;

public class SettingPresenter extends BaseMVPPresenter<SettingContract.View> implements SettingContract.Presenter {
    public String[] cats=new String[]{"小车路线","大车路线"};
    public String[] maps=new String[]{"在线加载","离线加载"};
    @Override
    public void settingCatType(int index,boolean value) {
        AppSettingUtil.setCatType(value);
        getView().settingCatType(cats[index]);
    }

    @Override
    public void settingMapType(int index,boolean value) {
        if (value!=AppSettingUtil.getMapType()){
            AppSettingUtil.setMapType(value);
        }
        getView().settingMapType(maps[index],value);
    }
}
