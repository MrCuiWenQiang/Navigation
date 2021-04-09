package com.zt.navigation.oldlyg.util.navigation;

import android.support.annotation.NonNull;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.tasks.na.RouteDirection;
import com.zt.navigation.oldlyg.constant.NavigationNote;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.faker.repaymodel.util.LogUtil;

/**
 * 导航核心类
 * 将停用实时路径，使用本地算法进行导航
 */
public class RouteCore {
    private SpatialReference sr = SpatialReference.create(4490);
    private StringBuilder sb = new StringBuilder();
    private Set<String> routeSet = new LinkedHashSet<>();
    private List<RouteDirection> routes;

    public RouteCore(List<RouteDirection> routes) {
        this.routes = routes;
    }

    /**
     * @param nowPoint 当前位置
     *                 step 1: 首先根据当前点筛选路径,判断用户在导航路径的哪条路段。
     *                 若找到存在路径则返回路径 否则提示用户行驶至入口
     *                 <p>
     *                 New Question: 1.在导航途中定位偶有偏移如何处理
     *                 2.偏移路线又如何处理
     *                 Improve:需要把每个路段播报状态存储下来 以免重复播报
     *                 理想的播报模式是 进入新路段 播报一次路段名后 只需播报距离
     *                 下个路段还有多少M
     *                 <p>
     *                 需要加前提判断 1. 位置未变 / 移动范围小 就不进行语音播报
     *                 2.当前路段一结束 立即播报下个路段
     */
    public String screenRoute(Point nowPoint) {
        sb.delete(0, sb.length());
        Geometry pointCircle = getCircle(nowPoint, 0.0001, new Polygon());
        int routeIndex = -1;//依然为-1的话就脱离了导航路径
        for (int i = 1; i < routes.size(); i++) {
            RouteDirection rd = routes.get(i);
            Geometry geometry = rd.getGeometry();
            Boolean a = GeometryEngine.crosses(pointCircle, geometry, sr);//相交
            if (a) {
                routeIndex = i;
                break;
            } else if (i + 1 == routes.size()) {
                routeIndex = -1;//说明与所有路段无交集 即脱离路线
            }
        }

        if (routeIndex == -1) {
            sb.append(NavigationNote.error_1_text);
            return sb.toString();
        } else {
            RouteDirection r0 = routes.get(routeIndex);
            String routeName = r0.getText();
            if (!routeSet.contains(routeName)) {
                sb.append(routeName);
                routeSet.add(routeName);
            } else if (routeIndex + 1 < routes.size()) {
                RouteDirection r1 = routes.get(routeIndex + 1);
                String routeNewName = NavigationNote.error_route_text.replace(NavigationNote.sign_route_name, r1.getText());
                Geometry g1 = r1.getGeometry();
                double distance = GeometryEngine.distance(nowPoint, g1, sr);
                distance = DistanceUtil.sumTo(distance);
                int nd = (int) distance;
                String routeNewDistances = routeNewName.replace(NavigationNote.sign_distances, nd + "");
                sb.append(routeNewDistances);
            } else {
                sb.append("导航结束");
            }
        }
        LogUtil.e("navigation info", sb.toString());
        return sb.toString();
    }

    private Geometry getCircle(Point point, double radius, Polygon polygon) {
        polygon.setEmpty();
        //圆形的边线点集合
        Point[] points = getPoints(point, radius);
        polygon.startPath(points[0]);
        for (int i = 1; i < points.length; i++) {
            polygon.lineTo(points[i]);
        }
        return new Graphic(polygon, null).getGeometry();
    }

    /**
     * 通过中心点和半径计算得出圆形的边线点集合
     *
     * @param center
     * @param radius
     * @return
     */
    private static Point[] getPoints(Point center, double radius) {
        Point[] points = new Point[50];
        double sin;
        double cos;
        double x;
        double y;
        for (double i = 0; i < 50; i++) {
            sin = Math.sin(Math.PI * 2 * i / 50);
            cos = Math.cos(Math.PI * 2 * i / 50);
            x = center.getX() + radius * sin;
            y = center.getY() + radius * cos;
            points[(int) i] = new Point(x, y);
        }
        return points;
    }


}
