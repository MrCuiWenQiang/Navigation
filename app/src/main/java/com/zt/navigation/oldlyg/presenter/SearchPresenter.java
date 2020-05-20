package com.zt.navigation.oldlyg.presenter;

import android.text.TextUtils;

import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.ags.find.FindParameters;
import com.esri.core.tasks.ags.find.FindResult;
import com.esri.core.tasks.ags.find.FindTask;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.SearchContract;
import com.zt.navigation.oldlyg.model.bean.HistoryBean;
import com.zt.navigation.oldlyg.task.AsyncQueryTask;
import com.zt.navigation.oldlyg.view.SearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.util.PreferencesUtility;
import cn.faker.repaymodel.util.db.DBThreadHelper;

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
        // TODO: 2020/3/16 更换全图层查询  searchFromLayers
        if (type == SearchActivity.QUERY_TYPE_OK) {
            addHistory(text);
            queryHistory();
        }
        final FindTask findTask = new FindTask(Urls.searchUrl);
        final FindParameters findParameters = new FindParameters();
        findParameters.setLayerIds(layerIds);
        findParameters.setReturnGeometry(true); //允许返回几何图形
        findParameters.setSearchText(text); // 设置查询关键字--必须设置
        findParameters.setSearchFields(fields); // 设置查询字段的名称
        DBThreadHelper.startThreadInPool(new DBThreadHelper.ThreadCallback<List<FindResult>>() {

            @Override
            protected List<FindResult> jobContent() throws Exception {
                return findTask.execute(findParameters);
            }

            @Override
            protected void jobEnd(List<FindResult> findResults) {
                if (findResults != null) {
                    if (findResults.size()>0) {
                        getView().search_Success(type, findResults);
                    } else {
                        getView().search_Fail(type, "未查询到数据");
                    }
                } else {
//                    getView().search_Fail(type, "查询失败");
                    getView().search_Fail(type, "未查询到数据");
                }
            }
        });

    }


    String[] fields = new String[]{"name","ZGGSDM"};
    //    String condition ;
    int[] layerIds = new int[]{19, 20,21,22,29,33,34,36,37,38,39,64};

    /**
     * 根据条件搜索, 供给RightMenuFragment使用,多图层一起查询
     *
//     * @param condition   // 查询条件
     * @param searchKey// 查询关键字 （设置查询参数的时候必须设置）
//     * @param fields      // 待查询的字段名称 数组
//     * @param layerIds    // 查询服务的子图层的id数组
//     * @param findLayer   // 待查询的服务URL
     */
    /*public void searchFromLayers(String searchKey) {

        final FindTask findTask = new FindTask(Urls.searchUrl);
        final FindParameters findParameters = new FindParameters();
        findParameters.setLayerIds(layerIds);//默认查询全部图层

        if (fields.length == 1) {
            findParameters.setReturnGeometry(true); //允许返回几何图形
            findParameters.setSearchText(searchKey); // 设置查询关键字--必须设置
            findParameters.setSearchFields(fields); // 设置查询字段的名称
        } else {
//            Map<Integer,String> parmMap = new HashMap<>();
//            for (int a : layerIds){
//                parmMap.put(a,condition);  // 设置每个子图层的 查询过滤条件
//            }

//            findParameters.setLayerDefs(parmMap);

        }
        findParameters.setReturnGeometry(true);
        findParameters.setSearchText(searchKey);
        findParameters.setSearchFields(fields);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<FindResult> res = findTask.execute(findParameters);
                    if (res != null) {

                    }
                    // res就是查询到的结果 List<FindResult>
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
*/}
