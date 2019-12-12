package com.zt.navigation.oldlyg.contract;


public class SettingContract {
    public interface View {
        void settingCatType(String name);
        void settingCatType_Fail(int type,String name);
        void settingMapType(String name);
        void settingMapType_Fail(int type,String msg);
    }

    public interface Presenter {
        void settingCatType(int index,boolean value);
        void settingMapType(int index,boolean value);
    }

    public interface Model {

    }
}
