package com.sqbnet.expressassistant.Provider;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.net.BaseHttpResultListener;
import com.sqbnet.expressassistant.net.BaseHttpThread;
import com.sqbnet.expressassistant.utils.UtilHelper;
import com.tencent.android.tpush.logging.TLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by virgil on 6/24/15.
 */
public class SQBProvider {

    private static volatile SQBProvider sInst;
    private static Map<String, SQBResponse> sCache = new HashMap<String, SQBResponse>();

    public static String BASE_URL = "http://wap.sqbnet.com/index.php/APP/DistributionAppAction/";
    public static String URL_APPREV = "phoneCaptcha";
    public static String URL_LOGIN = "userLogin";
    public static String URL_REGISTER = "userRegister";
    public static String URL_UPLOAD_PHOTO = "uploadPhoto";
    public static String URL_HISTORY_ORDERS = "getHistoryOrder";
    public static String URL_DISPATCHER_INFO = "getDispatchPerson";
    public static String URL_UPDATE_POSITION = "updatePosition";
    public static String URL_GET_ASSIGN_ORDER = "getAssignOrder";
    public static String URL_GET_ORDER_INFO = "getOrderInfo";
    public static String URL_UPDATE_STATUS = "updateStatus";
    public static String URL_VERIFY_CODE = "verifyCode";
    public static String URL_GIVEUP_ORDER = "giveUpOrder";
    public static String URL_UPDATE_USER_STATUS = "updateUserStatus";
    public static String URL_USER_LOGOUT = "userLogout";
    public static String URL_GET_AREA = "getArea";
    public static String URL_WRITE_INFO = "writeInfo";

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

    public void login(String username, String password, String latitude, String longitude, String token, final SQBResponseListener listener){
        try{
            String url = BASE_URL + URL_LOGIN;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("token", token);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("login", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void sendSMS(String mobile, final SQBResponseListener listener) {
        try {
            String url = BASE_URL + URL_APPREV;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", mobile);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("sendSMS", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void userRegister(String username, String password, String name, String card, String cardphoto, String phone, String address, String salt, String province, String city, String district, String sex,  final SQBResponseListener listener){
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
            jsonObject.put("province", province);
            jsonObject.put("city", city);
            jsonObject.put("district", district);
            jsonObject.put("sex", sex);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("userRegister", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }

    }

    public void uploadPhoto(String photoPath, final SQBResponseListener listener) {
        try {
            String imageData = UtilHelper.ImagePathToB64(photoPath);
            String url = BASE_URL + URL_UPLOAD_PHOTO;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cardphoto", imageData);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("uploadPhoto", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void getHistoryOrder(String userId, final SQBResponseListener listener, boolean isRefreh) {
        try{
            String url = BASE_URL + URL_HISTORY_ORDERS;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("d_id", userId);

            doPost(url, jsonObject, listener, isRefreh);
        }catch (Exception e){
            UtilHelper.LogException("getHistoryOrder", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void getDispatchPerson(String userId, final SQBResponseListener listener) {
        try {
            String url = BASE_URL + URL_DISPATCHER_INFO;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("d_id", userId);

            doPost(url, jsonObject, listener, true);
        } catch (Exception e) {
            UtilHelper.LogException("getDispatchPerson", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void updatePosition(String userId, String latitude, String longitude, final SQBResponseListener listener){
        try{
            String url = BASE_URL + URL_UPDATE_POSITION;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("d_id", userId);
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("updatePostition", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void getAssignOrder(String userId, final  SQBResponseListener listener){
        try{
            String url = BASE_URL + URL_GET_ASSIGN_ORDER;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("d_id", userId);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("getAssignOrder", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void getOrderInfo(String order_id, final SQBResponseListener listener){
        try{
            String url = BASE_URL + URL_GET_ORDER_INFO;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("order_id", order_id);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("getOrderInfo", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void updateOrderStatus(String order_id, String status, final SQBResponseListener listener){
        try{
            String url = BASE_URL + URL_UPDATE_STATUS;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("order_id", order_id);
            jsonObject.put("status", status);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("updateOrderStatus", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void verifyCode(String order_id, String code, final SQBResponseListener listener){
        try{
            String url = BASE_URL + URL_VERIFY_CODE;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("order_id", order_id);
            jsonObject.put("code", code);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("verifyCode", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void updateUserStatus(String user_id, String status, String latitude, String longitude, final SQBResponseListener listener){
        try{
            String url =BASE_URL + URL_UPDATE_USER_STATUS;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("d_id", user_id);
            jsonObject.put("status", status);
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("updateUserStatus", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void logout(String user_id, final SQBResponseListener listener){
        try{
            String url = BASE_URL + URL_USER_LOGOUT;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("d_id", user_id);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("logout", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void  giveupOrder(String user_id, String order_id, final SQBResponseListener listener){
        try{
            String url = BASE_URL + URL_GIVEUP_ORDER;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("d_id", user_id);
            jsonObject.put("order_id", order_id);

            doPost(url, jsonObject, listener, true);
        }catch (Exception e){
            UtilHelper.LogException("giveupOrder", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void getArea(String parent_id, final SQBResponseListener listener) {
        try {
            String url = BASE_URL + URL_GET_AREA;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("parent_id", parent_id);

            doPost(url, jsonObject, listener, true);
        } catch (Exception e) {
            UtilHelper.LogException("getArea", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    public void updateCrashReport(String info, final SQBResponseListener listener){
        try {
            String url = BASE_URL + URL_WRITE_INFO;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("info", info);

            doPost(url, jsonObject, listener, true);
        } catch (Exception e) {
            UtilHelper.LogException("updateCrashReport", e);
            if(listener != null){
                listener.onResponse(null);
            }
            e.printStackTrace();
        }
    }

    private boolean refreshMap(String url){
        if(url.equals(BASE_URL + URL_HISTORY_ORDERS)){
            return true;
        }else {
            return false;
        }
    }

    private void doPost(final String url, JSONObject jsonObject, final SQBResponseListener listener, final boolean isRefresh){
        try{
            TLog.i("virgil", "doPost");
            if(!isRefresh){
                if(sCache.containsKey(url)){
                    if(listener!=null){
                        TLog.i("virgil", "get data from cache");
                        listener.onResponse(sCache.get(url));
                        return;
                    }
                }
            }
            TLog.i("virgil", "goto http request");
            BaseHttpThread httpThread = new BaseHttpThread(url, jsonObject, new BaseHttpResultListener() {
                @Override
                public void onHttpChanged(JSONObject jsonObject){
                    if(listener != null) {
                        try {
                            SQBResponse response = new SQBResponse(jsonObject.getString("code"), jsonObject.getString("msg"), jsonObject.get("data"));
                            if(jsonObject.getString("code").equals("1000") && refreshMap(url)){
                                sCache.put(url, response);
                            }
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
            UtilHelper.LogException("doPost", e);
            e.printStackTrace();
            if(listener != null){
                listener.onResponse(null);
            }
        }
    }
}

