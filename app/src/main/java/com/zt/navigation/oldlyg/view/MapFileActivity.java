package com.zt.navigation.oldlyg.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.contract.MapFileContract;
import com.zt.navigation.oldlyg.presenter.MapFilePresenter;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;

/**
 * 地图资源管理
 */
public class MapFileActivity extends BaseMVPAcivity<MapFileContract.View, MapFilePresenter> implements MapFileContract.View, View.OnClickListener {

    private TextView tv_hint;
    private Button bt_up;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_mapfile;
    }

    @Override
    protected void initContentView() {
        changStatusIconCollor(false);
        setBackBackground(R.mipmap.fanhui_black);
        setTitle("地图资源");

        tv_hint = findViewById(R.id.tv_hint);
        bt_up = findViewById(R.id.bt_up);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        showLoading();
        mPresenter.check();
    }

    @Override
    protected void initListener() {
        super.initListener();
        bt_up.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_up) {
        }
    }

    @Override
    public void showHint(int type, String hint, String btMsg) {
        dimiss();
        tv_hint.setText(hint);
        if (type==mPresenter.TYPE_SUCCESS){
            bt_up.setVisibility(View.GONE);
        }
        bt_up.setText(btMsg);
    }
}
