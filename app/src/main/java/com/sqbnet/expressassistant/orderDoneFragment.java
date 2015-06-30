package com.sqbnet.expressassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Andy on 6/30/2015.
 *
 * this fragment is in the main activity's viewpager
 */
public class orderDoneFragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_done_fragment, container, false);
        initView(view);
        return view;
    }

    void initView(View view) {

    }
}
