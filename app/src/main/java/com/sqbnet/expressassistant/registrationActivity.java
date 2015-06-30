package com.sqbnet.expressassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.UtilHelper;

import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ResourceBundle;
import java.util.jar.JarException;


public class registrationActivity extends Activity {

    private Button btn_ok;
    private Button btn_cancel;
    private Button btn_pick_photo;
    private Button btn_get_passcode;

    private TextView tv_agreement;
    private TextView tv_photo_placeholder;
    private CheckBox chkbox_accept_protocol;

    private EditText et_username;
    private EditText et_password;
    private EditText et_real_name;
    private EditText et_id;
    private int et_photo_id;
    private EditText et_mobile;
    private EditText et_passcode;
    private EditText et_addr;

    private Bitmap photo;

    private TimeCount timeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        timeCount = new TimeCount(60000, 1000);
        initView();
    }

    private void initView() {
        initView_Buttons();
        initView_EditText();
        initView_Others();
    }

    private void initView_Buttons() {
        btn_ok = (Button) findViewById(R.id.btn_registration_ok);
        btn_cancel = (Button) findViewById(R.id.btn_registration_cancel);
        btn_pick_photo = (Button) findViewById(R.id.btn_registration_pick_photo);
        btn_get_passcode = (Button) findViewById(R.id.btn_registration_get_passcode);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent();
                intent.setClass(registrationActivity.this, loginActivity.class);

                startActivity(intent);
                overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);*/
                finish();
            }
        });

        btn_pick_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, getResources().getString(R.string.registration_pick_photo));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, RequestCode.PICK_PHOTO);
            }
        });

        btn_get_passcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_mobile.getText() == null || et_mobile.length() == 0){
                    return;
                }
                String mobile = et_mobile.getText().toString();
                Log.i("virgil", mobile);
                if(!UtilHelper.isMobileNO(mobile)){
                    Log.i("virgil", "not valid mobile");
                    Toast.makeText(getApplicationContext(), "手机号码格式不正确，请重新填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                SQBProvider.getInst().sendSMS(mobile, new SQBResponseListener() {
                    @Override
                    public void onResponse(final SQBResponse response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run(){
                                if (response != null) {
                                    Log.i("virgil", response.getCode());
                                    Log.i("virgil", response.getMsg());
                                    Log.i("virgil", response.getData().toString());
                                    Toast.makeText(getApplicationContext(), response.getMsg(), Toast.LENGTH_SHORT).show();
                                    try {
                                        String phone_code = ((JSONObject) response.getData()).getString("phone_code");
                                        Log.i("virgil", phone_code);
                                    }catch (JSONException e){
                                        Toast.makeText(getApplicationContext(), "发送验证码失败，请稍后再试", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "发送验证码失败，请稍后再试", Toast.LENGTH_SHORT).show();
                                    timeCount.onFinish();
                                }
                            }
                        });
                    }
                });
                timeCount.start();
            }
        });
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millsInFuture, long countDownInterval){
            super(millsInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btn_get_passcode.setText("重发验证码");
            btn_get_passcode.setClickable(true);
        }

        @Override
        public void onTick(long l) {
            btn_get_passcode.setClickable(false);
            btn_get_passcode.setText(l/1000 + "秒");
        }
    }

    private void initView_EditText() {
        et_username = (EditText) findViewById(R.id.et_registration_username);
        et_password = (EditText) findViewById(R.id.et_registration_password);
        et_real_name = (EditText) findViewById(R.id.et_registration_real_name);
        et_id = (EditText) findViewById(R.id.et_registration_id);
        et_mobile = (EditText) findViewById(R.id.et_registration_mobile);
        et_passcode = (EditText) findViewById(R.id.et_registration_passcode);
        et_addr = (EditText) findViewById(R.id.et_registration_full_addr);
    }

    private void initView_Others() {
        tv_photo_placeholder = (TextView) findViewById(R.id.tv_photo_placeholder);
        tv_agreement = (TextView) findViewById(R.id.tv_registration_agreement);
        chkbox_accept_protocol = (CheckBox) findViewById(R.id.chkbox_registration_accept_protocol);
        chkbox_accept_protocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    btn_ok.setEnabled(b);
            }
        });

        tv_agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(registrationActivity.this, registrationAgreementActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == RequestCode.PICK_PHOTO) {
            try {
                Uri originalUri = data.getData();
                photo = MediaStore.Images.Media.getBitmap(getContentResolver(), originalUri);
                String fileName = getRealPathFromURI(originalUri);
                int start = fileName.lastIndexOf("/");
                if (start != -1) {
                    fileName = fileName.substring(start + 1);
                }
                tv_photo_placeholder.setText(fileName);

                // upload the photo


            } catch (Exception e) {
                Log.e("registration", e.toString());
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(registrationActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private boolean validateEditText() {
        //TODO: validate all the edittext
        return true;
    }
}
