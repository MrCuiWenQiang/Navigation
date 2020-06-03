package com.zt.navigation.oldlyg.view.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zt.navigation.oldlyg.R;

import java.util.List;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.ViewHolder> {
    private List<String> end_name;//终点名称

    private OnItemClickListener listener;

    int postion_s = 0;

    public void setEnd_name(List<String> end_name) {
        this.end_name = end_name;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_stops, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String name = end_name.get(i);
        viewHolder.tv_name.setText(name);
        if (i == postion_s) {
            viewHolder.tv_name.setTextColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.shallowblack));
            viewHolder.ll_group.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.se_bean));
        } else {
            viewHolder.tv_name.setTextColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.white));
            viewHolder.ll_group.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.se_bean_open));
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postion_s != i) {
                    postion_s = i;
                    notifyDataSetChanged();
                }
                listener.onclick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return end_name == null ? 0 : end_name.size();
    }

    public interface OnItemClickListener {
        void onclick(int postion);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_group;
        TextView tv_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_group = itemView.findViewById(R.id.ll_group);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
