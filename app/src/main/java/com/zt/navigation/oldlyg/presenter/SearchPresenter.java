package com.zt.navigation.oldlyg.presenter;

import android.text.TextUtils;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.ags.find.FindParameters;
import com.esri.core.tasks.ags.find.FindResult;
import com.esri.core.tasks.ags.find.FindTask;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.SearchContract;
import com.zt.navigation.oldlyg.model.bean.HistoryBean;
import com.zt.navigation.oldlyg.task.AsyncQueryTask;
import com.zt.navigation.oldlyg.util.NominateUtil;
import com.zt.navigation.oldlyg.view.SearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.net.json.JsonUtil;
import cn.faker.repaymodel.util.LogUtil;
import cn.faker.repaymodel.util.PreferencesUtility;
import cn.faker.repaymodel.util.UUIDUtil;
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
        search(type, text, null);
    }

    public void search(final int type, String text, String gsmc) {
        if (TextUtils.isEmpty(text)) {
            getView().search_Fail(type, "");
            return;
        }
        String sql;
        if ((type == SearchActivity.QUERY_TYPE_OK || type == SearchActivity.QUERY_TYPE_OK_HIST)
                && !TextUtils.isEmpty(gsmc)) {
            sql = splitSQL(text, gsmc);

        } else {
            sql = splitSQL(text);
        }
        if (type == SearchActivity.QUERY_TYPE_OK) {
            addHistory(text);
            queryHistory();
        }
        AsyncQueryTask asyncQueryTask = new AsyncQueryTask();
        asyncQueryTask.execute(Urls.searchUrl + "/19", null, sql);
        asyncQueryTask.setOnReturnDataListener(new AsyncQueryTask.OnReturnDataListener() {
            @Override
            public void onReturnData(FeatureResult result) {
                if (result != null) {
                    Iterator<Object> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        Map<String, Point> search_Data = new HashMap<>();
                        ArrayList<String> names = new ArrayList<>();
                        ArrayList<String> cityAddress = new ArrayList<>();

                        while (iterator.hasNext()) {
                            Feature feature = (Feature) iterator.next();
                            Map<String, Object> attributes = feature.getAttributes();
                            Set<String> set = attributes.keySet();

                            Geometry geometry = feature.getGeometry();
                            String name = null;
                            String gsmc = null;
                            for (String key : set) {
                                if (key.equals("NAME")) {
                                    name = attributes.get(key) == null ? null : String.valueOf(attributes.get(key));

                                } else if (key.equals("GSMC")) {
                                    gsmc = attributes.get(key) == null ? null : String.valueOf(attributes.get(key));
                                }
                            }
                            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(gsmc)) {
                                continue;
                            } else {
                                names.add(name);
                                cityAddress.add(gsmc);
                                // TODO: 2020/6/5 有键名重复的可能性  展示的时候将其变为独特，唯一
                                if (search_Data.containsKey(name)) {
                                    name = NominateUtil.contName(name);
                                }

                                search_Data.put(name, (Point) geometry);
                            }
                        }
                        if (getView() != null) {
                            getView().search_Success(type, search_Data, names, cityAddress);
                        }
                    } else {
                        getView().search_Fail(type, "未查询到数据");
                    }
                } else {
                    getView().search_Fail(type, "未查询到数据");
                }
            }
        });
    }

    @Override
    public void loadData() {
        loadData(0, classs);
    }

    private final String[] classs = {"磅房", "堆场出口", "堆场入口"};

    // TODO: 2020/6/5 有没有更好的做法？？？ 用实体?
    private Map<String, List<String>> nameMap = new HashMap<>();
    private Map<String, List<String>> gsmcMap = new HashMap<>();
    private Map<String, List<Point>> pointMap = new HashMap<>();


    // TODO: 2020/6/5 嵌套查询 预知查询时间肯定变长
    private void loadData(int index, String[] classs) {
        if (index >= classs.length) {
            if (getView() != null) {
                getView().loadData_success(classs, nameMap, gsmcMap, pointMap);
            }
            return;
        }
        String className = classs[index];
        AsyncQueryTask asyncQueryTask = new AsyncQueryTask();
        asyncQueryTask.execute(Urls.searchUrl + "/19", null, splitdataSQL(className));
        asyncQueryTask.setOnReturnDataListener(new AsyncQueryTask.OnReturnDataListener() {
            @Override
            public void onReturnData(FeatureResult result) {
                if (result != null) {
                    Iterator<Object> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        ArrayList<String> names = new ArrayList<>();
                        ArrayList<String> cityAddress = new ArrayList<>();
                        ArrayList<Point> points = new ArrayList<>();

                        while (iterator.hasNext()) {
                            Feature feature = (Feature) iterator.next();
                            Map<String, Object> attributes = feature.getAttributes();
                            Set<String> set = attributes.keySet();

                            Geometry geometry = feature.getGeometry();
                            String name = null;
                            String gsmc = null;
                            for (String key : set) {
                                if (key.equals("NAME")) {
                                    name = attributes.get(key) == null ? null : String.valueOf(attributes.get(key));

                                } else if (key.equals("GSMC")) {
                                    gsmc = attributes.get(key) == null ? null : String.valueOf(attributes.get(key));
                                }
                            }
                            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(gsmc)) {
                                continue;
                            } else {
                                names.add(name);
                                cityAddress.add(gsmc);
                                points.add((Point) geometry);
                            }
                        }
                        nameMap.put(className, names);
                        gsmcMap.put(className, cityAddress);
                        pointMap.put(className, points);
                    }
                }
                loadData(index + 1, classs);
            }
        });
    }


    String sql = "NAME like '%$name%' OR GSMC  like '%$gsmc%'";
    String sql_W = "NAME = '$name' And GSMC  = '$gsmc'";
    String sql_data = "class = '$class' ";

    private String splitSQL(String value) {
        String o = sql.replace("$name", value);
        String t = o.replace("$gsmc", value);
        return t;
    }

    private String splitSQL(String value, String gsmc) {
        String o = sql_W.replace("$name", value);
        String t = o.replace("$gsmc", gsmc);
        return t;
    }

    private String splitdataSQL(String value) {
        String o = sql_data.replace("$class", value);
        return o;
    }

}
