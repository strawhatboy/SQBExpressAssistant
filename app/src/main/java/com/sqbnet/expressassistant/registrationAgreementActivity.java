package com.sqbnet.expressassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;


public class registrationAgreementActivity extends Activity {

    private WebView wv_agreement;
    private ImageButton ibtn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_agreement);

        initView();
    }

    private void initView() {
        wv_agreement = (WebView) findViewById(R.id.wv_registration_agreement);
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_registration_agreement_back);

        wv_agreement.getSettings().setDefaultTextEncodingName("gbk");
        wv_agreement.loadUrl("file:///android_asset/agreement.html");

        ibtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
