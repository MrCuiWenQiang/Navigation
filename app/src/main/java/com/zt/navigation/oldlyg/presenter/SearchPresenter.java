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

    String[] fields = new String[]{"name", "ZGGSDM"};
    //    String condition ;
    int[] layerIds = new int[]{19, 20, 21, 22, 29, 33, 34, 36, 37, 38, 39, 64};

    /*   @Override
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

       }*/
    @Override
    public void search(final int type, String text) {
        search(type, text, null);
    }

    public void search(final int type, String text, String gsmc) {
        if (TextUtils.isEmpty(text)) {
            getView().search_Fail(type, "");
            return;
        }
        String sql ;
        if ((type == SearchActivity.QUERY_TYPE_OK || type == SearchActivity.QUERY_TYPE_OK_HIST)
                && !TextUtils.isEmpty(gsmc)) {
            sql = splitSQL(text,gsmc);

        }else {
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
                                if (search_Data.containsKey(name)){
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

    //    String sql = "UNAME ='$name' AND GKZYQDM  = '$code'";
    String sql = "NAME like '%$name%' OR GSMC  like '%$gsmc%'";
    String sql_W = "NAME = '$name' And GSMC  = '$gsmc'";

    private String splitSQL(String value) {
        String o = sql.replace("$name", value);
        String t = o.replace("$gsmc", value);
        return t;
    }

    private String splitSQL(String value,String gsmc) {
        String o = sql_W.replace("$name", value);
        String t = o.replace("$gsmc", gsmc);
        return t;
    }
}
