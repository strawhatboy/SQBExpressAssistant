package com.sqbnet.expressassistant.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.sqbnet.expressassistant.MyApplication;
import com.sqbnet.expressassistant.R;

/**
 * Created by Andy on 7/7/2015.
 */
public class SingletonObjects {

    private static SingletonObjects sInst;
    private View.OnClickListener phoneOnClickListener;

    public static SingletonObjects getInst(){
        if(sInst == null){
            synchronized (SingletonObjects.class){
                if(sInst == null){
                    sInst = new SingletonObjects();
                }
            }
        }
        return sInst;
    }

    public View.OnClickListener getPhoneNumberOnClickListener() {
        if (phoneOnClickListener != null) {
            return phoneOnClickListener;
        }

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof TextView) {
                    final String phoneNumber = ((TextView) view).getText().toString();

                    if (phoneNumber.matches("[\\d-]+")) {
                        final Activity activity = MyApplication.getInst().currentActivity();
                        new AlertDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.dialog_dial))
                                .setMessage(activity.getResources().getString(R.string.intent_call) + " " + phoneNumber + " ?")
                                .setPositiveButton(activity.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                                        activity.startActivity(intent);
                                    }
                                })
                                .setNegativeButton(activity.getResources().getString(R.string.dialog_no), null)
                                .show();
                    }
                }
            }
        };
    }
}
