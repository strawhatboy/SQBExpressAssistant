package com.sqbnet.expressassistant.Provider;

import android.util.Log;

import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.net.BaseHttpResultListener;
import com.sqbnet.expressassistant.net.BaseHttpThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by virgil on 6/24/15.
 */
public class SQBProvider {

    private static volatile SQBProvider sInst;

    private static String BASE_URL = "http://wap.sqbnet.com/index.php/APP/DistributionAppAction/";
    private static String URL_APPREV = "phoneCaptcha";
    private static String URL_LOGIN = "userLogin";

    public static SQBProvider getInst(){
        if(sInst == null){
            synchronized (SQBProvider.class){
                if(sInst == null){
                    sInst = new SQBProvider();
                }
            }
        }
        return sInst;
    }

    public void SendSMS(String mobile, final SQBResponseListener listener) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobile", mobile);
        callRest(URL_APPREV, map, listener);
    }

    public void login(String username, String password, final SQBResponseListener listener) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        map.put("password", password);
        callRest(URL_LOGIN, map, listener);
    }

    public void uploadPhoto() {

    }

    private void callRest(String apiName, Map<String, Object> parameters, final SQBResponseListener listener) {
        try {
            String url = BASE_URL + apiName;
            JSONObject jsonObject = new JSONObject();

            for (Map.Entry<String, Object> keyValuePair : parameters.entrySet()) {
                jsonObject.put(keyValuePair.getKey(), keyValuePair.getValue());
            }

            BaseHttpThread httpThread = new BaseHttpThread(url, jsonObject, new BaseHttpResultListener() {
                @Override
                public void onHttpChanged(JSONObject jsonObject){
                    if(listener != null) {
                        try {
                            SQBResponse response = new SQBResponse(jsonObject.getString("code"), jsonObject.getString("msg"), jsonObject.get("data"));
                            listener.onResponse(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i("SQBProvider", e.toString());
                            listener.onResponse(null);
                        }
                    }
                }
            });

            httpThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

