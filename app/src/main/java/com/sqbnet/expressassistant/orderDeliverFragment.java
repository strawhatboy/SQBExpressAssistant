package com.sqbnet.expressassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andy on 6/30/2015.
 */
public class orderDeliverFragment extends android.support.v4.app.Fragment implements orderMainActivity.IWizardPage {

    orderMainActivity.IWizardPageDelegate delegate;
    Button btn_confirm;
    private TextView tv_good_count;

    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> mData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_deliver_fragment, container, false);
        initView(view);
        return view;
    }

    void initView(View view) {
        btn_confirm = (Button) view.findViewById(R.id.btn_order_deliver_confirm);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delegate != null) {
                    delegate.goNext();
                }
            }
        });


        tv_good_count = (TextView) view.findViewById(R.id.tv_order_deliver_good_count);

        listView = (ListView) view.findViewById(R.id.lv_order_deliver_details);
        mData = new ArrayList<Map<String, Object>>();
        // For debugging:
        for (int i = 0; i < 15; i++) {
            Map<String, Object> hisData0 = new HashMap<String, Object>();
            hisData0.put("good_name", "Rice");
            hisData0.put("good_count", "5.00kg");
            mData.add(hisData0);
        }
        tv_good_count.setText(Integer.toString(mData.size()));

        adapter = new SimpleAdapter(getActivity(), mData, R.layout.history_details_fragment_list, new String[] {
                "good_name",
                "good_count"
        }, new int[] {
                R.id.tv_history_details_list_good_name,
                R.id.tv_history_details_list_good_count
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void setNextDelegate(orderMainActivity.IWizardPageDelegate delegate) {
        this.delegate = delegate;
    }
}
