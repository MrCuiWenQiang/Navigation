package com.zt.navigation.oldlyg.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zt.navigation.oldlyg.contract.CarListContract;
import com.zt.navigation.oldlyg.model.webbean.CarListBean;
import com.zt.navigation.oldlyg.presenter.CarListPresenter;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;
import cn.faker.repaymodel.util.SpaceItemDecoration;
import cn.faker.repaymodel.util.SpacesItemDecoration;
import cn.faker.repaymodel.util.ToastUtility;
import cn.faker.repaymodel.widget.view.BaseRecycleView;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.util.TokenManager;
import com.zt.navigation.oldlyg.view.adapter.CarListAdapter;

import java.util.List;

/**
 * 派车单列表
 */
public class CarListActivity extends BaseMVPAcivity<CarListContract.View, CarListPresenter> implements CarListContract.View {

    private RecyclerView rv_list;
    private CarListAdapter carListAdapter;
    private RefreshLayout mRefreshLayout;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_carlist;
    }

    @Override
    protected void initContentView() {
        changStatusIconCollor(false);
        setBackBackground(R.mipmap.fanhui_black);
        setTitle("我的派车单");

        rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_list.addItemDecoration(new SpaceItemDecoration(8));
        carListAdapter = new CarListAdapter();
        rv_list.setAdapter(carListAdapter);

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadmore(false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        showLoading();
        loadData();
    }

    @Override
    protected void initListener() {
        super.initListener();
        carListAdapter.setOnItemClickListener(new BaseRecycleView.OnItemClickListener<String>() {
            @Override
            public void onItemClick(View view, String data, int position) {
                Intent intent = new Intent(getContext(), CarInfoActivity.class);
                intent.putExtra(CarInfoActivity.CARID, data);
                startActivity(intent);
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadData();
            }
        });
    }

    private void loadData() {
        mPresenter.loadData(TokenManager.token, TokenManager.getUserId(), "1");
    }

    @Override
    public void loadData_success(List<CarListBean> cars) {
        mRefreshLayout.finishRefresh();//完成刷新
        carListAdapter.setCars(cars);
        dimiss();
    }

    @Override
    public void loadData_fail(int status, String message) {
        mRefreshLayout.finishRefresh();//完成刷新
        dimiss();
        ToastUtility.showToast(message);
    }
}
