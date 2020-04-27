package com.zt.navigation.oldlyg.presenter;


import android.text.TextUtils;

import com.zt.navigation.oldlyg.contract.AddressSettingContract;
import com.zt.navigation.oldlyg.util.UrlUtil;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;

public class AddressSettingPresenter extends BaseMVPPresenter<AddressSettingContract.View> implements AddressSettingContract.Presenter {

    @Override
    public void save(String netAddress, String gisAddress) {
        if (TextUtils.isEmpty(netAddress)){
            getView().save_fail("保存失败:业务地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(gisAddress)){
            getView().save_fail("保存失败:地图地址不能为空");
            return;
        }

        UrlUtil.setNetUrl(netAddress);
        UrlUtil.setGisUrl(gisAddress);
        getView().save_Success("保存成功");
    }
}
