package com.zt.navigation.oldlyg.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.zt.navigation.oldlyg.R;

import com.zt.navigation.oldlyg.contract.SettingContract;
import com.zt.navigation.oldlyg.presenter.SettingPresenter;
import com.zt.navigation.oldlyg.util.AppSettingUtil;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;
import cn.faker.repaymodel.util.ToastUtility;

/**
 * 设置页面
 */
public class SettingActivity extends BaseMVPAcivity<SettingContract.View, SettingPresenter> implements SettingContract.View, View.OnClickListener {

    private String[] item_one_name = new String[]{"路线模式", "地图模式"};
    private int[] one_ids = new int[]{R.id.one_1, R.id.one_2};
    private String[] item_two_name = new String[]{"车辆设置", "地图资源", "版本更新"};
    private int[] two_ids = new int[]{R.id.two_1, R.id.two_2, R.id.two_3, R.id.two_4};
    private String[] item_three_name = new String[]{"意见反馈", "使用说明"};
    private int[] three_ids = new int[]{R.id.three_1, R.id.three_2};
    private String[] item_one_describe;



    private QMUIGroupListView qmui_gl;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_setting;
    }

    @Override
    protected void initContentView() {
        qmui_gl = findViewById(R.id.qmui_gl);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setBackBackground(R.mipmap.fanhui_black);
        setTitle("设置中心");
        changStatusIconCollor(false);

        boolean cattype = AppSettingUtil.getCatType();
        boolean maptype = AppSettingUtil.getMapType();
        String cat_d = cattype ? "大车路线" : "小车路线";
        String map_d = maptype ? "离线加载" : "在线加载";
        item_one_describe = new String[]{cat_d, map_d};
        initList();
    }

    private void initList() {
        settingGroup("模式设置", QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_NONE, item_one_name, item_one_describe, one_ids);
        settingGroup("资源管理", QMUICommonListItemView.VERTICAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, item_two_name, null, two_ids);
        settingGroup("帮助相关", QMUICommonListItemView.VERTICAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, item_three_name, null, three_ids);

    }

    private void settingGroup(String title, @QMUICommonListItemView.QMUICommonListItemOrientation int orientation, @QMUICommonListItemView.QMUICommonListItemAccessoryType int type, String[] name, String[] describe, int[] ids) {
        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext()).setTitle(title);
        for (int i = 0; i < name.length; i++) {
            QMUICommonListItemView listItemView = qmui_gl.createItemView(name[i]);
            listItemView.setId(ids[i]);
            if (describe != null) {
                listItemView.setDetailText(describe[i]);
            }
            listItemView.setOrientation(orientation);
            listItemView.setAccessoryType(type);
            section.addItemView(listItemView, this);
        }
        section.addTo(qmui_gl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.one_1: {
                showListDialog(mPresenter.cats, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean select = which==1;//fale 为在线加载
                        mPresenter.settingCatType(which,select);
                        dialog.dismiss();
                    }
                });
                break;
            }
            case R.id.one_2: {
                showListDialog(mPresenter.maps, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean select = which==1;//fale 为小车
                        mPresenter.settingMapType(which,select);
                        dialog.dismiss();
                    }
                });
                break;
            }
            case R.id.two_1: {
                mPresenter.showUsers();
                break;
            }
            case R.id.two_2: {
                break;
            }
            case R.id.two_3: {
                break;
            }
            case R.id.two_4: {
                break;
            }
            case R.id.three_1: {
                toAcitvity(FeedbackActivity.class);
                break;
            }
            case R.id.three_2: {
                break;
            }
        }
    }

    @Override
    public void settingCatType(String name) {
        QMUICommonListItemView view = qmui_gl.findViewById(R.id.one_1);
        view.setDetailText(name);
    }

    @Override
    public void settingCatType_Fail(int type, String name) {
    }

    @Override
    public void settingMapType(String name,boolean value) {
        dimiss();
        QMUICommonListItemView view = qmui_gl.findViewById(R.id.one_2);
        view.setDetailText(name);
        Intent intent = new Intent(getResources().getString(R.string.action));
        intent.putExtra("MAPTYPE",value);
        sendBroadcast(intent);
    }

    @Override
    public void settingMapType_Fail(int type, String msg) {
        dimiss();
        ToastUtility.showToast(msg);
    }

    @Override
    public void showUsers(String[] values) {
        showListDialog(values, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sv = values[which];
                dialog.dismiss();
                showLoading();
                mPresenter.settingUser(sv);
            }
        });
    }

    @Override
    public void settingUser_Success(String msg) {
        dimiss();
        ToastUtility.showToast(msg);
    }

    @Override
    public void settingUser_Fail(String msg) {
        dimiss();
        ToastUtility.showToast(msg);
    }
}
