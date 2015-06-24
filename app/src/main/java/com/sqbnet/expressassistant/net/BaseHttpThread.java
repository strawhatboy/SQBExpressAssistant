package com.sqbnet.expressassistant.net;

import org.json.JSONObject;

/**
 * Created by virgil on 6/24/15.
 */
public class BaseHttpThread extends Thread{
    private String mUrl;
    private JSONObject mJsonObject;
    private BaseHttpResultListener mListener;

    public BaseHttpThread(String url, JSONObject jsonObject, BaseHttpResultListener listener){
        mUrl = url;
        mJsonObject = jsonObject;
        mListener = listener;
    }

    public void run(){
        JSONObject res_ojbect = NetEnginee.getInst().HttpPost(mUrl, mJsonObject);
        if(mListener != null){
            mListener.onHttpChanged(res_ojbect);
        }
    }
}
