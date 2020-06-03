package com.zt.navigation.oldlyg.contract;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteResult;
import com.zt.navigation.oldlyg.model.webbean.XSBean;

import java.util.List;

public class NavigationMultiContract {
    public interface View {
        void queryDirections_Success(RouteResult mResults, Point start, Geometry endPoint);
        void queryDirections_Fail(String msg);

        void navigation_fail(String msg);
        void navigation_success(Route route, String msg);
        void navigation_arrive(double surplus);

        void showXS(XSBean minPoint);
    }

    public interface Presenter {
        void queryDirections(Point start, Geometry end, String stopName);

        void navigation(Point start, Geometry end, String stopName);

        void updateLocation(double lan, double lon);
    }

    public interface Model {

    }
}
