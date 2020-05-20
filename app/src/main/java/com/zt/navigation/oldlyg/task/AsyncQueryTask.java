package com.zt.navigation.oldlyg.task;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.esri.core.geometry.Point;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;

import cn.faker.repaymodel.util.error.ErrorUtil;

public class AsyncQueryTask extends AsyncTask<Object, Void, FeatureResult> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected FeatureResult doInBackground(Object... params) {
        String url = (String) params[0];
        Point point = (Point) params[1];
        String whereClause = (String) params[2];

        //创建查询参数对象
        QueryParameters parameters = new QueryParameters();

        //设置识别位置
        if (point != null) {
            parameters.setGeometry(point);
        }

        //指定是否返回几何对象
        parameters.setReturnGeometry(true);

        //设置查询条件
        if (!TextUtils.isEmpty(whereClause)) {
            parameters.setWhere(whereClause);
        }
        //设置空间参考坐标系
        //SpatialReference sr = SpatialReference.create(4326);

        //设置输出坐标系
        //parameters.setOutSpatialReference(sr);

        //设置返回字段内容
        parameters.setOutFields(new String[]{"*"});

        //        parameters.setOutFields(new String[] { "县名称_1", "乡名称_1", "村名称_1",
        //                "地块名称", "统一编号" });

        //查询
        QueryTask queryTask = new QueryTask(url);
        try {
            FeatureResult featureResult = queryTask.execute(parameters);
            return featureResult;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorUtil.showError(e);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(FeatureResult result) {
        super.onPostExecute(result);

        //接口回调返回数据
        if (onReturnDataListener != null) {
            onReturnDataListener.onReturnData(result);
        }
    }

    public interface OnReturnDataListener {
        void onReturnData(FeatureResult result);
    }

    private OnReturnDataListener onReturnDataListener;

    public void setOnReturnDataListener(OnReturnDataListener onReturnDataListener) {
        this.onReturnDataListener = onReturnDataListener;
    }
}
