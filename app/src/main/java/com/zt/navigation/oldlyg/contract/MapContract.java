package com.zt.navigation.oldlyg.contract;

import com.esri.core.geometry.Point;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteResult;

import java.util.Iterator;

public class MapContract {
    public interface View {
        void search_Success(int type,Iterator<Object> iterator);
        void search_Fail(int type,String text);

        void queryDirections_Success(RouteResult mResults,Point start, Point end);
        void queryDirections_Fail(String msg);

        void navigation_fail(String msg);
        void navigation_success(Route route, String msg);

    }

    public interface Presenter {
        void search(int type,String text);
        void queryDirections(Point start, Point end,String stopName);
        void navigation(Point start, Point end,String stopName);
    }

    public interface Model {

    }
}
