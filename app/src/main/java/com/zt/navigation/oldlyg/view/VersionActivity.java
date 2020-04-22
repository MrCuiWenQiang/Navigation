package com.zt.navigation.oldlyg.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.contract.VersionContract;
import com.zt.navigation.oldlyg.presenter.VersionPresenter;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;

/**
 * 版本更新
 */
public class VersionActivity extends BaseMVPAcivity<VersionContract.View, VersionPresenter> implements VersionContract.View {
    private TextView tv_hint;
    private TextView tv_now;
    private TextView bt_up;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_version;
    }

    @Override
    protected void initContentView() {
        changStatusIconCollor(false);
        setBackBackground(R.mipmap.fanhui_black);
        setTitle("版本更新");

        tv_now = findViewById(R.id.tv_now);
        tv_hint = findViewById(R.id.tv_hint);
        bt_up = findViewById(R.id.bt_up);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        String nowVersion = QMUIPackageHelper.getAppVersion(getContext());
        tv_now.setText("APP当前版本为" + nowVersion);
    }

    @Override
    protected void initListener() {
        super.initListener();
        bt_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
