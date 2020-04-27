package com.zt.navigation.oldlyg.contract;


import com.zt.navigation.oldlyg.model.webbean.CarInfoBean;

public class AddressSettingContract {
    public interface View {

        void save_fail(String s);
        void save_Success(String s);
    }

    public interface Presenter {
        void save(String netAddress,String gisAddress);
    }

    public interface Model {

    }
}
