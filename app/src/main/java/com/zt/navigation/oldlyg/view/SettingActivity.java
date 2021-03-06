package com.zt.navigation.oldlyg.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.zt.navigation.oldlyg.MyApplication;
import com.zt.navigation.oldlyg.R;

import com.zt.navigation.oldlyg.contract.SettingContract;
import com.zt.navigation.oldlyg.presenter.SettingPresenter;
import com.zt.navigation.oldlyg.util.AppSettingUtil;
import com.zt.navigation.oldlyg.util.TpkTDTUtil;

import cn.faker.repaymodel.mvp.BaseMVPAcivity;
import cn.faker.repaymodel.util.ToastUtility;

/**
 * 设置页面
 */
public class SettingActivity extends BaseMVPAcivity<SettingContract.View, SettingPresenter> implements SettingContract.View, View.OnClickListener {

    private String[] item_one_name = new String[]{"路线模式", "地图模式"};
    private int[] one_ids = new int[]{R.id.one_1, R.id.one_2};
    private String[] item_two_name = new String[]{"到达上报","车辆设置", "网络设置",  "版本更新"};
    private int[] two_ids = new int[]{R.id.ddupdate,R.id.two_1, R.id.two_2, R.id.two_4};
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
        int id = v.getId();
        if (id == R.id.one_1) {
            showListDialog(mPresenter.cats, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean select = which == 1;//fale 为在线加载
                    mPresenter.settingCatType(which, select);
                    dialog.dismiss();
                }
            });
        } else if (id == R.id.one_2) {
            showListDialog(mPresenter.maps, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    boolean select = which == 1;
                    if (select&&!TpkTDTUtil.isHaveMap()){
                        showDialog("您手机还未有天地图离线包,是否下载?", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                                showLoading("正在下载,请稍等...");
                                mPresenter.downloadMap();
                            }
                        });
                    }else {
                        mPresenter.settingMapType(which, select);
                    }
                }
            });
        } else if (id == R.id.two_1) {
            mPresenter.showUsers();
        } else if (id == R.id.two_2) {
            toAcitvity(AddressSettingActivity.class);
        }  else if (id == R.id.two_4) {
            toAcitvity(VersionActivity.class);
        } else if (id == R.id.three_1) {
            toAcitvity(FeedbackActivity.class);
        } else if (id == R.id.three_2) {
        }else if (id == R.id.ddupdate) {
            if (MyApplication.startPoint!=null){
                showLoading();
                mPresenter.updateLocation(MyApplication.startPoint.getX(),MyApplication.startPoint.getY());
            }else {
                showDialog("未开启定位");
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
    public void settingMapType(String name, boolean value) {
        dimiss();
        QMUICommonListItemView view = qmui_gl.findViewById(R.id.one_2);
        view.setDetailText(name);
        Intent intent = new Intent(getResources().getString(R.string.action));
        intent.putExtra("MAPTYPE", value);
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

    @Override
    public void uploadArriveSuccess(String msg) {
        dimiss();
        ToastUtility.showToast(msg);
    }

    @Override
    public void uploadArriveFail(String msg) {
        dimiss();
        ToastUtility.showToast(msg);
    }

    @Override
    public void showArrivefinal(String msg) {
        dimiss();
        ToastUtility.showToast(msg);
    }

    @Override
    public void download_Success(String message) {
        dimiss();
        ToastUtility.showToast(message);
    }

    @Override
    public void download_Fail(String message) {
        dimiss();
        showDialog(message);
    }
}
