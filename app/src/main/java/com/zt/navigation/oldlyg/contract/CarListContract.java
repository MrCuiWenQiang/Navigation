package com.zt.navigation.oldlyg.contract;


import com.zt.navigation.oldlyg.model.webbean.CarListBean;

import java.util.List;

public class CarListContract {
    public interface View {
        void loadData_success(List<CarListBean> cars );
        void loadData_fail(int status, String message);
    }

    public interface Presenter {
        /**
         *
         * @param token
         * @param userId 类型（1-车号 2-卡号）
         * @param userName 车号或卡号
         */
        void loadData(String token,String userId,String userName);
    }

    public interface Model {

    }
}
