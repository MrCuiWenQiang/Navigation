package com.zt.navigation.oldlyg.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.zt.navigation.oldlyg.contract.CarInfoContract;
import com.zt.navigation.oldlyg.model.webbean.CarInfoBean;
import com.zt.navigation.oldlyg.presenter.CarInfoPresenter;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.view.adapter.CarInfoAdapter;

/**
 * 派车单详情
 */
public class CarInfoActivity extends BaseMVPAcivity<CarInfoContract.View, CarInfoPresenter> implements CarInfoContract.View
        , View.OnClickListener {

    public static final String CARID = "CARID";

    private TextView tv_taskno;
    private TextView tv_permitno;
    private TextView tv_vessel;
    private TextView tv_voyage;
    private TextView tv_vehicle;
    private TextView tv_department;
    private TextView tv_cargo;
    private TextView tv_mark;
    private TextView tv_weight;
    private RecyclerView rv_address;
    private Button bt_nav;

    private CarInfoAdapter carInfoAdapter = new CarInfoAdapter();

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_carinfo;
    }

    @Override
    protected void initContentView() {
        changStatusIconCollor(false);
        setBackBackground(R.mipmap.fanhui_black);
        setTitle("派车单详情");

        tv_taskno = findViewById(R.id.tv_taskno);
        tv_permitno = findViewById(R.id.tv_permitno);
        tv_vessel = findViewById(R.id.tv_vessel);
        tv_voyage = findViewById(R.id.tv_voyage);
        tv_vehicle = findViewById(R.id.tv_vehicle);
        tv_department = findViewById(R.id.tv_department);
        tv_cargo = findViewById(R.id.tv_cargo);
        tv_mark = findViewById(R.id.tv_mark);
        tv_weight = findViewById(R.id.tv_weight);
        rv_address = findViewById(R.id.rv_address);
        bt_nav = findViewById(R.id.bt_nav);

        rv_address.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_address.setAdapter(carInfoAdapter);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        String id = getIntent().getStringExtra(CARID);
        showLoading();
        mPresenter.loadInfo(id);
    }

    @Override
    protected void initListener() {
        super.initListener();
        bt_nav.setOnClickListener(this);
    }

    @Override
    public void info_Success(CarInfoBean carInfoBean) {
        dimiss();
        tv_taskno.setText(carInfoBean.getTaskno());
        tv_permitno.setText(carInfoBean.getPermitno());
        tv_vessel.setText(carInfoBean.getVessel());
        tv_voyage.setText(carInfoBean.getVoyage());
        tv_vehicle.setText(carInfoBean.getVehicle());
        tv_department.setText(carInfoBean.getDepartment());
        tv_cargo.setText(carInfoBean.getCargo());
        tv_mark.setText(carInfoBean.getMark());
        tv_weight.setText(carInfoBean.getWeight());
        carInfoAdapter.setData(carInfoBean.getAddress());
    }

    @Override
    public void info_Fail(String msg) {
        dimiss();
        showDialog(msg, new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_nav: {
                break;
            }
        }
    }
}
