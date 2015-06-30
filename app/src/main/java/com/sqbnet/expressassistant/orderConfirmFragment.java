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
 * Created by Andy on 6/29/2015.
 */
public class orderConfirmFragment extends android.support.v4.app.Fragment implements orderMainActivity.IWizardPage {

    orderMainActivity.IWizardPageDelegate delegate;

    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> mData;

    private TextView tv_good_count;
    private Button btn_ok;
    private Button btn_cancel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_confirm_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_good_count = (TextView) view.findViewById(R.id.tv_order_confirm_good_count);
        btn_ok = (Button) view.findViewById(R.id.btn_order_confirm_ok);
        btn_cancel = (Button) view.findViewById(R.id.btn_order_confirm_cancel);

        listView = (ListView) view.findViewById(R.id.lv_order_confirm_details);
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

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delegate != null) {
                    // add rest to server to confirm the order
                    delegate.goNext();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delegate != null) {
                    delegate.exit(ResultCode.ORDER_CANCELED, null);
                }
            }
        });
    }



    @Override
    public void setNextDelegate(orderMainActivity.IWizardPageDelegate delegate) {
        this.delegate = delegate;
    }
}
