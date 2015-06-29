package com.sqbnet.expressassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Andy on 6/29/2015.
 */
public class orderGotFragment extends android.support.v4.app.Fragment implements orderMainActivity.IWizardPage {

    orderMainActivity.IGotoNextFragment delegate;

    private Button btn_seeDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_got_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        btn_seeDetails = (Button) view.findViewById(R.id.btn_order_got_see_details);

        btn_seeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delegate != null) {
                    delegate.goNext();
                }
            }
        });
    }

    @Override
    public void setNextDelegate(orderMainActivity.IGotoNextFragment delegate) {
        this.delegate = delegate;
    }
}
