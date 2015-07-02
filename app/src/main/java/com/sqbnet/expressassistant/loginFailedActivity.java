package com.sqbnet.expressassistant;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class loginFailedActivity extends BaseActivity {

    private Button btn_re_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_failed);

        initView();
    }

    private void initView() {
        btn_re_login = (Button) findViewById(R.id.btn_login_failed_re_login);

        btn_re_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
