package com.sqbnet.expressassistant;


import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabHistoryOrder extends Fragment {

    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> data;
    private Animation textRotateAnimation;
    public IGotoDetails gotoDetails;

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
        textRotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotated_text_view);
        textRotateAnimation.setFillAfter(true);
        listView = (ListView) view.findViewById(R.id.lv_history);

        data = new ArrayList<Map<String, Object>>();


        //TODO: read the historical order from the server by api call
        // For debugging:
        for (int i = 0; i < 15; i++) {
            Map<String, Object> hisData0 = new HashMap<String, Object>();
            hisData0.put("from_avatar", R.drawable.index_avatar);
            hisData0.put("from_name", "小晶石日用百货店");
            hisData0.put("time", "2015.05.11 15:21:14");
            hisData0.put("distance", "1.36");
            hisData0.put("reward", "4.88");
            hisData0.put("to_name", "ThreeLeaves");
            hisData0.put("to_avatar", R.drawable.index_avatar);
            data.add(hisData0);
        }

        adapter = new SimpleAdapter(getActivity(), data, R.layout.tab_history_order_list,
                new String[] {
                        "from_avatar",
                        "from_name",
                        "time",
                        "distance",
                        "reward",
                        "to_name",
                        "to_avatar"
                },
                new int[] {
                        R.id.civ_history_list_from_avatar,
                        R.id.tv_history_list_from_name,
                        R.id.tv_history_list_time,
                        R.id.tv_history_list_distance,
                        R.id.tv_history_list_reward,
                        R.id.tv_history_list_to_name,
                        R.id.civ_history_list_to_avatar
                }) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                //TODO: set visibility for 'done' according to the real status from server
                // rotate 'Done'
                TextView tv_done = (TextView) view.findViewById(R.id.tv_history_list_done);
                tv_done.setAnimation(textRotateAnimation);
                return view;
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("history", "item " + i + "clicked!");
                if (gotoDetails != null) {
                    gotoDetails.gotoDetails(null /*data*/);
                }
            }
        });
    }

    public interface IGotoDetails {
        void gotoDetails(JSONObject data);
        void back();
    }
}
