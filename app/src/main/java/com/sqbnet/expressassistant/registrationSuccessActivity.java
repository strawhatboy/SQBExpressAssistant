package com.sqbnet.expressassistant;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                MyApplication.getInst().AppExit();
            }
        });
    }
}

