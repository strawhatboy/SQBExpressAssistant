package com.sqbnet.expressassistant;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabMyWallet extends Fragment {

    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> data;
    private TextView tv_count;

    public TabMyWallet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_my_wallet, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.lv_wallet);
        tv_count = (TextView) view.findViewById(R.id.tv_wallet_count);

        data = new ArrayList<Map<String, Object>>();
        for (int i = 1; i < 15; i++) {
            HashMap<String, Object> data0 = new HashMap<String, Object>();
            data0.put("index", i);
            data0.put("date", "2010-05-09");
            data0.put("duration", "10����");
            data0.put("time", "9:30 - 10:00");
            data0.put("is_settled", "δ����");
            data0.put("reward", "2.00Ԫ");
            data.add(data0);
        }

        setCount(data.size());

        adapter = new SimpleAdapter(getActivity(), data, R.layout.tab_my_wallet_list, new String[] {
                "index",
                "date",
                "duration",
                "time",
                "is_settled",
                "reward"
        }, new int[] {
                R.id.tv_wallet_list_index,
                R.id.tv_wallet_list_date,
                R.id.tv_wallet_list_duration,
                R.id.tv_wallet_list_time,
                R.id.tv_wallet_list_is_settled,
                R.id.tv_wallet_list_reward
        }) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv_index = (TextView) view.findViewById(R.id.tv_wallet_list_index);
                tv_index.setText(Integer.toString(position + 1));
                if (position % 2 == 0) {
                    view.setBackground(getActivity().getResources().getDrawable(R.color.bg_white));
                } else {
                    view.setBackground(null);
                }
                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    private void setCount(int count) {
        tv_count.setText(Integer.toString(count));
    }
}
