package com.sqbnet.expressassistant;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Andy on 6/27/2015.
 */
public class historyDetailsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_details_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

    }
}
