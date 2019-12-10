package com.zt.navigation.oldlyg.view.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.model.bean.HistoryBean;

import java.util.List;

import cn.faker.repaymodel.util.UiTools;
import cn.faker.repaymodel.widget.view.BaseRecycleView;

public class HistoryAdapter extends BaseRecycleView.BaseRecycleAdapter<RecyclerView.ViewHolder> {
    private List<HistoryBean> historyDatas;
    private int dataLength = 0;

    private final int TYPE_CODE_TOP = 0;
    private final int TYPE_CODE_CONTENT = 1;
    private final int TYPE_CODE_BOTTOM = 2;

    private int textSize = 6;

    private OnItemClick onItemClick;

    public void setHistoryDatas(List<HistoryBean> historyDatas) {
        this.historyDatas = historyDatas;
        if (historyDatas != null) {
            dataLength = historyDatas.size();
        }
        notifyDataSetChanged();
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (position == 0) {
            type = TYPE_CODE_TOP;
        } else if (position > dataLength) {
            type = TYPE_CODE_BOTTOM;
        } else {
            type = TYPE_CODE_CONTENT;
        }
        return type;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (type) {
            case TYPE_CODE_TOP: {
                View view = new RelativeLayout(viewGroup.getContext());
                viewHolder = new TopViewHolder(view);
                break;
            }
            case TYPE_CODE_CONTENT: {
                View view = LayoutInflater
                        .from(viewGroup.getContext()).inflate(R.layout.item_history, viewGroup, false);
                viewHolder = new HistoryViewHolder(view);
                break;
            }
            case TYPE_CODE_BOTTOM: {
                View view = new RelativeLayout(viewGroup.getContext());
                viewHolder = new BottomViewHolder(view);
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof HistoryViewHolder) {
            HistoryViewHolder histHolder = (HistoryViewHolder) viewHolder;
            HistoryBean historyBean = historyDatas.get(i - 1);
            histHolder.tv_value.setText(historyBean.getName());
            histHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onClick(i-1,historyBean.getName());
                }
            });
        } else if (viewHolder instanceof TopViewHolder) {
            ((TopViewHolder) viewHolder).tv_title.setText("历史纪录");
        } else if (viewHolder instanceof BottomViewHolder) {
            ((BottomViewHolder) viewHolder).tv_clear.setText("清除记录");
            ((BottomViewHolder) viewHolder).tv_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onClear();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return historyDatas == null||historyDatas.size()==0 ? 0 : historyDatas.size() + 2;
    }

    protected class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tv_value;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_value = itemView.findViewById(R.id.tv_value);
        }
    }

    //顶部标题
    protected class TopViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;

        public TopViewHolder(@NonNull View itemView) {
            super(itemView);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(UiTools.px2dip(itemView.getContext(), 3), UiTools.px2dip(itemView.getContext(), 5), 0, UiTools.px2dip(itemView.getContext(), 5));
            itemView.setLayoutParams(params);

            RelativeLayout.LayoutParams tvparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv_title = new TextView(itemView.getContext());
            tv_title.setLayoutParams(tvparams);
            tv_title.setGravity(Gravity.CENTER);
            tv_title.setTextSize(UiTools.sp2px(itemView.getContext(), textSize));
            ((ViewGroup) itemView).addView(tv_title);
        }
    }

    //底部清除历史纪录
    protected class BottomViewHolder extends RecyclerView.ViewHolder {
        TextView tv_clear;

        public BottomViewHolder(@NonNull View itemView) {
            super(itemView);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setPadding(0, 5, 0, 5);
            params.setMargins(0, UiTools.px2dip(itemView.getContext(), 12), 0, 0);
            itemView.setLayoutParams(params);

            RelativeLayout.LayoutParams tvparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv_clear = new TextView(itemView.getContext());
            tvparams.addRule(RelativeLayout.CENTER_IN_PARENT);
            tv_clear.setLayoutParams(tvparams);
            tv_clear.setGravity(Gravity.CENTER);
            tv_clear.setTextSize(UiTools.sp2px(itemView.getContext(), textSize));
            tv_clear.setTextColor(ContextCompat.getColor(tv_clear.getContext(), R.color.viewfinder_mask));
            ((ViewGroup) itemView).addView(tv_clear);
        }
    }


    public interface OnItemClick{
        void onClick(int position,String txt);
        void onClear();
    }
}
