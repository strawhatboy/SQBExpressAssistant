package com.sqbnet.expressassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
 * Created by Andy on 6/27/2015.
 */
public class historyDetailsFragment extends android.support.v4.app.Fragment {
    private ImageButton backButton;
    private Animation textRotateAnimation;
    private TextView tv_done;
    public TabHistoryOrder.IGotoDetails gotoDetails;
    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_details_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        textRotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotated_text_view);
        textRotateAnimation.setFillAfter(true);

        tv_done = (TextView) view.findViewById(R.id.tv_history_details_done);
        tv_done.setAnimation(textRotateAnimation);

        listView = (ListView) view.findViewById(R.id.lv_history_details);

        backButton = (ImageButton) view.findViewById(R.id.ibtn_histroy_details_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gotoDetails != null) {
                    gotoDetails.back();
                }
            }
        });

        data = new ArrayList<Map<String, Object>>();
        // For debugging:
        for (int i = 0; i < 15; i++) {
            Map<String, Object> hisData0 = new HashMap<String, Object>();
            hisData0.put("good_name", "Rice");
            hisData0.put("good_count", "5.00kg");
            data.add(hisData0);
        }

        adapter = new SimpleAdapter(getActivity(), data, R.layout.history_details_fragment_list, new String[] {
                "good_name",
                "good_count"
        }, new int[] {
                R.id.tv_history_details_list_good_name,
                R.id.tv_history_details_list_good_count
        });

        listView.setAdapter(adapter);
    }

    public void setData(JSONObject data) {

    }
}
