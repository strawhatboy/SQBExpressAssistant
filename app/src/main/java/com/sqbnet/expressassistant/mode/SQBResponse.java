package com.sqbnet.expressassistant.mode;

/**
 * Created by virgil on 6/24/15.
 */
public class SQBResponse {
    private String mCode;
    private String mMsg;
    private Object mData;

    public SQBResponse(String code, String msg, Object data){
        mCode = code;
        mMsg = msg;
        mData = data;
    }

    public String getCode(){
        return mCode;
    }

    public String getMsg(){
        return mMsg;
    }

    public Object getData(){
        return mData;
    }
}
