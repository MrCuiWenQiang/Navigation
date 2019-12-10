package com.zt.navigation.oldlyg.presenter;

import android.text.TextUtils;

import com.esri.core.map.FeatureResult;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.SearchContract;
import com.zt.navigation.oldlyg.model.bean.HistoryBean;
import com.zt.navigation.oldlyg.task.AsyncQueryTask;
import com.zt.navigation.oldlyg.view.SearchActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.util.PreferencesUtility;

public class SearchPresenter extends BaseMVPPresenter<SearchContract.View> implements SearchContract.Presenter {

    private final String keyName = "history";

    private List<HistoryBean> historyBeans;

    @Override
    public void attachView(SearchContract.View view) {
        super.attachView(view);
    }

    @Override
    public void addHistory(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (historyBeans == null) {
            queryHistory();
        }
        if (historyBeans == null) {
            historyBeans = new ArrayList<>();
        }
        if (historyBeans.size() >= 12) {
            historyBeans.remove(11);
        }
        for (int i = 0; i < historyBeans.size(); i++) {
            HistoryBean item = historyBeans.get(i);
            if (item.getName().equals(text)) {
                historyBeans.remove(item);
                i--;
            }
        }

        HistoryBean bean = new HistoryBean();
        bean.setName(text);
        historyBeans.add(0, bean);
        String json = JsonUtil.convertObjectToJson(historyBeans);
        PreferencesUtility.setPreferencesField(keyName, json);
    }

    @Override
    public void cleanHistory() {
        PreferencesUtility.setPreferencesField(keyName, "");
    }

    @Override
    public void queryHistory() {
        String json = PreferencesUtility.getPreferencesAsString(keyName);
        if (!TextUtils.isEmpty(json)) {
            historyBeans = JsonUtil.fromList(json, HistoryBean.class);
        } else {
            historyBeans = null;
        }
        getView().queryHistory(historyBeans);
    }

    @Override
    public void search(final int type, String text) {
        if (TextUtils.isEmpty(text)) {
            getView().search_Fail(type, "请输入地点");
            return;
        }
        if (type == SearchActivity.QUERY_TYPE_OK) {
            addHistory(text);
            queryHistory();
        }
        AsyncQueryTask asyncQueryTask = new AsyncQueryTask();
        asyncQueryTask.execute(Urls.searchUrl, null, "name LIKE '%" + text + "%'");
        asyncQueryTask.setOnReturnDataListener(new AsyncQueryTask.OnReturnDataListener() {
            @Override
            public void onReturnData(FeatureResult result) {
                if (result != null) {
                    Iterator<Object> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        getView().search_Success(type, iterator);
                    } else {
                        getView().search_Fail(type, "未查询到数据");
                    }
                } else {
                    getView().search_Fail(type, "查询失败");
                }
            }
        });
    }

}
