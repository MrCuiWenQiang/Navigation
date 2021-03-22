package com.zt.navigation.oldlyg.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esri.core.geometry.Point;
import com.zt.navigation.oldlyg.R;

import java.util.List;
import java.util.Map;

public class TreeEndAdapter extends RecyclerView.Adapter<TreeEndAdapter.EndViewHolder> {
    List<String> names;
    List<String> gsmcs ;
    List<Point> points;
    private OnItemListener onItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public TreeEndAdapter(List<String> names, List<String> gsmcs, List<Point> points) {
        this.names = names;
        this.gsmcs = gsmcs;
        this.points = points;
    }

    @NonNull
    @Override
    public EndViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tree_end,viewGroup,false);
        return new EndViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EndViewHolder titleViewHolder, int i) {
        String name = "";
        if (gsmcs.get(i)!=null){
            name=name+ gsmcs.get(i);
        }
        if (names.get(i)!=null){
            name=name+"  "+ names.get(i);
        }


        titleViewHolder.tv_title.setText(name);
        titleViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemListener.onClick(names.get(i),points.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return names==null?0:names.size();
    }


    protected class EndViewHolder extends RecyclerView.ViewHolder{

        TextView tv_title;

        public EndViewHolder(@NonNull View convertView) {
            super(convertView);
            tv_title = convertView.findViewById(R.id.tv_title);
        }
    }

    public interface OnItemListener{
        void onClick(String name ,Point point);
    }
}
