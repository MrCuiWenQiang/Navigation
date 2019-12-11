package com.zt.navigation.oldlyg.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.esri.core.geometry.Point;
import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.contract.AddressListContract;
import com.zt.navigation.oldlyg.presenter.AddressListPresenter;
import com.zt.navigation.oldlyg.view.adapter.BootomAddressAdapter;

import java.util.List;
import java.util.Map;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;

/**
 * 搜索地点展示
 */
public class AddressListActivity extends BaseMVPAcivity<AddressListContract.View, AddressListPresenter> implements AddressListContract.View {

    public static final String INTENT_KEY_NAME = "INTENT_KEY_NAME";//LIST<String>
    public static final String INTENT_KEY_CITY = "INTENT_KEY_CITY";//LIST<String>
    public static final String INTENT_KEY_SEARCH_DATA = "INTENT_KEY_SEARCH_DATA";//Map

    private RecyclerView rv_list;
    private BootomAddressAdapter bootomAddressAdapter;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_address;
    }

    @Override
    protected void initContentView() {
        setBackBackground(R.mipmap.fanhui_black);
        setTitle("搜索结果");
        rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getContext()));
        bootomAddressAdapter = new BootomAddressAdapter();
        rv_list.setAdapter(bootomAddressAdapter);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        List<String> names = bundle.getStringArrayList(INTENT_KEY_NAME);
        List<String> cityAddresss = bundle.getStringArrayList(INTENT_KEY_CITY);
        Map<String, Point> pointMap = (Map<String, Point>) bundle.getSerializable(INTENT_KEY_SEARCH_DATA);
        bootomAddressAdapter.setCityAddresss(cityAddresss);
        bootomAddressAdapter.setData(names, pointMap);
    }

    @Override
    protected void initListener() {
        super.initListener();
        bootomAddressAdapter.setOnItemClickListener(new BootomAddressAdapter.OnAddressItemClickListener() {
            @Override
            public void onItemClick(View view, Point data, String name, int position) {
                Intent intent = new Intent(getContext(), NavigationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(NavigationActivity.INTENT_KEY_END, data);
                bundle.putString(NavigationActivity.INTENT_KEY_END_NAME, name);
                intent.putExtra(NavigationActivity.BUNDLE_NAME, bundle);
                startActivity(intent);
            }
        });

    }


}
