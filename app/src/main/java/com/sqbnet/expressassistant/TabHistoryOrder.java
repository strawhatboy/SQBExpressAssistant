package com.sqbnet.expressassistant;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
public class TabHistoryOrder extends Fragment {

    private ListView listView;
    SimpleAdapter adapter;
    List<Map<String, Object>> data;

    public TabHistoryOrder() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_history_order, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.lv_history);

        data = new ArrayList<Map<String, Object>>();


        //TODO: read the historical order from the server by api call
        // For debugging:
        Map<String, Object> hisData0 = new HashMap<String, Object>();
        hisData0.put("iv_history_list_avatar", R.drawable.index_avatar);
        hisData0.put("tv_history_list_client_name", "小晶石日用百货店");
        hisData0.put("tv_history_list_time", "2015.05.11 15:21:14");
        hisData0.put("tv_history_list_distance", "1.36km");
        hisData0.put("tv_history_list_reward", "4.88元");
        Map<String, Object> hisData1 = new HashMap<String, Object>();
        hisData1.put("iv_history_list_avatar", R.drawable.index_avatar);
        hisData1.put("tv_history_list_client_name", "hahahahaha");
        hisData1.put("tv_history_list_time", "2015.06.11 15:22:14");
        hisData1.put("tv_history_list_distance", "5.36km");
        hisData1.put("tv_history_list_reward", "22.28元");

        data.add(hisData0);
        data.add(hisData1);

        adapter = new SimpleAdapter(getActivity(), data, R.layout.tab_history_order_list,
                new String[] {
                        "iv_history_list_avatar",
                        "tv_history_list_client_name",
                        "tv_history_list_time",
                        "tv_history_list_distance",
                        "tv_history_list_reward"
                },
                new int[] {
                        R.id.iv_history_list_avatar,
                        R.id.tv_history_list_client_name,
                        R.id.tv_history_list_time,
                        R.id.tv_history_list_distance,
                        R.id.tv_history_list_reward
                });
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("history", "item" + i + "clicked!");
            }
        });
    }
}
