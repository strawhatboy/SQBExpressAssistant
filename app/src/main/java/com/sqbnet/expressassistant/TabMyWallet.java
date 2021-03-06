package com.sqbnet.expressassistant;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.controls.CircleImageView;
import com.sqbnet.expressassistant.controls.CustomListView;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.AsyncImageLoader;
import com.sqbnet.expressassistant.utils.UtilHelper;
import com.tencent.bugly.crashreport.CrashReport;

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
public class TabMyWallet extends BaseFragment {

    private CustomListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> mData;
    private TextView tv_count;

    private TextView tv_real_name;
    private TextView tv_id;
    private TextView tv_reg_date;
    private TextView tv_balance;
    private TextView tv_un_reward;
    private CircleImageView civ_wallet_avatar;

    public TabMyWallet() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("virgil", "TabMyWallet onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("virgil", "TabMyWallet onCreateView");
        View view = inflater.inflate(R.layout.tab_my_wallet, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("virgil", "TabMyWallet onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("virgil", "TabMyWallet onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("virgil", "TabMyWallet onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("virgil", "TabMyWallet onDestroy");
    }

    private void initView(View view) {
        //tv_id = (TextView) view.findViewById(R.id.tv_wallet_id);
        tv_real_name = (TextView) view.findViewById(R.id.tv_wallet_username);
        tv_reg_date = (TextView) view.findViewById(R.id.tv_wallet_reg_time);
        tv_balance = (TextView) view.findViewById(R.id.tv_wallet_total_reward);
        tv_un_reward = (TextView) view.findViewById(R.id.tv_wallet_unsettled_reward);

        listView = (CustomListView) view.findViewById(R.id.lv_wallet);
        tv_count = (TextView) view.findViewById(R.id.tv_wallet_count);
        civ_wallet_avatar = (CircleImageView) view.findViewById(R.id.civ_wallet_avatar);

        mData = new ArrayList<Map<String, Object>>();
        /*
        for (int i = 1; i < 15; i++) {
            HashMap<String, Object> data0 = new HashMap<String, Object>();
            data0.put("index", i);
            data0.put("date", "2010-05-09");
            data0.put("duration", "10����");
            data0.put("time", "9:30 - 10:00");
            data0.put("is_settled", "δ����");
            data0.put("reward", "2.00Ԫ");
            data.add(data0);
        }*/

        adapter = new SimpleAdapter(getActivity(), mData, R.layout.tab_my_wallet_list, new String[] {
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
                    view.setBackgroundDrawable(getActivity().getResources().getDrawable(R.color.bg_white));
                } else {
                    view.setBackgroundDrawable(null);
                }
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setonRefreshListener(new CustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("virgil", "TabMyWallet listview onRefresh");
                refreshData(true);
            }
        });

        if(mData.size() == 0){
            Log.i("virgil", "TabMyWallet listview init data");
            listView.startRefresh();
            refreshData(false);
        }

        final String user_id = UtilHelper.getSharedUserId();
        SQBProvider.getInst().getDispatchPerson(user_id, new SQBResponseListener() {
            @Override
            public void onResponse(final SQBResponse response) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("virgil", "TabHsitoryOrder get DispatchPerson");
                            if (response == null) {
                                Toast.makeText(getActivity().getApplicationContext(), "出错啦，请重试", Toast.LENGTH_SHORT);
                                return;
                            }

                            if (!response.getCode().equals("1000")) {
                                Toast.makeText(getActivity().getApplicationContext(), response.getMsg(), Toast.LENGTH_SHORT);
                                return;
                            }

                            try {
                                JSONObject userInfo = (JSONObject) response.getData();
                                String realName = userInfo.getString("name");
                                String regDate = UtilHelper.getDateString(userInfo.getInt("add_time"), "yyyy-MM-dd");
                                String balance = userInfo.getString("balance");
                                String un_reward = userInfo.getString("s_proceeds");
                                String avatar = userInfo.getString("cardphoto");
                                if (un_reward == "null") {
                                    un_reward = "0";
                                }

                                //tv_id.setText(user_id);
                                tv_real_name.setText(realName);
                                tv_reg_date.setText(regDate);
                                tv_balance.setText(balance);
                                tv_un_reward.setText(un_reward);
                                if (avatar != null && avatar.length() > 0) {
                                    AsyncImageLoader.getInst().loadBitmap(avatar, new AsyncImageLoader.ImageLoadResultLister() {
                                        @Override
                                        public void onImageLoadResult(final Bitmap bitmap) {
                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        civ_wallet_avatar.setImageBitmap(bitmap);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }

                                listView.onRefreshComplete();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity().getApplicationContext(), "出错啦，请重试", Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            }
        });
    }

    private void setCount(int count) {
        tv_count.setText(Integer.toString(count));
    }

    public void refreshData(boolean isRefresh) {
        mData.clear();
        setCount(mData.size());
        final String user_id = UtilHelper.getSharedUserId();
        SQBProvider.getInst().getHistoryOrder(user_id, new SQBResponseListener() {
            @Override
            public void onResponse(final SQBResponse response) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.onRefreshComplete();
                            if (response != null) {
                                Log.i("virgil", "TabMyWallet get History order");
                                Log.i("virgil", response.getCode());
                                Log.i("virgil", response.getMsg());
                                Log.i("virgil", response.getData().toString());
                                if (response.getCode().equals("1000")) {
                                    try {
                                        JSONArray orders = (JSONArray) response.getData();
                                        for (int i = orders.length() - 1; i >= 0; i--) {
                                            JSONObject item = orders.getJSONObject(i);
                                            Log.i("virgil", item.toString());
                                            JSONObject orderInfo = item.getJSONObject("orderInfo");
                                            int startTimestamp = item.getInt("starttime");
                                            int endTimestamp = item.optInt("endtime", 0);
                                            int durationTimestamp = endTimestamp - startTimestamp;
                                            Date durationDate = UtilHelper.getDate(durationTimestamp);
                                            String date = UtilHelper.getDateString(startTimestamp, "yyyy-MM-dd");
                                            String startDate = UtilHelper.getDateString(startTimestamp, "HH:mm:ss");
                                            String endDate = UtilHelper.getDateString(endTimestamp, "HH:mm:ss");
                                            //int status = item.getInt("status");
                                            String remuneration = item.getString("remuneration");
                                            //String consignee = orderInfo.getString("consignee");
                                            //String company_name = orderInfo.getJSONObject("company").getString("name");

                                            Map<String, Object> data = new HashMap<String, Object>();
                                            data.put("from_avatar", R.drawable.index_avatar);
                                            data.put("duration", durationDate.getMinutes() + getResources().getString(R.string.unit_minute));
                                            data.put("time", startDate + " -" + endDate);
                                            data.put("date", date);
                                            //data.put("distance", "1.36");
                                            data.put("reward", remuneration);
                                            //data.put("to_name", consignee);
                                            //data.put("to_avatar", R.drawable.index_avatar);
                                            data.put("jsonObject", item);

                                            mData.add(data);
                                        }

                                   /* Collections.sort(mData, new Comparator<Map<String, Object>>() {
                                        @Override
                                        public int compare(Map<String, Object> stringObjectMap, Map<String, Object> t1) {
                                            Date dateLeft = UtilHelper.getString2Date2(stringObjectMap.get("date").toString());
                                            Date dateRight = UtilHelper.getString2Date2(t1.get("date").toString());
                                            //String dateLeft = stringObjectMap.get("time").toString();
                                            //String dateRight = stringObjectMap.get("time").toString();
                                            if (dateLeft.compareTo(dateRight) > 0) {
                                                return 1;
                                            } else {
                                                return 0;
                                            }
                                        }
                                    });*/


                                        setCount(mData.size());
                                        adapter.notifyDataSetChanged();
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
            }
        }, isRefresh);
    }
}
