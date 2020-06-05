package com.zt.navigation.oldlyg.contract;


import com.esri.core.geometry.Point;
import com.esri.core.tasks.ags.find.FindResult;
import com.zt.navigation.oldlyg.model.bean.HistoryBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SearchContract {
    public interface View {

        void search_Fail(int type, String text);

        void queryHistory(List<HistoryBean> historyDatas);

        void search_Success(int type, Map<String,Point> search_data, ArrayList<String> names, ArrayList<String> cityAddress);
    }

    public interface Presenter {
        void addHistory(String text);

        void cleanHistory();

        void queryHistory();

        void search(int type, String text);
    }

    public interface Model {

    }
}
