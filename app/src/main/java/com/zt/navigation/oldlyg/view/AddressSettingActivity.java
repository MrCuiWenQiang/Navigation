package com.zt.navigation.oldlyg.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.contract.AddressSettingContract;
import com.zt.navigation.oldlyg.presenter.AddressSettingPresenter;
import com.zt.navigation.oldlyg.util.UrlUtil;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;

public class AddressSettingActivity extends BaseMVPAcivity<AddressSettingContract.View, AddressSettingPresenter> implements AddressSettingContract.View {
    private EditText et_net_ip;
    private EditText et_gis_ip;
    private Button btnSetHttp;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_addresssetting;
    }

    @Override
    protected void initContentView() {
        changStatusIconCollor(false);
        setBackBackground(R.mipmap.fanhui_black);
        setTitle("网络设置");

        et_net_ip = findViewById(R.id.et_net_ip);
        et_gis_ip = findViewById(R.id.et_gis_ip);
        btnSetHttp = findViewById(R.id.btnSetHttp);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        et_net_ip.setText(UrlUtil.getNetUrl());
        et_gis_ip.setText(UrlUtil.getGisUrl());

    }

    @Override
    protected void initListener() {
        super.initListener();
        btnSetHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.save(getEditTextValue(et_net_ip),getEditTextValue(et_gis_ip));
            }
        });
    }

    @Override
    public void save_fail(String s) {
        showDialog(s);
    }

    @Override
    public void save_Success(String s) {
        showDialog(s, new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                finish();
            }
        });
    }
}
