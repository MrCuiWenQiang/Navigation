package cn.faker.repaymodel.widget.view.select;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.faker.repaymodel.R;
import cn.faker.repaymodel.util.ScreenUtil;
import cn.faker.repaymodel.widget.view.dialog.BasicDialog;

/**
 * 地址选择对话框 仿jd
 * MR.C
 */
public class AddressDialog extends BasicDialog implements View.OnClickListener {

    private LinearLayout tablegroup;//标签组

    private RecyclerView rv_list;
    private AddressAdapter addressAdapter;

//    private Map<Integer,String[]> valueMap = new HashMap<>();
    private SparseArray<String[]> valueMap = new SparseArray<>();//存储以前的值 并用层数做索引 减少重复请求次数
    private String[] tableNames;//标签组名称
    private int  maxCount;//标签组最大长度
    private int nowCount = 0;//当前标签
    private onSelectListener onSelectListener;//选择事件回调

    @Override
    public int getLayoutId() {
        return R.layout.dg_address;
    }

    @Override
    public void initview(View v) {
        tablegroup = v.findViewById(R.id.tablegroup);
        rv_list = v.findViewById(R.id.rv_list);

        rv_list.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        settingTables();
        addressAdapter = new AddressAdapter();
        addressAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadData(position);
            }
        });
        rv_list.setAdapter(addressAdapter);
        String[] v = onSelectListener.onLoadData(0,0);
        valueMap.put(nowCount,v);
        addressAdapter.setValues(v);
    }

    private void loadData(int position) {
        nowCount += 1;
        if (nowCount<maxCount){
            String[] v = onSelectListener.onLoadData(nowCount,position);
            valueMap.put(nowCount,v);
            addressAdapter.setValues(v);
            View nextv=tablegroup.getChildAt(nowCount);
            nextv.setVisibility(View.VISIBLE);
        }else {
            onSelectListener.finish(nowCount-1);
        }
    }

    private void settingTables() {
        if (maxCount<=0){
            return;
        }
        for (int i = 0; i < maxCount; i++) {
            View v = getTab(i);
            tablegroup.addView(v);
        }
    }

    private View getTab(int i) {
        TextView tab = new TextView(getContext());
        tab.setText(tableNames[i]);
        tab.setTag(i);
        tab.setOnClickListener(this);
        if (i!=0){
            tab.setVisibility(View.GONE);
        }
        return tab;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag()!=null){
            int index = (int) v.getTag();
            addressAdapter.setValues(valueMap.get(index));
        }
    }

    /**
     * 设置标签组
     * @param tableNames
     */
    public void setTableNames(String[] tableNames) {
        this.tableNames = tableNames;
        if (tableNames!=null){
            maxCount = tableNames.length;
        }
    }

    /**
     * 设置选择数据回调
     * @param onSelectListener
     */
    public void setOnSelectListener(AddressDialog.onSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    @Override
    protected void initLayoutParams(WindowManager.LayoutParams attributes) {
        attributes.gravity = Gravity.BOTTOM;
        attributes.width =  ViewGroup.LayoutParams.MATCH_PARENT;
        attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int getDialogHeght() {
        return ScreenUtil.getWindowHeight(getContext()) * 3 / 5;
    }



    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
        private String[] values;

        private AdapterView.OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public void setValues(String[] values) {
            this.values = values;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LinearLayout layout = new LinearLayout(getContext());
            ViewHolder vh = new ViewHolder(layout);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(null, v, i, -1);
                }
            });
            viewHolder.textView.setText(values[i]);
        }

        @Override
        public int getItemCount() {
            return values == null ? 0 : values.length;
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = new TextView(getContext());
                ((ViewGroup) itemView).addView(textView);
            }
        }
    }

    public interface onSelectListener{
        /**
         * @param tableIndex 第X层
         * @param index 选中的索引值
         * @return
         */
        String[] onLoadData(int tableIndex,int index);
        void finish(int index);
    }

}
