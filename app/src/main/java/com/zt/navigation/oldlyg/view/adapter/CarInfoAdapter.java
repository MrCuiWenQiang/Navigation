package com.zt.navigation.oldlyg.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.model.webbean.CarInfoBean;

import java.util.List;

public class CarInfoAdapter extends RecyclerView.Adapter<CarInfoAdapter.ViewHolder> {

    private List<CarInfoBean.Address> data;

    public void setData(List<CarInfoBean.Address> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public CarInfoBean.Address getTopData(){
        return data==null||data.size()<=0?null:data.get(0);
    }
    public List<CarInfoBean.Address> getData(){
        return data;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_carinfo, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CarInfoBean.Address address = data.get(i);
        viewHolder.tv_title.setText("目的地" + (i + 1));
        viewHolder.tv_department.setText(address.getCODE_DEPARTMENT());
        viewHolder.tv_storage.setText(address.getSTORAGE());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_department;
        TextView tv_storage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_department = itemView.findViewById(R.id.tv_department);
            tv_storage = itemView.findViewById(R.id.tv_storage);
        }
    }
}
