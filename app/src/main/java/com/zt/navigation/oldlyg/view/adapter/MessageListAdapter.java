package com.zt.navigation.oldlyg.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.model.webbean.MessageBean;

import java.util.Date;
import java.util.List;

import cn.faker.repaymodel.util.DateUtils;
import cn.faker.repaymodel.widget.view.BaseRecycleView;

public class MessageListAdapter extends BaseRecycleView.BaseRecycleAdapter<MessageListAdapter.CarViewHolder> {

    private List<MessageBean> daras;
    private BaseRecycleView.OnItemClickListener<String> onItemClickListener;

    public void daras(List<MessageBean> daras) {
        this.daras = daras;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(BaseRecycleView.OnItemClickListener<String> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder carViewHolder, int i) {
        MessageBean bean = daras.get(i);
        carViewHolder.tv_content.setText(bean.getCONTENT());
//        String time = bean.getPUSHTIME();
        carViewHolder.tv_pushtime.setText(bean.getPUSHTIME());
//        if (time != null) {DateUtils.stringToDate(time,DateUtils.DATE_TIME_FORMAT)
//            carViewHolder.tv_pushtime.setText(DateUtils.stringToDate(time,DateUtils.DATE_TIME_FORMAT).toString());
//        }
    /*    if (onItemClickListener != null) {
            carViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, String.valueOf(bean.getID()), i);
                }
            });
        }*/
    }

    @Override
    public int getItemCount() {
        return daras == null ? 0 : daras.size();
    }

    protected class CarViewHolder extends RecyclerView.ViewHolder {
        TextView tv_pushtime;
        TextView tv_content;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_pushtime = itemView.findViewById(R.id.tv_pushtime);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}
