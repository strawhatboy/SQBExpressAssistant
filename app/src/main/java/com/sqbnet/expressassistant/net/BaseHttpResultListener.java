package com.sqbnet.expressassistant.net;

import org.json.JSONObject;

/**
 * Created by virgil on 6/24/15.
 */
public interface BaseHttpResultListener {
    void onHttpChanged(JSONObject jsonObject);
}
