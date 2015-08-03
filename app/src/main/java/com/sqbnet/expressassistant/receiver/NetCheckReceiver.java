package com.sqbnet.expressassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.sqbnet.expressassistant.service.LocalService;
import com.tencent.android.tpush.XGPushManager;

/**
 * Created by virgil on 8/3/15.
 */
public class NetCheckReceiver extends BroadcastReceiver{

    private static final String netAction = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(netAction)){
            if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)){
                Log.i("virgil", "!---- network not connected");
            }else {
                Log.i("virgil", "!---- network connected");
                XGPushManager.registerPush(context);
            }
        }
    }
}
