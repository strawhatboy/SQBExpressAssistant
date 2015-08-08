package com.sqbnet.expressassistant;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by virgil on 7/1/15.
 */
public class BaseActivity extends Activity {

    protected boolean mIsDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getInst().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        mIsDestroyed = true;
        MyApplication.getInst().finishActivity(this);
        super.onDestroy();
    }
}
