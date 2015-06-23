package com.sqbnet.expressassistant.net;

import android.util.Log;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by virgil on 6/23/15.
 */
public class NetEnginee {

    private static volatile NetEnginee sInst;

    public static NetEnginee getInst(){
        if(sInst == null){
            synchronized (NetEnginee.class){
                if(sInst == null){
                    sInst = new NetEnginee();
                }
            }
        }
        return sInst;
    }

    public JSONObject HttpPost(String url, JSONObject jsonObject){
        HttpClient httpClient = new DefaultHttpClient();
        try{
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(new BasicNameValuePair("jsonString", jsonObject.toString()));
            Log.d("virgil: post url", url);
            Log.d("virgil: post body", jsonObject.toString());
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, "utf-8"));

            HttpResponse response = httpClient.execute(httpPost);
            int res_code = response.getStatusLine().getStatusCode();
            Log.d("virgil: response code", String.valueOf(res_code));
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            Log.d("virgil: response data", result);
            JSONObject res_object = new JSONObject(result);
            return res_object;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
