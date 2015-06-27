package com.sqbnet.expressassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class loginActivity extends Activity {

    private EditText et_pwd;
    private EditText et_usr;
    private Button btn_login;
    private TextView tv_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        et_pwd = (EditText) findViewById(R.id.editTextPwd);
        et_usr = (EditText) findViewById(R.id.editTextUsr);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_register = (TextView) findViewById(R.id.tv_register);

        et_pwd.setTypeface(Typeface.DEFAULT);
        et_pwd.setTransformationMethod(new PasswordTransformationMethod());

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                //TODO: add login api call here

                Intent intent = new Intent();

                // TODO: for debugging
                if (et_usr.getText().toString().equals("user")) {
                    //login success
                    //intent.putExtra()

                    setResult(ResultCode.LOGIN_SUCCESS);
                    finish();
                } else {
                    //login failed
                    intent.setClass(loginActivity.this, loginFailedActivity.class);

                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }

            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(loginActivity.this, registrationActivity.class);

                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.getCurrentFocus() != null) {
            if (et_pwd.hasFocus()) {
                et_pwd.clearFocus();
            }
            if (et_usr.hasFocus()) {
                et_usr.clearFocus();
            }

            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
