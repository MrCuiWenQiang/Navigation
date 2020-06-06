package com.zt.navigation.oldlyg.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.tts.client.SpeechError;
import com.zt.navigation.oldlyg.Urls;
import com.zt.navigation.oldlyg.model.bean.BroadBean;
import com.zt.navigation.oldlyg.tts.BDTTS;
import com.zt.navigation.oldlyg.tts.listener.BDListener;
import com.zt.navigation.oldlyg.tts.listener.MainHandlerConstant;
import com.zt.navigation.oldlyg.util.TokenManager;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.faker.repaymodel.net.okhttp3.HttpHelper;
import cn.faker.repaymodel.net.okhttp3.callback.HttpResponseCallback;
import cn.faker.repaymodel.util.LogUtil;
import cn.faker.repaymodel.util.error.ErrorUtil;

public class BroadcastService extends Service {

    public static final String KEY_BROAD = "KEY_BROAD";

    private String TAG = getClass().getName();

    BroadBean br = null;

    private static final int PERIOD = 30 * 1000;
    private static final int DELAY = 2000;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private boolean isquery = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        br = new BroadBean(intent.getStringExtra(KEY_BROAD));
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isquery && !BDTTS.isHave) {
                    isquery = true;
                    query();
                }
            }
        };
        mTimer.schedule(mTimerTask, DELAY, PERIOD);
    }


    private void query() {
        HashMap map = new HashMap<String, String>();
        map.put("type", br.getTYPE());
        HttpHelper.get(Urls.SELECT_TG, map, new HttpResponseCallback() {
            @Override
            public void onSuccess(String data) {

            }

            @Override
            public void onFailed(int status, String message, String data) {
                data = trimFirstAndLastChar(data, "\"");
                JSONObject json = JSON.parseObject(data);
                String content = json.getString("content");
                isquery = false;
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                content.replaceAll("_", ";");
                try {
                    if (!BDTTS.isHave) {
                        BDTTS bdtts = new BDTTS();
                        bdtts.init(getBaseContext(), new TTSHandler(bdtts, content), new BDListener() {
                            @Override
                            public void onSpeechFinish(String s) {
                                super.onSpeechFinish(s);
                                bdtts.releas();
                            }

                            @Override
                            public void onError(String s, SpeechError speechError) {
                                super.onError(s, speechError);
                                bdtts.releas();
                            }
                        });
                    }
                }catch (Exception e){
                    ErrorUtil.showError(e);
                }
            }

            @Override
            public void onFailed(int status, String message) {
                LogUtil.e(TAG, message);
                isquery = false;
            }

            @Override
            public void onMessage(String msg) {
                super.onMessage(msg);
                isquery = false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) mTimer.cancel();
    }

    protected class TTSHandler extends Handler {
        private BDTTS bdtts;
        private String content;

        public TTSHandler(BDTTS bdtts, String content) {
            this.bdtts = bdtts;
            this.content = content;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MainHandlerConstant.PRINT) {
                bdtts.speak(content);
            }
        }
    }

    public String trimFirstAndLastChar(String source, String element) {
        boolean beginIndexFlag = true;
        boolean endIndexFlag = true;
        int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
        int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
        source = source.substring(beginIndex, endIndex);
        beginIndexFlag = (source.indexOf(element) == 0);
        endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        return source;
    }
}
