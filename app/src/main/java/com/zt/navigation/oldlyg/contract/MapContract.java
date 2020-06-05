package com.zt.navigation.oldlyg.contract;

import android.content.Context;

import com.esri.core.geometry.Point;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteResult;

import java.util.Iterator;

public class MapContract {
    public interface View {
        void showStartMapFile(String name);

        void showEndMapFile(String name);




        void toGetHinder_Success(String msg);

    }

    public interface Presenter {
        void mapFile(Context context);
        void updateLocation(double lan,double lon);
        void toGetHinder();
    }

    public interface Model {

    }
}
