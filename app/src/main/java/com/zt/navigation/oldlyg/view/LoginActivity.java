package com.zt.navigation.oldlyg.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.faker.repaymodel.activity.BaseToolBarActivity;
import cn.faker.repaymodel.mvp.BaseMVPAcivity;
import cn.faker.repaymodel.util.ToastUtility;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.contract.LoginContract;
import com.zt.navigation.oldlyg.presenter.LoginPresenter;

public class LoginActivity extends BaseMVPAcivity<LoginContract.View, LoginPresenter> implements LoginContract.View, View.OnClickListener {

    private EditText editText;
    private TextView tv_head;
    private Button bt_login;
    private String[] carHeads;
    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_login;
    }

    @Override
    protected void initContentView() {
        isShowBackButton(false);
        isShowToolView(false);
        changStatusIconCollor(false);
        editText = findViewById(R.id.editText);
        bt_login = findViewById(R.id.bt_login);
        tv_head = findViewById(R.id.tv_head);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        carHeads = getResources().getStringArray(R.array.carheads);
    }

    @Override
    protected void initListener() {
        super.initListener();
        bt_login.setOnClickListener(this);
        tv_head.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.bt_login){
            showLoading();
            mPresenter.login(getValue(tv_head),getValue(editText));
        }else if (v.getId()==R.id.tv_head){
            showListDialog(carHeads, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tv_head.setText(carHeads[which]);
                    dialog.dismiss();
                }
            });
        }
   /*     switch (v.getId()) {
            case R.id.bt_login: {
                // TODO: 2020/4/24 暂时隐藏
                showLoading();
                mPresenter.login(getValue(tv_head),getValue(editText));
//                toAcitvity(MapActivity.class);
                break;
            }
            case R.id.tv_head: {
                showListDialog(carHeads, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_head.setText(carHeads[which]);
                        dialog.dismiss();
                    }
                });
                break;
            }
        }*/
    }

    @Override
    public void init_data(String carHead,String carNo) {
        tv_head.setText(carHead);
        editText.setText(carNo);
    }

    @Override
    public void login_Success(String msg) {
        dimiss();
        toAcitvity(MapActivity.class);
        finish();
    }

    @Override
    public void login_Fail(String msg) {
        dimiss();
        ToastUtility.showToast(msg);
    }
}
