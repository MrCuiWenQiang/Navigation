package com.zt.navigation.oldlyg.presenter;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteDirection;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.contract.AddressListContract;
import com.zt.navigation.oldlyg.contract.NavigationContract;

import java.text.DecimalFormat;
import java.util.List;

import cn.faker.repaymodel.mvp.BaseMVPPresenter;
import cn.faker.repaymodel.util.FileWUtil;
import cn.faker.repaymodel.util.db.DBThreadHelper;

public class NavigationPresenter extends BaseMVPPresenter<NavigationContract.View> implements NavigationContract.Presenter {
    private RouteTask mRouteTask = null;
    final SpatialReference wm = SpatialReference.create(4490);

    @Override
    public void queryDirections(final Point start, final Point end,String stopName) {
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
                rfaf.setFeatures(new Graphic[] { point1, point2 });
                rp.setReturnDirections(true);
                rp.setStops(rfaf);
                rp.setOutSpatialReference(wm);
                return  mRouteTask.solve(rp);
            }

            @Override
            protected void jobEnd(RouteResult o) {
                if (o==null){
                    getView().queryDirections_Fail("算路失败");
                }else {
                    getView().queryDirections_Success(o,start,end);
                }
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
                rfaf.setFeatures(new Graphic[] { point1, point2 });
                rp.setReturnDirections(true);
                rp.setStops(rfaf);
                rp.setOutSpatialReference(wm);
                return  mRouteTask.solve(rp);
            }

            @Override
            protected void jobEnd(RouteResult mResults) {
                if (mResults==null){
                    getView().navigation_fail("导航失败");
                }else {
                    Route curRoute = mResults.getRoutes().get(0);
                    List<RouteDirection> routeDirections = curRoute.getRoutingDirections();
                    StringBuffer sb = new StringBuffer();
                    // TODO: 2019/11/13 只取三个位置  现在只有长距离  未判断到达目的地
                    double size = 0;
                    if (routeDirections.size()>0){
                        RouteDirection r0 = routeDirections.get(0);
                        sb.append(r0.getText());
                        size = r0.getLength();
                    }
                    if (routeDirections.size()>1){
                        RouteDirection r1 = routeDirections.get(1);
                        sb.append(",");
                        sb.append(r1.getText());
                        size += r1.getLength();
                    }
                    if (routeDirections.size()>2){
                        RouteDirection r2 = routeDirections.get(2);
                        sb.append(",");
                        sb.append(r2.getText());
                        size += r2.getLength();
                    }
                    if (size>0){
                        DecimalFormat df = new DecimalFormat("#.00");
                        String str = df.format(size*1000);
                        sb.append(",距离"+str);
                        sb.append("米");
                    }
                    // TODO: 2019/12/10 记录器
                    FileWUtil.setAppendFile(sb.toString());

                    getView().navigation_success(curRoute,sb.toString());
                }
            }
        });
    }

}
