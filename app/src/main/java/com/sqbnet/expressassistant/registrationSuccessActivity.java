package com.sqbnet.expressassistant;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sqbnet.expressassistant.Location.GPSLocation;
import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.UtilHelper;
import com.tencent.android.tpush.XGPushManager;

/**
 * Created by virgil on 7/1/15.
 */
public class registrationSuccessActivity extends BaseActivity {

    private Button quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success);

        initView();
    }

    private void initView() {
        quitButton = (Button) findViewById(R.id.btn_registration_success_quit);

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XGPushManager.unregisterPush(getApplicationContext());
                finish();
                MyApplication.getInst().AppExit();
            }
        });
    }
}

