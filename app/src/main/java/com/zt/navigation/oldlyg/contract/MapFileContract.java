package com.zt.navigation.oldlyg.contract;

public class MapFileContract {
    public interface View {
        void showHint(int type, String hint, String btMsg);
    }

    public interface Presenter {
        void check();
    }

    public interface Model {

    }
}
