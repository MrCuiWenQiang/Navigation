package com.zt.navigation.oldlyg.view;

import android.os.Bundle;

import com.zt.navigation.oldlyg.R;

import cn.faker.repaymodel.activity.BaseToolBarActivity;

/**
 * 意见反馈
 */
public class FeedbackActivity extends BaseToolBarActivity {
    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_feeback;
    }

    @Override
    protected void initContentView() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        changStatusIconCollor(false);
        setBackBackground(R.mipmap.fanhui_black);
        setTitle("意见反馈");
    }
}
