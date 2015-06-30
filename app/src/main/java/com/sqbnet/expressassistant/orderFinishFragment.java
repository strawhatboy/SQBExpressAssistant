package com.sqbnet.expressassistant;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Andy on 6/30/2015.
 */
public class orderFinishFragment extends android.support.v4.app.Fragment implements orderMainActivity.IWizardPage {

    orderMainActivity.IWizardPageDelegate delegate;

    EditText et_delivery_code;
    Button btn_confirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_finish_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        btn_confirm = (Button) view.findViewById(R.id.btn_order_finish_confirm);
        et_delivery_code = (EditText) view.findViewById(R.id.et_order_finish_delivery_code);
        et_delivery_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = editable.toString();
                btn_confirm.setEnabled(!content.equals(""));
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delegate != null) {
                    delegate.exit(ResultCode.ORDER_DONE, /* balance got */ "4.88");
                }
            }
        });
    }

    @Override
    public void setNextDelegate(orderMainActivity.IWizardPageDelegate delegate) {
        this.delegate = delegate;
    }
}
