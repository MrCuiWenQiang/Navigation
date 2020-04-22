package com.zt.navigation.oldlyg.contract;


import com.esri.core.tasks.ags.find.FindResult;
import com.zt.navigation.oldlyg.model.bean.HistoryBean;

import java.util.Iterator;
import java.util.List;

public class SearchContract {
    public interface View {
        void search_Success(int type, List<FindResult> datas);

        void search_Fail(int type, String text);

        void queryHistory(List<HistoryBean> historyDatas);
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
