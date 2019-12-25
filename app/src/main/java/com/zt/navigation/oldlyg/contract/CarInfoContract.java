package com.zt.navigation.oldlyg.contract;


import com.zt.navigation.oldlyg.model.webbean.CarInfoBean;

public class CarInfoContract {
    public interface View {
        void info_Success(CarInfoBean carInfoBean);
        void info_Fail(String msg);
    }

    public interface Presenter {
        void loadInfo(String id);
    }

    public interface Model {

    }
}
