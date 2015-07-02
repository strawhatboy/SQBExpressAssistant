package com.sqbnet.expressassistant;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by virgil on 7/1/15.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getInst().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyApplication.getInst().finishActivity(this);
    }
}