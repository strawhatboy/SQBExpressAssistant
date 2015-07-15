package com.sqbnet.expressassistant;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.controls.CustomListView;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.AsyncImageLoader;
import com.sqbnet.expressassistant.utils.UtilHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabHistoryOrder extends BaseFragment {

    private CustomListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> mData;
    private Animation textRotateAnimation;
    public IGotoDetails gotoDetails;

    public TabHistoryOrder() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("virgil", "TabHistoryOrder onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("virgil", "TabHistoryOrder onCreateView");
        View view = inflater.inflate(R.layout.tab_history_order, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        Log.i("virgil", "TabHistoryOrder onResume");
        super.onResume();
    }

    private void initView(View view) {
        textRotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotated_text_view);
        textRotateAnimation.setFillAfter(true);
        listView = (CustomListView) view.findViewById(R.id.lv_history);

        mData = new ArrayList<Map<String, Object>>();


        //TODO: read the historical order from the server by api call
        // For debugging:
/*        for (int i = 0; i < 15; i++) {
            Map<String, Object> hisData0 = new HashMap<String, Object>();
            hisData0.put("from_avatar", R.drawable.index_avatar);
            hisData0.put("from_name", "С��ʯ���ðٻ���");
            hisData0.put("time", "2015.05.11 15:21:14");
            hisData0.put("distance", "1.36");
            hisData0.put("reward", "4.88");
            hisData0.put("to_name", "ThreeLeaves");
            hisData0.put("to_avatar", R.drawable.index_avatar);
            data.add(hisData0);
        }*/

        adapter = new SimpleAdapter(getActivity(), mData, R.layout.tab_history_order_list,
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
                if ((Integer) mData.get(position).get("status") == 1) {
                    TextView tv_done = (TextView) view.findViewById(R.id.tv_history_list_done);
                    tv_done.setVisibility(View.VISIBLE);
                    tv_done.setAnimation(textRotateAnimation);
                }

                if (position % 2 == 0) {
                    view.setBackgroundDrawable(getActivity().getResources().getDrawable(R.color.bg_white));
                } else {
                    view.setBackgroundDrawable(null);
                }

                return view;
            }
        };

        adapter.setViewBinder(new AsyncImageLoader.AsyncImageViewBinder(getActivity()));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("history", "item " + i + "clicked!");
                if (gotoDetails != null) {
                    JSONObject jsonObject = (JSONObject) (mData.get(i).get("jsonObject"));
                    gotoDetails.gotoDetails(jsonObject);
                }
            }
        });

        listView.setonRefreshListener(new CustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("virgil", "CustomListView onRefresh");
                refreshData();
            }
        });

        if(mData.size() == 0){
            refreshData();
        }
    }

    private void refreshData(){
        mData.clear();
        String user_id = UtilHelper.getSharedUserId();
        SQBProvider.getInst().getHistoryOrder(user_id, new SQBResponseListener() {
            @Override
            public void onResponse(final SQBResponse response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response != null) {
                            Log.i("virgil", "TabHsitoryOrder getHisotryOrder");
                            Log.i("virgil", response.getCode());
                            Log.i("virgil", response.getMsg());
                            Log.i("virgil", response.getData().toString());
                            if (response.getCode().equals("1000")) {
                                try {
                                    JSONArray orders = (JSONArray) response.getData();
                                    for (int i = orders.length() -1; i >= 0; i--) {
                                        JSONObject item = orders.getJSONObject(i);
                                        Log.i("virgil", item.toString());
                                        JSONObject orderInfo = item.getJSONObject("orderInfo");
                                        int startTimestamp = item.getInt("starttime");
                                        String date = UtilHelper.getDateString(startTimestamp);
                                        int status = item.getInt("status");
                                        String remuneration = item.getString("remuneration");
                                        String consignee = orderInfo.getString("consignee");
                                        JSONObject company = orderInfo.getJSONObject("company");
                                        String company_name = company.getString("name");
                                        String company_pic = company.has("pic") ? company.getString("pic") : "";

                                        Map<String, Object> data = new HashMap<String, Object>();
                                        data.put("from_avatar", company_pic);
                                        data.put("from_name", company_name);
                                        data.put("time", date);
                                        data.put("distance", "1.36");
                                        data.put("reward", remuneration);
                                        data.put("to_name", consignee);
                                        data.put("to_avatar", orderInfo.has("headimgurl") ? orderInfo.getString("headimgurl") : "");
                                        data.put("status", status);
                                        data.put("jsonObject", item);

                                        mData.add(data);
                                    }

                                  /*  Collections.sort(mData, new Comparator<Map<String, Object>>() {
                                        @Override
                                        public int compare(Map<String, Object> stringObjectMap, Map<String, Object> t1) {
                                            //Date dateLeft = UtilHelper.getString2Date(stringObjectMap.get("time").toString());
                                            //Date dateRight = UtilHelper.getString2Date(t1.get("time").toString());

                                            String dateLeft = stringObjectMap.get("time").toString();
                                            String dateRight = stringObjectMap.get("time").toString();
                                            if(dateLeft.compareTo(dateRight) > 0){
                                                return 1;
                                            }else {
                                                return 0;
                                            }
                                        }
                                    });*/

                                    adapter.notifyDataSetChanged();
                                    listView.onRefreshComplete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(), "出错啦，请重试", Toast.LENGTH_SHORT);
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), response.getMsg(), Toast.LENGTH_SHORT);
                            }
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "出错啦，请重试", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
    }

    public interface IGotoDetails {
        void gotoDetails(JSONObject data);
        void back();
    }

    /**
     * Created by virgil on 7/7/15.
     */
    public static class BaseFragment {
    }
}
