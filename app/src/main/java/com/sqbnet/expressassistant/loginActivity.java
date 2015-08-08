package com.sqbnet.expressassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sqbnet.expressassistant.Location.BaiDuLocationService;
import com.sqbnet.expressassistant.Location.GPSLocation;
import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.MyLocation;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.UtilHelper;

import org.json.JSONException;
import org.json.JSONObject;

import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.tencent.android.tpush.XGPushConfig;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class loginActivity extends BaseActivity {

    private EditText et_pwd;
    private EditText et_usr;
    private Button btn_login;
    private TextView tv_register;
    private CheckBox chkbox_remember;
    private SharedPreferences sharedPreferences;

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

        UtilHelper.checkGPSLocation(loginActivity.this);
    }

    private void initView() {
        et_pwd = (EditText) findViewById(R.id.editTextPwd);
        et_usr = (EditText) findViewById(R.id.editTextUsr);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_register = (TextView) findViewById(R.id.tv_register);
        chkbox_remember = (CheckBox) findViewById(R.id.chkbox_login_remember_username);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isRemember", false)) {
            et_usr.setText(sharedPreferences.getString("username", ""));
            et_pwd.setText(sharedPreferences.getString("password", ""));
            chkbox_remember.setChecked(true);
        }

        et_pwd.setTypeface(Typeface.DEFAULT);
        et_pwd.setTransformationMethod(new PasswordTransformationMethod());

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                final ProgressDialog locationDialog = UtilHelper.getProgressDialog("定位中，可能需要几分钟，请稍候...", loginActivity.this);
                if (!mIsDestroyed) {
                    locationDialog.show();
                }

                AsyncTask<String, String, MyLocation> task = new AsyncTask<String, String, MyLocation>() {
                    @Override
                    protected MyLocation doInBackground(String... strings) {
                        MyLocation location = GPSLocation.getInst().getCurrentLocation();
                        int count = 0;
                        while (location == null) {
                            if (count >= 10) {
                                return null;
                            }
                            try {
                                Thread.sleep(2000);
                                location = GPSLocation.getInst().getCurrentLocation();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            count += 1;
                        }
                        return location;
                    }

                    @Override
                    protected void onPostExecute(MyLocation location) {
                        super.onPostExecute(location);

                        if (locationDialog.isShowing()) {
                            locationDialog.dismiss();
                        }
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

                        String latitude = "0";
                        String longitude = "0";

                        if (location != null) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        } else {
                            UtilHelper.showToast("定位不成功，请登录后再试");
                        }
                        final ProgressDialog progressDialog = UtilHelper.getProgressDialog("登录中...", loginActivity.this);
                        if (!mIsDestroyed) {
                            progressDialog.show();
                        }

                        String token = XGPushConfig.getToken(getApplicationContext());
                        Log.i("virgil", "XG token:" + token);

                        SQBProvider.getInst().login(username, password, latitude, longitude, token, new SQBResponseListener() {
                            @Override
                            public void onResponse(final SQBResponse response) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
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
                                                    if (chkbox_remember.isChecked()) {
                                                        sharedPreferences.edit()
                                                                .putBoolean("isRemember", true)
                                                                .apply();
                                                        sharedPreferences.edit()
                                                                .putString("username", et_usr.getText().toString())
                                                                .apply();
                                                        sharedPreferences.edit()
                                                                .putString("password", et_pwd.getText().toString())
                                                                .apply();
                                                    }

                                                    finish();
                                                } catch (JSONException e) {
                                                    loginFall();
                                                }
                                            } else if (response.getCode().equals("1003")) {
                                                UtilHelper.showToast(response.getMsg());
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
                };
                task.execute();
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

        /*chkbox_remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPreferences.edit()
                        .putBoolean("isRemember", b)
                        .apply();
            }
        });*/

        UtilHelper.checkNewVersion(new SQBResponseListener() {
            @Override
            public void onResponse(SQBResponse response) {
                String result = (String) response.getData();
                if (result == null) {
                    return;
                }

                List<String> versionMatches = UtilHelper.getMatchedText(result, "<version>(.*)</version>");
                String version = versionMatches.size() > 0 ? versionMatches.get(0) : "";
                String localVersion = UtilHelper.getApplicationVersion();
                Log.i("loginActivity", "comparing version: remote: " + version + ", local: " + localVersion);
                if (version.length() > 0 && version.compareTo(localVersion) > 0) {
                    Log.i("loginActivity", "Got new version!!!");

                    List<String> urlMatches = UtilHelper.getMatchedText(result, "<url>(.*)</url>");
                    final String url = urlMatches.size() > 0 ? urlMatches.get(0) : "";
                    if (url.length() > 0 && url.startsWith("http:")) {
                        List<String> infoMatches = UtilHelper.getMatchedText(result, "<description>(.*)</description>");
                        final String info = infoMatches.size() > 0 ? infoMatches.get(0) : "";

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(loginActivity.this).setTitle("提示")
                                        .setMessage(info)
                                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Uri uri = Uri.parse(url);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("否", null)
                                        .show();
                            }
                        });
                    }
                }

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
        new AlertDialog.Builder(loginActivity.this).setTitle("提示")
                .setMessage("确认退出？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setResult(ResultCode.QUIT);
                        finish();
                        //MyApplication.getInst().AppExit();
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
