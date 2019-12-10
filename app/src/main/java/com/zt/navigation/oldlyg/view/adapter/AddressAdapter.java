package com.zt.navigation.oldlyg.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.zt.navigation.oldlyg.R;

import java.util.List;

import cn.faker.repaymodel.widget.view.BaseRecycleView;

/**
 * 地址搜索列表适配器
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<String> suggestResults;
    private BaseRecycleView.OnItemClickListener<String> onItemClickListener;

    public void setSuggestResults(List<String> suggestResults) {
        if ( this.suggestResults==null&&suggestResults==null){
            return;
        }
        this.suggestResults = suggestResults;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(BaseRecycleView.OnItemClickListener<String> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_addresss, viewGroup, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final String result = suggestResults.get(i);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(viewHolder.itemView,result,i);
                }
            }
        });
        viewHolder.tv_title.setText(result);
    }

    @Override
    public int getItemCount() {
        return suggestResults == null ? 0 : suggestResults.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }
}
