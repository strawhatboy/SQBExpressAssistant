package com.sqbnet.expressassistant.controls;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by virgil on 7/2/15.
 */
public class CustomProgressDialog extends Dialog {
    private Context context = null;

    public CustomProgressDialog(Context context){
        super(context);
        this.context = context;
    }

    public CustomProgressDialog(Context context, int theme){
        super(context,theme);
    }

}
