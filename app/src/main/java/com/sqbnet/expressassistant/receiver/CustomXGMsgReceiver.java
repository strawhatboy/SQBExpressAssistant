package com.sqbnet.expressassistant.receiver;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * Created by virgil on 7/9/15.
 */
public class CustomXGMsgReceiver extends XGPushBaseReceiver {
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        Log.i("virgil", "----CXG register result----");
        Log.i("virgil", "account:"+ xgPushRegisterResult.getAccount());
        Log.i("virgil", "device id:" + xgPushRegisterResult.getDeviceId());
        Log.i("virgil", "token:" + xgPushRegisterResult.getToken());
    }

    @Override
    public void onUnregisterResult(Context context, int i) {
        Log.i("virgil", "----CXG unregister----");
    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.i("virgil", "----CXG text message ----");
        Log.i("virgil", "title:" + xgPushTextMessage.getTitle());
        Log.i("virgil", "content:" + xgPushTextMessage.getContent());
        Log.i("virgil", "custom content:" + xgPushTextMessage.getCustomContent());
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }
}
