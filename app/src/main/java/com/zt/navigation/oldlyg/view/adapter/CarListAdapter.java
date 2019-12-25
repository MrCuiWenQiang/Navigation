package com.zt.navigation.oldlyg.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.model.webbean.CarListBean;

import java.util.Date;
import java.util.List;

import cn.faker.repaymodel.util.DateUtils;
import cn.faker.repaymodel.widget.view.BaseRecycleView;

public class CarListAdapter extends BaseRecycleView.BaseRecycleAdapter<CarListAdapter.CarViewHolder> {

    private List<CarListBean> cars;
    private BaseRecycleView.OnItemClickListener<String> onItemClickListener;

    public void setCars(List<CarListBean> cars) {
        this.cars = cars;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(BaseRecycleView.OnItemClickListener<String> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_carlist, viewGroup, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder carViewHolder, int i) {
        CarListBean bean = cars.get(i);
        carViewHolder.tv_taskno.setText(bean.getTaskno());
        carViewHolder.tv_operatetype.setText(bean.getOPERATETYPE());
        carViewHolder.tv_carno.setText(bean.getVEHICLE());
        carViewHolder.tv_dep.setText(bean.getDEPARTMENT());
        Date time = bean.getAUDITTIME();
        if (time != null) {
            carViewHolder.tv_time.setText(DateUtils.dateToString(time,DateUtils.DATE_TIME_FORMAT));
        }
        if (onItemClickListener != null) {
            carViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, String.valueOf(bean.getID()), i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cars == null ? 0 : cars.size();
    }

    protected class CarViewHolder extends RecyclerView.ViewHolder {
        TextView tv_taskno;
        TextView tv_operatetype;
        TextView tv_time;
        TextView tv_carno;
        TextView tv_dep;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_taskno = itemView.findViewById(R.id.tv_taskno);
            tv_operatetype = itemView.findViewById(R.id.tv_operatetype);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_carno = itemView.findViewById(R.id.tv_carno);
            tv_dep = itemView.findViewById(R.id.tv_dep);
        }
    }
}
