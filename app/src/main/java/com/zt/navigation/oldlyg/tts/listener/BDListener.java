package com.zt.navigation.oldlyg.tts.listener;

import android.util.Log;

import com.baidu.tts.client.SpeechError;

public class BDListener  extends MessageListener {
    @Override
    public void onSynthesizeStart(String s) {
        Log.i("msg", "合成开始");
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        Log.i("msg", "合成进度 :"+i);
    }

    @Override
    public void onSynthesizeFinish(String s) {

        Log.i("msg", "合成结束");
    }

    @Override
    public void onSpeechStart(String s) {
        Log.i("msg", "开始播放");
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
        Log.i("msg", "播放进度 :"+i);
    }

    @Override
    public void onSpeechFinish(String s) {
        Log.i("msg", "播放结束");
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        Log.i("msg", "error :"+speechError);
    }

}
