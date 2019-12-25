package com.zt.navigation.oldlyg.contract;

import com.esri.core.geometry.Point;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteResult;

public class NavigationContract {
    public interface View {
        void queryDirections_Success(RouteResult mResults, Point start, Point end);
        void queryDirections_Fail(String msg);

        void navigation_fail(String msg);
        void navigation_success(Route route, String msg);
        void navigation_arrive(double surplus);
    }

    public interface Presenter {
        void queryDirections(Point start, Point end, String stopName);

        void navigation(Point start, Point end,String stopName);

        void updateLocation(double lan,double lon);
    }

    public interface Model {

    }
}
