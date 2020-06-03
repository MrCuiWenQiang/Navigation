package com.zt.navigation.oldlyg.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.contract.MessageContract;
import com.zt.navigation.oldlyg.model.webbean.MessageBean;
import com.zt.navigation.oldlyg.presenter.MessagePresenter;
import com.zt.navigation.oldlyg.view.adapter.MessageListAdapter;

import java.util.List;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;
import cn.faker.repaymodel.util.SpaceItemDecoration;
import cn.faker.repaymodel.util.ToastUtility;

/**
 * 消息中心
 */
public class MessageAcctivity extends BaseMVPAcivity<MessageContract.View, MessagePresenter> implements MessageContract.View, View.OnClickListener {

    private RecyclerView rv_list;
    private MessageListAdapter adapter;

    @Override
    public void onClick(View v) {

    }

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_message;
    }

    @Override
    protected void initContentView() {
        changStatusIconCollor(false);
        setBackBackground(R.mipmap.fanhui_black);
        setTitle("我的消息");


        rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageListAdapter();
        rv_list.setAdapter(adapter);
        rv_list.addItemDecoration(new SpaceItemDecoration(8));
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        showLoading();
        mPresenter.loadData();
    }

    @Override
    public void loadDataSuccess(List<MessageBean> datas) {
        dimiss();
        adapter.daras(datas);
    }

    @Override
    public void loadDataFail(String message) {
        dimiss();
        ToastUtility.showToast(message);
    }
}
