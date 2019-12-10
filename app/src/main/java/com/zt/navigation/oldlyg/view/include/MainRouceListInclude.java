package com.zt.navigation.oldlyg.view.include;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteDirection;
import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.view.MapActivity;
import com.zt.navigation.oldlyg.view.adapter.RouceAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 控制管理主页的路线展示include
 */
public class MainRouceListInclude {

    private Activity mActivity;

    private RecyclerView rv_route;
    private TextView tv_length;
    private TextView bt_tonav;
    private TextView bt_clean;
    private LinearLayout ll_group;
    private RouceAdapter rouceAdapter;

    public MainRouceListInclude(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void init() {
        ll_group = mActivity.findViewById(R.id.ll_group);
        rv_route = mActivity.findViewById(R.id.rv_route);
        tv_length = mActivity.findViewById(R.id.tv_length);
        bt_tonav = mActivity.findViewById(R.id.bt_tonav);
        bt_clean = mActivity.findViewById(R.id.bt_clean);

        rv_route.setLayoutManager(new LinearLayoutManager(mActivity));

        bt_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                off();
            }
        });
    }

    public void setData(final Route mRoute, final OnClickStartNai startNai) {
        List<RouteDirection> routeDirections = mRoute.getRoutingDirections();
        double length = mRoute.getTotalKilometers();
        List<String> txts = new ArrayList<>();
        for (RouteDirection rd : routeDirections) {
            txts.add(rd.getText());
        }
        String[] addresss = txts.toArray(new String[txts.size()]);
        DecimalFormat df = new DecimalFormat("#.00");
        String str = df.format(length);
        if (rv_route == null) {
            init();
        }
        ll_group.setVisibility(View.VISIBLE);
        if (rouceAdapter == null) {
            rouceAdapter = new RouceAdapter();
            rv_route.setAdapter(rouceAdapter);
        }
        rouceAdapter.setAddresss(addresss);
        tv_length.setText("总长:" + str + "公里");

        bt_tonav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clean();
//                mActivity.mPresenter.navigation(mActivity, mRoute);
                startNai.start();
            }
        });
    }

    public void off() {
        if (ll_group != null) {
            ll_group.setVisibility(View.GONE);
        }
    }

    public void clean() {
        if (ll_group != null) {
            ll_group.setVisibility(View.GONE);
            tv_length.setText(null);
            if (rouceAdapter != null) {
                rouceAdapter.setAddresss(null);
            }
        }
    }

    public interface OnClickStartNai {
        void start();
    }
}
