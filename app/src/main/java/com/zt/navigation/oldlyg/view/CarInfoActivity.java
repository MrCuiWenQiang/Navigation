package com.zt.navigation.oldlyg.view;

import android.os.Bundle;

import com.zt.navigation.oldlyg.contract.CarInfoContract;
import com.zt.navigation.oldlyg.presenter.CarInfoPresenter;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;

import com.zt.navigation.oldlyg.R;

public class CarInfoActivity extends BaseMVPAcivity<CarInfoContract.View, CarInfoPresenter> implements CarInfoContract.View {
    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_carinfo;
    }

    @Override
    protected void initContentView() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }
}
