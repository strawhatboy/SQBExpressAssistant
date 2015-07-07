package com.sqbnet.expressassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sqbnet.expressassistant.Location.GPSLocation;
import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.UtilHelper;

import org.json.JSONException;
import org.json.JSONObject;

import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;


public class loginActivity extends BaseActivity {

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

    @Override
    protected void onDestroy() {
        Log.i("virgil", "onDestory Login Activity");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startGPSLocation();
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

                String username = null;
                String password = null;

                if (et_usr.length() > 0) {
                    username = et_usr.getText().toString();
                }
                if (et_pwd.length() > 0) {
                    password = et_pwd.getText().toString();
                }

                if (username == null || password == null) {
                    UtilHelper.showToast("用户名密码不能为空");
                    return;
                }

                password = UtilHelper.MD5(password);

                Location location = GPSLocation.getInst().getCurrentLocation();

                final ProgressDialog progressDialog = UtilHelper.getProgressDialog("登录中...", loginActivity.this);
                progressDialog.show();

                SQBProvider.getInst().login(username, password, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new SQBResponseListener() {
                    @Override
                    public void onResponse(final SQBResponse response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                if (response != null) {
                                    Log.i("virgil", response.getCode());
                                    Log.i("virgil", response.getMsg());
                                    Log.i("virgil", response.getData().toString());
                                    if (response.getCode().equals("1000")) {
                                        try {
                                            String userId = ((JSONObject) response.getData()).getString("id");
                                            Log.i("virgil", userId);
                                            UtilHelper.setSharedUserId(userId);
                                            setResult(ResultCode.LOGIN_SUCCESS);
                                            finish();
                                        } catch (JSONException e) {
                                            loginFall();
                                        }
                                    } else {
                                        loginFall();
                                    }
                                } else {
                                    loginFall();
                                }
                            }
                        });
                    }
                });
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

    private void startGPSLocation(){
        GPSLocation.getInst().GPSProviderStatusChanged = new GPSLocation.GPSProviderStatusChanged(){
            @Override
            public void onStatusChanged(boolean isEnabled) {
                if(!isEnabled){
                    startGPSLocation();
                }
            }
        };
        if(!GPSLocation.getInst().openGEPSettings()){
            new AlertDialog.Builder(loginActivity.this).setTitle("提示")
                    .setMessage("GPS定位没有开启，请手动开启！")
                    .setPositiveButton("已开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startGPSLocation();
                                }
                            });
                        }
                    })
                    .show();
            return;
        }

        GPSLocation.getInst().start();
        //BaiDuLocationService.getInst().getLocationClient().start();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(loginActivity.this).setTitle("提示")
                .setMessage("确认退出？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(ResultCode.QUIT);
                        MyApplication.getInst().AppExit();
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }

    private void loginFall(){
        //login failed
        Intent intent = new Intent();
        intent.setClass(loginActivity.this, loginFailedActivity.class);

        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
