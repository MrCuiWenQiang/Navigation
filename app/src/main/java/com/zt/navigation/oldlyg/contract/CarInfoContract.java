package com.zt.navigation.oldlyg.contract;



import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.tasks.ags.find.FindResult;
import com.zt.navigation.oldlyg.model.webbean.CarInfoBean;

import java.util.List;

public class CarInfoContract {
    public interface View {
        void info_Success(CarInfoBean carInfoBean);
        void info_Fail(String msg);

        void searchAddresss_Fail(int type, String msg);

        void searchAddresss_Success(int type,String name, Geometry findResult);
    }

    public interface Presenter {
        void loadInfo(String id);
         void searchAddress(String name, String code);
    }

    public interface Model {

    }
}
