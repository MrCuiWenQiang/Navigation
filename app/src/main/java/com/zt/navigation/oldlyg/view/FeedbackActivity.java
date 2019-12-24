package com.zt.navigation.oldlyg.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.zt.navigation.oldlyg.R;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.util.TokenManager;

import java.util.HashMap;

import cn.faker.repaymodel.activity.BaseToolBarActivity;
import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;
import cn.faker.repaymodel.util.ToastUtility;

/**
 * 意见反馈
 */
public class FeedbackActivity extends BaseToolBarActivity {

    private EditText et_content;
    private Button bt_sumbit;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_feeback;
    }

    @Override
    protected void initContentView() {
        et_content = findViewById(R.id.et_content);
        bt_sumbit = findViewById(R.id.bt_sumbit);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        changStatusIconCollor(false);
        setBackBackground(R.mipmap.fanhui_black);
        setTitle("意见反馈");
    }

    @Override
    protected void initListener() {
        super.initListener();
        bt_sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumbit();
            }
        });
    }

    private void sumbit() {
        String content = getValue(et_content);
        if (TextUtils.isEmpty(content)) {
            ToastUtility.showToast("请填写意见内容");
            return;
        }
        showLoading();
        HashMap map = new HashMap<String, String>();
//        map.put("userId", userId);
        map.put("info", content);
        map.put("token", TokenManager.token);
        map.put("userId", TokenManager.userId);
        HttpHelper.get(Urls.FEEDBACK, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {
                dimiss();
                showDialog("感谢您的反馈，我们虚心接受您的意见.", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onFailed(int status, String message) {
                dimiss();
                ToastUtility.showToast(message);
            }
        });

    }
}
