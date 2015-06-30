package com.sqbnet.expressassistant.Provider;

import android.util.Log;

import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.net.BaseHttpResultListener;
import com.sqbnet.expressassistant.net.BaseHttpThread;
import com.sqbnet.expressassistant.utils.UtilHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by virgil on 6/24/15.
 */
public class SQBProvider {

    private static volatile SQBProvider sInst;

    private static String BASE_URL = "http://wap.sqbnet.com/index.php/APP/DistributionAppAction/";
    private static String URL_APPREV = "phoneCaptcha";
    private static String URL_LOGIN = "userLogin";
    private static String URL_REGISTER = "userRegister";
    private static String URL_UPLOAD_PHOTO = "uploadPhoto";
    private static String URL_HISTORY_ORDERS = "getHistoryOrder";
    private static String URL_DISPATCHER_INFO = "getDispatchPerson";

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

    public void login(String username, String password, final SQBResponseListener listener){
        try{
            String url = BASE_URL + URL_LOGIN;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);

            doPost(url, jsonObject, listener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendSMS(String mobile, final SQBResponseListener listener) {
        try {
            String url = BASE_URL + URL_APPREV;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", mobile);

            doPost(url, jsonObject, listener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void userRegister(String username, String password, String name, String card, String cardphoto, String phone, String address, String salt, final SQBResponseListener listener){
        try{
            String url = BASE_URL + URL_REGISTER;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("name", name);
            jsonObject.put("card", card);
            jsonObject.put("cardphoto", cardphoto);
            jsonObject.put("phone", phone);
            jsonObject.put("address", address);
            jsonObject.put("salt", salt);

            doPost(url, jsonObject, listener);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void uploadPhoto(String photoPath, final SQBResponseListener listener) {
        try {
            String imageData = UtilHelper.ImagePathToB64(photoPath);
            String url = BASE_URL + URL_UPLOAD_PHOTO;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cardphoto", imageData);

            doPost(url, jsonObject, listener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getHistoryOrder(String userId, final SQBResponseListener listener) {
        try{
            String url = BASE_URL + URL_HISTORY_ORDERS;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("d_id", userId);

            doPost(url, jsonObject, listener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getDispatchPerson(String userId, final SQBResponseListener listener) {
        try {
            String url = BASE_URL + URL_DISPATCHER_INFO;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("d_id", userId);

            doPost(url, jsonObject, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPost(String url, JSONObject jsonObject, final SQBResponseListener listener){
        try{
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

