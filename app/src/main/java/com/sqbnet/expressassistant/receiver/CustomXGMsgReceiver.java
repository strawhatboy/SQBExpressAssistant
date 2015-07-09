package com.sqbnet.expressassistant.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;

import com.sqbnet.expressassistant.MyApplication;
import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.R;
import com.sqbnet.expressassistant.RequestCode;
import com.sqbnet.expressassistant.orderMainActivity;
import com.sqbnet.expressassistant.utils.CustomConstants;
import com.sqbnet.expressassistant.utils.UtilHelper;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import  com.sqbnet.expressassistant.mainActivity;

import org.json.JSONObject;

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
        MediaPlayer mediaPlayer = MediaPlayer.create(MyApplication.getInst().getApplicationContext(), R.raw.notification);
        mediaPlayer.start();

        String title = xgPushTextMessage.getTitle();
        String content = xgPushTextMessage.getContent();
        if(title.equals(SQBProvider.URL_GET_ASSIGN_ORDER)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONObject dataObj = jsonObject.getJSONObject("data");
                final String user_id = UtilHelper.getSharedUserId();
                final String order_id = dataObj.getString("order_id");
                final String status = dataObj.getString("d_status");

                Intent orderIntent = new Intent();
                orderIntent.setClass(MyApplication.getInst().getApplicationContext(), orderMainActivity.class);
                orderIntent.putExtra("user_id", user_id);
                orderIntent.putExtra("order_id", order_id);
                orderIntent.putExtra("status", status);

                int intentId = UtilHelper.getIntentId();
                PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getInst().getApplicationContext(), intentId,
                        orderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new Notification.Builder(MyApplication.getInst().getApplicationContext())
                        .setSmallIcon(R.drawable.logo_icon)
                        .setTicker("您有新的配送单，请尽快确认")
                        .setContentTitle("您有新的配送单号" + order_id)
                        .setContentText("请在5分钟内确认")
                        .setContentIntent(pendingIntent)
                        .getNotification();
                notification.flags |= Notification.FLAG_AUTO_CANCEL|Notification.DEFAULT_SOUND;
                NotificationManager notificationManager = (NotificationManager) MyApplication.getInst().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }
}
