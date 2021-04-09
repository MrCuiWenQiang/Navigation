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
import com.zt.navigation.oldlyg.model.webbean.XSBean;
import com.zt.navigation.oldlyg.task.AsyncQueryTask;
import com.zt.navigation.oldlyg.util.MapUtil;
import com.zt.navigation.oldlyg.util.navigation.RouteCore;
import com.zt.navigation.oldlyg.util.TokenManager;
import com.zt.navigation.oldlyg.util.TpkUtil;
import com.zt.navigation.oldlyg.util.UrlUtil;
import com.zt.navigation.oldlyg.util.navigation.ThreadManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.util.FileWUtil;
import cn.faker.repaymodel.util.LogUtil;
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

    private List<XSBean> xsBeans;

    /**
     * 只在第一次加载才获取网络资源
     *
     * @param start
     */
    public void findXS(Point start) {
        if (xsBeans == null) {
            queryxs(start);
        } else {
            distance(start);
        }
    }

    private void queryxs(Point point) {
        AsyncQueryTask asyncQueryTask = new AsyncQueryTask();
        asyncQueryTask.execute(Urls.xspmapUrl, null, "class='限速牌'");
        asyncQueryTask.setOnReturnDataListener(new AsyncQueryTask.OnReturnDataListener() {
            @Override
            public void onReturnData(FeatureResult result) {
                if (result != null) {
                    Iterator<Object> iterator = result.iterator();
                    if (iterator.hasNext()) {
                        xsBeans = new ArrayList<>();
                        while (iterator.hasNext()) {
                            Feature feature = (Feature) iterator.next();
                            Map<String, Object> attributes = feature.getAttributes();
                            Set<String> set = attributes.keySet();
                            XSBean item = new XSBean();
                            boolean isAdd = true;
                            for (String key : set) {
                                try {
                                    if (key.equals("X")) {
                                        item.setX((Double) attributes.get(key));
                                    } else if (key.equals("Y")) {
                                        item.setY((Double) attributes.get(key));
                                    } else if (key.equals("INFORMATION")) {
                                        item.setContent((String) attributes.get(key));
                                    }
                                } catch (NullPointerException e) { //三项有null存在就不再循环 且不存入
                                    isAdd = false;
                                    break;
                                }
                            }
                            if (isAdd) {
                                xsBeans.add(item);
                            }
                        }
                        distance(point);
                    }
                }
            }
        });
    }

    /**
     * 计算距离最近的点
     *
     * @param point
     */
    private void distance(Point point) {
        double minLength = -1;
        XSBean minPoint = null;
        for (XSBean item : xsBeans) {
            double w = Math.pow(item.getX() - point.getX(), 2);
            double h = Math.pow(item.getY() - point.getY(), 2);
            double l = w + h;
            if (minLength <= 0 || l < minLength) {
                minLength = l;
                minPoint = item;
            }
        }
        if (minPoint != null && getView() != null) {
            getView().showXS(minPoint);
        }
    }

    @Override
    public void queryDirections(final Point start, final Geometry end, String stopName) {
        ArrayList list = new ArrayList<Geometry>();
        list.add(end);
        queryDirections(start, list, stopName);
    }

    public void queryDirections(final Point start, final List<Geometry> ends, String stopName) {
        if (mRouteTask == null) {
            try {
                mRouteTask = RouteTask
                        .createOnlineRouteTask(
                                UrlUtil.getCarNavi(),
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
                        MapUtil.movenPoint(start);
                        StopGraphic point1 = new StopGraphic(start);
                        point1.setName("当前位置");

                        Graphic[] points = new Graphic[ends.size() + 1];
                        points[0] = point1;
                        int i = 1;
                        for (Geometry item : ends) {
                            StopGraphic point2 = new StopGraphic(item);
                            point2.setName(stopName);
                            points[i] = point2;
                            i++;
                        }

                        rfaf.setFeatures(points);
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
                            getView().queryDirections_Success(o, start, ends);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void navigation(Point start, Geometry end, String stopName) {
        ArrayList list = new ArrayList<Geometry>();
        list.add(end);
        navigation(start, list, stopName);
    }


    public void navigation(Point start, List<Geometry> ends, String stopName) {
        findXS(start);
        if (mRouteTask == null) {
            try {
                mRouteTask = RouteTask
                        .createOnlineRouteTask(
                                UrlUtil.getCarNavi(),
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
                MapUtil.movenPoint(start);
                StopGraphic point1 = new StopGraphic(start);
                point1.setName("当前位置");
                Graphic[] points = new Graphic[ends.size() + 1];
                points[0] = point1;
                int i = 1;
                for (Geometry item : ends) {
                    StopGraphic point2 = new StopGraphic(item);
                    point2.setName(stopName);
                    points[i] = point2;
                    i++;
                }
                rfaf.setFeatures(points);
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
                    navigateJob = new ThreadManager(routeDirections, new ThreadManager.OnNavigateListen() {
                        @Override
                        public void speak(String msg) {

                            FileWUtil.setAppendFile(msg);
                            if (getView() != null) {
                                getView().navigation_success(curRoute, msg);
                            }
                        }
                    });
                    navigateJob.start();
                }
            }
        });
    }

    private ThreadManager navigateJob;

    private LocationUploadModel uploadModel = new LocationUploadModel();

    @Override
    public void updateLocation(double lan, double lon) {
        uploadModel.upload(TokenManager.token, lon, lan, null);
    }

    public void stopNavigation() {
        if (navigateJob!=null){
            navigateJob.exit();
        }
    }
}
