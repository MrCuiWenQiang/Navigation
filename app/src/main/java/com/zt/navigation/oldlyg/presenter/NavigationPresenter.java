package com.zt.navigation.oldlyg.presenter;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteDirection;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.NavigationContract;
import com.zt.navigation.oldlyg.model.LocationUploadModel;
import com.zt.navigation.oldlyg.task.AsyncQueryTask;
import com.zt.navigation.oldlyg.util.TokenManager;
import com.zt.navigation.oldlyg.util.TpkUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.util.FileWUtil;
import cn.faker.repaymodel.util.db.DBThreadHelper;

public class NavigationPresenter extends BaseMVPPresenter<NavigationContract.View> implements NavigationContract.Presenter {
    private RouteTask mRouteTask = null;

    private List<StopGraphic> hinderList = null;
    private StopGraphic[] hinderdatas = null;

    final SpatialReference wm = SpatialReference.create(4490);

    public String getPath() {
        return TpkUtil.getPath();
    }

    /**
     * 查询障碍点
     */
    public void queryHinder() throws InterruptedException {

    }

    @Override
    public void queryDirections(final Point start, final Point end, String stopName) {
        if (mRouteTask == null) {
            try {
                mRouteTask = RouteTask
                        .createOnlineRouteTask(
                                Urls.mapNaviUrl,
                                null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        hinderList = new ArrayList<>();
        AsyncQueryTask asyncQueryTask = new AsyncQueryTask();
        asyncQueryTask.execute(Urls.searchHinderUrl, null, "OBJECTID LIKE '%%'");
        asyncQueryTask.setOnReturnDataListener(new AsyncQueryTask.OnReturnDataListener() {
            @Override
            public void onReturnData(FeatureResult result) {
                if (result != null) {
                    Iterator<Object> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            Feature feature = (Feature) iterator.next();
//                            Map<String, Object> attributes = feature.getAttributes();
                            Geometry geometry = feature.getGeometry();
                            if (geometry != null) {
                                StopGraphic hinder = new StopGraphic(geometry);
                                hinderList.add(hinder);
                            }
                        }
                        if (hinderList.size() > 0) {
                            hinderdatas = hinderList.toArray(new StopGraphic[]{});
                        }
                    }
                }
                DBThreadHelper.startThreadInPool(new DBThreadHelper.ThreadCallback<RouteResult>() {
                    @Override
                    protected RouteResult jobContent() throws Exception {
                        RouteParameters rp = mRouteTask
                                .retrieveDefaultRouteTaskParameters();
                        NAFeaturesAsFeature rfaf = new NAFeaturesAsFeature();

                        StopGraphic point1 = new StopGraphic(start);
                        point1.setName("当前位置");
                        StopGraphic point2 = new StopGraphic(end);
                        point2.setName(stopName);
                        rfaf.setFeatures(new Graphic[]{point1, point2});
                        rp.setReturnDirections(true);
                        rp.setStops(rfaf);
                        rp.setOutSpatialReference(wm);
                        if (hinderList.size() > 0) {
                            NAFeaturesAsFeature hindf = new NAFeaturesAsFeature();
                            hindf.addFeatures(hinderdatas);
                            rp.setPointBarriers(hindf);
                        }
                        return mRouteTask.solve(rp);
                    }

                    @Override
                    protected void jobEnd(RouteResult o) {
                        if (o == null) {
                            getView().queryDirections_Fail("算路失败:无法规划路线");
                        } else {
                            getView().queryDirections_Success(o, start, end);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void navigation(Point start, Point end, String stopName) {
        if (mRouteTask == null) {
            try {
                mRouteTask = RouteTask
                        .createOnlineRouteTask(
                                Urls.mapNaviUrl,
                                null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DBThreadHelper.startThreadInPool(new DBThreadHelper.ThreadCallback<RouteResult>() {
            @Override
            protected RouteResult jobContent() throws Exception {
                RouteParameters rp = mRouteTask
                        .retrieveDefaultRouteTaskParameters();
                NAFeaturesAsFeature rfaf = new NAFeaturesAsFeature();

                StopGraphic point1 = new StopGraphic(start);
                point1.setName("当前位置");
                StopGraphic point2 = new StopGraphic(end);
                point2.setName(stopName);
                rfaf.setFeatures(new Graphic[]{point1, point2});
                rp.setReturnDirections(true);
                rp.setStops(rfaf);
                rp.setOutSpatialReference(wm);
                if (hinderList.size() > 0) {
                    NAFeaturesAsFeature hindf = new NAFeaturesAsFeature();
                    hindf.addFeatures(hinderdatas);
                    rp.setPointBarriers(hindf);
                }
                return mRouteTask.solve(rp);
            }

            @Override
            protected void jobEnd(RouteResult mResults) {
                if (mResults == null) {
                    getView().navigation_fail("导航失败");
                } else {
                    Route curRoute = mResults.getRoutes().get(0);
                    List<RouteDirection> routeDirections = curRoute.getRoutingDirections();
                    StringBuffer sb = new StringBuffer();
                    double size = 0;
                    if (routeDirections.size() > 0) {
                        RouteDirection r0 = routeDirections.get(0);
                        sb.append(r0.getText());
                        size = r0.getLength();
                    }
                    if (routeDirections.size() > 1) {
                        RouteDirection r1 = routeDirections.get(1);
                        sb.append(",");
                        sb.append(r1.getText());
                        size += r1.getLength();
                    }
                    if (routeDirections.size() > 2) {
                        RouteDirection r2 = routeDirections.get(2);
                        sb.append(",");
                        sb.append(r2.getText());
                        size += r2.getLength();
                    }
                    DecimalFormat df = new DecimalFormat("#.00");
//                    String str = df.format(size * 1000);
                    String str = df.format(size );
                    sb.append(",距离" + str);
                    sb.append("米");
                    double surplus = Double.valueOf(str);
                    if (surplus <= 200) {
                        getView().navigation_arrive(surplus);//距离目的地小于200米
                    }
                    // TODO: 2019/12/10 记录器
                    FileWUtil.setAppendFile(sb.toString());

                    getView().navigation_success(curRoute, sb.toString());
                }
            }
        });
    }



    private LocationUploadModel uploadModel = new LocationUploadModel();

    @Override
    public void updateLocation(double lan, double lon) {
        uploadModel.upload(TokenManager.token, lon, lan, null);
    }

}
