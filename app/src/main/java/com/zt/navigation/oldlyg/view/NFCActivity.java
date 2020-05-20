package com.zt.navigation.oldlyg.view;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

import com.zt.navigation.oldlyg.R;

import cn.faker.repaymodel.activity.BaseToolBarActivity;

public class NFCActivity extends BaseToolBarActivity {

    private NfcAdapter mNfcAdapter;
    private PendingIntent pi;
    public static final String NFC_SCAN_RESULT = "NFC_SCAN_RESULT";
    public static final int CAPTURE_SCAN_CODE = 0X42;

    @Override
    protected int getLayoutContentId() {
        return R.layout.ac_nfc;
    }

    @Override
    protected void initContentView() {
        isShowBackButton(false);
        isShowToolView(false);
        changStatusIconCollor(false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter!=null){
            mNfcAdapter.enableForegroundDispatch(this, pi, null, null); //启动
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter != null){
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 当前app正在前端界面运行，这个时候有intent发送过来，那么系统就会调用onNewIntent回调方法，将intent传送过来
        // 我们只需要在这里检验这个intent是否是NFC相关的intent，如果是，就调用处理方法
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            processIntent(intent);
        }
    }


    private void processIntent(Intent intent) {
        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String cardId = ByteArrayToHexString(tagFromIntent.getId());
        Intent intentdata = new Intent();
        intentdata.putExtra(NFC_SCAN_RESULT,cardId);
        setResult(CAPTURE_SCAN_CODE,intentdata);
        finish();
    }

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F"};
        String out = "";


        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNfcAdapter=null;
    }
}
