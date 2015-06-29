package com.sqbnet.expressassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Andy on 6/29/2015.
 */
public class orderConfirmFragment extends android.support.v4.app.Fragment implements orderMainActivity.IWizardPage {

    orderMainActivity.IGotoNextFragment delegate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_confirm_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
    }

    @Override
    public void setNextDelegate(orderMainActivity.IGotoNextFragment delegate) {
        delegate = delegate;
    }
}
