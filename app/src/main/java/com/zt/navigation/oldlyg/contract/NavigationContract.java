package com.zt.navigation.oldlyg.contract;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteResult;

import java.util.List;

public class NavigationContract {
    public interface View {
        void queryDirections_Success(RouteResult mResults, Point start, List<Geometry> endPoint);
        void queryDirections_Fail(String msg);

        void navigation_fail(String msg);
        void navigation_success(Route route, String msg);
        void navigation_arrive(double surplus);
    }

    public interface Presenter {
        void queryDirections(Point start, Geometry end, String stopName);

        void navigation(Point start, Geometry end,String stopName);

        void updateLocation(double lan,double lon);
    }

    public interface Model {

    }
}
