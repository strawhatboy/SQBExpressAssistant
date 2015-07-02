package com.sqbnet.expressassistant;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by virgil on 7/1/15.
 */
public class BaseFragmentActivity extends FragmentActivity {
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
