package com.zt.navigation.oldlyg.contract;


public class LoginContract {
    public interface View {
        void init_data(String carHead,String carNo);

        void login_Success(String msg);

        void login_Fail(String msg);
    }

    public interface Presenter {
        void login(String carHead,String carNo);
    }

    public interface Model {

    }
}
