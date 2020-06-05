package com.zt.navigation.oldlyg.contract;


public class SettingContract {
    public interface View {
        void settingCatType(String name);
        void settingCatType_Fail(int type,String name);
        void settingMapType(String name,boolean value);
        void settingMapType_Fail(int type,String msg);
        void showUsers(String[] values);
        void settingUser_Success(String msg);
        void settingUser_Fail(String msg);
        void uploadArriveSuccess(String msg);
        void uploadArriveFail(String msg);
        void showArrivefinal(String message);
    }

    public interface Presenter {
        void uploadArrive();
        void updateLocation(double lan,double lon);
        void settingCatType(int index,boolean value);
        void settingMapType(int index,boolean value);
        void showUsers();
        void settingUser(String sv);
    }

    public interface Model {

    }
}
