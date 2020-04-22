package com.zt.navigation.oldlyg.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.map.Feature;
import com.esri.core.tasks.ags.find.FindResult;
import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.contract.SearchContract;
import com.zt.navigation.oldlyg.model.bean.HistoryBean;
import com.zt.navigation.oldlyg.presenter.SearchPresenter;
import com.zt.navigation.oldlyg.view.adapter.AddressAdapter;
import com.zt.navigation.oldlyg.view.adapter.HistoryAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;
import cn.faker.repaymodel.util.ToastUtility;
import cn.faker.repaymodel.widget.view.BaseRecycleView;

public class SearchActivity extends BaseMVPAcivity<SearchContract.View, SearchPresenter> implements SearchContract.View {

    private EditText mEdTxt;
    private TextView mTvquery;
    private RecyclerView mRvHistory;//历史记录
    private RecyclerView mRvSearch;//搜索列表

    private HistoryAdapter historyAdapter;
    private AddressAdapter addressAdapter;

    public static final  int QUERY_TYPE_ED=0;//EDITTEXT动态输入
    public static final  int QUERY_TYPE_OK=1;//点击搜索
    public static final  int QUERY_TYPE_OK_HIST=2;//点击历史纪录搜索

    private final int requestCode =405;
    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_search;
    }

    @Override
    protected void initContentView() {
        isShowToolView(false);
        changStatusIconCollor(false);

        mEdTxt = findViewById(R.id.ed_txt);
        mTvquery = findViewById(R.id.tv_query);
        mRvHistory = findViewById(R.id.rv_history);
        mRvSearch = findViewById(R.id.rv_search);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        historyAdapter = new HistoryAdapter();
        mRvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvHistory.setAdapter(historyAdapter);

        mRvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        addressAdapter = new AddressAdapter();
        mRvSearch.setAdapter(addressAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTvquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                mPresenter.search(QUERY_TYPE_OK, getEditTextValue(mEdTxt));
            }
        });
        historyAdapter.setOnItemClick(new HistoryAdapter.OnItemClick() {
            @Override
            public void onClick(int position, String txt) {
                showLoading();
                mPresenter.search(QUERY_TYPE_OK_HIST,txt);
            }

            @Override
            public void onClear() {
                mPresenter.cleanHistory();
                mPresenter. queryHistory();
            }
        });
        addressAdapter.setOnItemClickListener(new BaseRecycleView.OnItemClickListener<String>() {
            @Override
            public void onItemClick(View view, String data, int position) {
                showLoading();
                addressAdapter.setSuggestResults(null);
                mPresenter.search(QUERY_TYPE_OK, data);
            }
        });

        mEdTxt.addTextChangedListener(new TextWatcher() {
            private CharSequence mOldQueryText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                if (!TextUtils.equals(newText, this.mOldQueryText)) {
                    mPresenter.search(QUERY_TYPE_ED, newText.toString());
                }
                this.mOldQueryText = newText.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPresenter.queryHistory();
    }

    @Override
    public void search_Success(int type, List<FindResult> datas) {
         Map<String, Point> search_Data = new HashMap<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> cityAddress = new ArrayList<>();
        for (FindResult item:datas) {
            String name = item.getValue();
            if (item.getGeometry() instanceof Point){
                search_Data.put(name, (Point) item.getGeometry());
                names.add(name);
            }

//            cityAddress.add(sb_Address.toString());
        }

 /*       while (iterator.hasNext()) {
            Feature feature = (Feature) iterator.next();
            Map<String, Object> attributes = feature.getAttributes();
            Geometry geometry = feature.getGeometry();
            Set<String> set = attributes.keySet();
            int i = 0;
            String name = null;
            StringBuffer sb_Address= new StringBuffer();
            for (String key : set) {
                if ("NAME".equals(key)) {
                    name = String.valueOf(attributes.get(key));
                } else if (i == 7||i==8||i==9) {
                    sb_Address.append(String.valueOf(attributes.get(key)));
                }
                i++;
            }
            if (geometry instanceof Point) {
                search_Data.put(name, (Point) geometry);
                names.add(name);
                cityAddress.add(sb_Address.toString());
            } else {
                continue;
            }
        }*/
        if (type == QUERY_TYPE_ED) {
            mRvSearch.setVisibility(View.VISIBLE);
            //自动搜索
            addressAdapter.setSuggestResults(names);
        } else if (type == QUERY_TYPE_OK||type==QUERY_TYPE_OK_HIST) {
            //跳转到路线选择展示
            Intent intent = new Intent();
            Bundle bd = new Bundle();
            bd.putSerializable(AddressListActivity.INTENT_KEY_SEARCH_DATA, (Serializable) search_Data);
            bd.putStringArrayList(AddressListActivity.INTENT_KEY_NAME,names);
            bd.putStringArrayList(AddressListActivity.INTENT_KEY_CITY,cityAddress);
            intent.putExtra("bundle",bd);
            intent.setClass(getContext(),AddressListActivity.class);
            dimiss();
            startActivityForResult(intent,requestCode);
        }
    }

    @Override
    public void search_Fail(int type, String text) {
        if (type == QUERY_TYPE_ED) {
            addressAdapter.setSuggestResults(null);
        } else if (type == QUERY_TYPE_OK||type==QUERY_TYPE_OK_HIST) {
            dimiss();
            ToastUtility.showToast(text);
        }
    }

    @Override
    public void queryHistory(List<HistoryBean> historyDatas) {
        historyAdapter.setHistoryDatas(historyDatas);
    }
}
