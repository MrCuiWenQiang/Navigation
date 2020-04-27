package com.zt.navigation.oldlyg.tts;

import android.content.Context;
import android.os.Handler;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.zt.navigation.oldlyg.tts.config.InitConfig;
import com.zt.navigation.oldlyg.tts.config.MySyntherizer;
import com.zt.navigation.oldlyg.tts.config.NonBlockSyntherizer;
import com.zt.navigation.oldlyg.tts.listener.BDListener;

import java.util.HashMap;
import java.util.Map;

public class BDTTS {

    protected String appId = "17763821";
    protected String appKey = "sZn7Oyh62aBKobueQaX51MfD";
    protected String secretKey = "EU0dUe61oEz2d2VKHPxyWkbdc5o2RSvz";
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.ONLINE;
    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;

    public static boolean isHave = false;//判断是否被其他activity初始化

    public void init(Context context, Handler mainHandler) {
        init(context, mainHandler,null);
    }


    public void init(Context context, Handler mainHandler, SpeechSynthesizerListener listener) {
        isHave = true;
        LoggerProxy.printable(true); // 日志打印在logcat中
        if (listener == null) {
            listener = new BDListener();
        }
//        Map<String, String> params = getParams();
        Map<String, String> params = new HashMap<>();
        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
        synthesizer = new NonBlockSyntherizer(context, initConfig, mainHandler); // 此处可以改为MySyntherizer 了解调用过程
    }


    public void releas() {
        isHave = false;
        if (synthesizer != null) {
            synthesizer.release();
        }
    }

    //如果不为0则播放失败
    public int speak(String txt) {
        int code = 0;
        if (synthesizer != null) {
            code = synthesizer.speak(txt);
        }
        return code;
    }

    /*
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    public int stop() {
        int code = 0;
        if (synthesizer != null) {
            code = synthesizer.stop();
        }
        return code;
    }

}
