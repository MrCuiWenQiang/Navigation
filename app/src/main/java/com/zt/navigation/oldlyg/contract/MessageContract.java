package com.zt.navigation.oldlyg.contract;

import com.zt.navigation.oldlyg.model.webbean.MessageBean;

import java.util.List;

public class MessageContract {
    public interface View {
        void loadDataSuccess(List<MessageBean> datas);
        void loadDataFail(String message);
    }

    public interface Presenter {
        void loadData();
    }

    public interface Model {

    }
}
