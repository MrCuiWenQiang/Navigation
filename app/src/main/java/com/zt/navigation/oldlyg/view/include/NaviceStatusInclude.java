package com.zt.navigation.oldlyg.view.include;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.view.MapActivity;

/**
 * 路线展示
 */
public class NaviceStatusInclude implements View.OnClickListener {
    private Activity mActivity;

    private LinearLayout ll_status;
    private LinearLayout ll_status_exit;
    private TextView tv_status;
    private OnClickStatu onClickStatu;


    public NaviceStatusInclude(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void init() {
        ll_status = mActivity.findViewById(R.id.ll_status);
        ll_status_exit = mActivity.findViewById(R.id.ll_status_exit);
        tv_status = mActivity.findViewById(R.id.tv_status);
        ll_status_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onClickStatu!=null){
            onClickStatu.onExit();
        }
    }

    public void setOnClickStatu(OnClickStatu onClickStatu) {
        this.onClickStatu = onClickStatu;
    }

    /**
     * 设置数据
     * @param txt
     */
    public void setData(String txt) {
        if (ll_status == null) {
            init();
        }
        if (ll_status.getVisibility() == View.GONE) {
            ll_status.setVisibility(View.VISIBLE);
        }
        tv_status.setText(txt);
    }

    public void on(){
        if (ll_status == null) {
            init();
        }
        ll_status.setVisibility(View.VISIBLE);
    }
    public void off(){
        if (ll_status!=null){
            ll_status.setVisibility(View.GONE);
            tv_status.setText(mActivity.getString(R.string.nai_hint));
        }
    }

    public interface OnClickStatu{
        void onExit();
    }
}
