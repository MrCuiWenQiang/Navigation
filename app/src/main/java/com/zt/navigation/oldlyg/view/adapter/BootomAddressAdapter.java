package com.zt.navigation.oldlyg.view.adapter;

import com.esri.core.geometry.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zt.navigation.oldlyg.R;

import java.util.List;
import java.util.Map;

import cn.faker.repaymodel.widget.view.BaseRecycleView;

/**
 * 底部地址选择
 */
public class BootomAddressAdapter extends RecyclerView.Adapter<BootomAddressAdapter.ViewHolder> {

    private List<String> suggestResults;
    private List<String> cityAddresss;
    private OnAddressItemClickListener onItemClickListener;
    private Map<String, Point> search_Data;

    public void setCityAddresss(List<String> cityAddresss) {
        this.cityAddresss = cityAddresss;
    }

    public void setData(List<String> suggestResults, Map<String, Point> search_Data) {
        if (this.suggestResults == null && suggestResults == null) {
            return;
        }
        this.suggestResults = suggestResults;
        this.search_Data = search_Data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnAddressItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bottom_addresss, viewGroup, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final String text = suggestResults.get(i);
        if (cityAddresss!=null&&cityAddresss.size()>i){
            String city = cityAddresss.get(i);
            viewHolder.tv_address.setText(city);
        }
        viewHolder.tv_title.setText(text);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(viewHolder.im_nv,search_Data.get(text),text,-1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestResults == null ? 0 : suggestResults.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_address;
        ImageView im_nv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_address = itemView.findViewById(R.id.tv_address);
            im_nv = itemView.findViewById(R.id.im_nv);
        }
    }

    public interface OnAddressItemClickListener{
        void onItemClick(View view, Point data,String name, int position);
    }
}
