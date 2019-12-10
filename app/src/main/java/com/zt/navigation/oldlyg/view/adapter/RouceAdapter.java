package com.zt.navigation.oldlyg.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zt.navigation.oldlyg.R;


public class RouceAdapter extends RecyclerView.Adapter<RouceAdapter.ViewHolder>{
    private String[] addresss;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rouce,viewGroup,false));
    }

    public void setAddresss(String[] addresss) {
        this.addresss = addresss;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tv_name.setText(addresss[i]);
    }

    @Override
    public int getItemCount() {
        return addresss==null?0:addresss.length;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
