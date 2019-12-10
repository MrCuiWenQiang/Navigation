package com.zt.navigation.oldlyg.view.adapter.manager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

public class HistoryManager extends GridLayoutManager {
    public HistoryManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public HistoryManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public HistoryManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }


}
