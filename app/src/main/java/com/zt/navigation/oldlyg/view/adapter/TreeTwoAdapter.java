package com.zt.navigation.oldlyg.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esri.core.geometry.Point;
import com.zt.navigation.oldlyg.R;

import java.util.List;
import java.util.Map;

public class TreeTwoAdapter extends RecyclerView.Adapter<TreeTwoAdapter.TitleViewHolder> {
    private String[] classs;
    private Map<String, List<String>> nameMap;
    private Map<String, List<String>> gsmcMap;
    private Map<String, List<Point>> pointMap;

    private TreeEndAdapter.OnItemListener onItemListener;

    public void setOnItemListener(TreeEndAdapter.OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public TitleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tree, viewGroup, false);
        return new TitleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TitleViewHolder titleViewHolder, int i) {
        String className = classs[i];
        List<String> names = nameMap.get(className);
        List<String> gsmcs = gsmcMap.get(className);
        List<Point> points = pointMap.get(className);

        titleViewHolder.tvTitle.setText(className);
        titleViewHolder.rvDatas.setLayoutManager(new LinearLayoutManager(titleViewHolder.itemView.getContext()));
        TreeEndAdapter adapter = new TreeEndAdapter(names, gsmcs, points);
        adapter.setOnItemListener(onItemListener);
        titleViewHolder.rvDatas.setAdapter(adapter);
        titleViewHolder.rvDatas.setVisibility(View.GONE);
        titleViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleViewHolder.rvDatas.getVisibility() == View.GONE) {
                    titleViewHolder.rvDatas.setVisibility(View.VISIBLE);
                } else {
                    titleViewHolder.rvDatas.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return classs == null ? 0 : classs.length;
    }

    public void setDatas(String[] classs, Map<String, List<String>> nameMap, Map<String, List<String>> gsmcMap, Map<String, List<Point>> pointMap) {
        this.classs = classs;
        this.nameMap = nameMap;
        this.gsmcMap = gsmcMap;
        this.pointMap = pointMap;
        notifyDataSetChanged();
    }

    protected class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        RecyclerView rvDatas;


        public TitleViewHolder(@NonNull View convertView) {
            super(convertView);
            tvTitle = convertView.findViewById(R.id.tv_title);
            rvDatas = convertView.findViewById(R.id.rv_datas);
        }
    }
}
