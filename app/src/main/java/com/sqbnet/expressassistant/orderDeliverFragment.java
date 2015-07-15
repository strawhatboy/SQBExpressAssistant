package com.sqbnet.expressassistant;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sqbnet.expressassistant.Location.BaiDuLocationService;
import com.sqbnet.expressassistant.Location.GPSLocation;
import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.controls.CircleImageView;
import com.sqbnet.expressassistant.mode.MyLocation;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.AsyncImageLoader;
import com.sqbnet.expressassistant.utils.CustomConstants;
import com.sqbnet.expressassistant.utils.UtilHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andy on 6/30/2015.
 */
public class orderDeliverFragment extends OrderBaseFragment {

    Button btn_confirm;
    private TextView tv_good_count;
    private TextView tv_distance;
    private TextView tv_renueration;
    private CircleImageView civ_customer_image;
    private TextView tv_customer_name;
    private TextView tv_customer_address;
    private TextView tv_customer_phone;

    private LinearLayout ly_goto;

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
        tv_renueration = (TextView) view.findViewById(R.id.tv_order_remuneration);
        tv_customer_name = (TextView) view.findViewById(R.id.tv_order_customer_name);
        tv_customer_address = (TextView) view.findViewById(R.id.tv_order_customer_address);
        tv_customer_phone = (TextView) view.findViewById(R.id.tv_order_customer_phone);
        tv_distance = (TextView) view.findViewById(R.id.tv_order_distance);
        civ_customer_image = (CircleImageView) view.findViewById(R.id.civ_order_customer_image);

        listView = (ListView) view.findViewById(R.id.lv_order_deliver_details);

        ly_goto = (LinearLayout) view.findViewById(R.id.ly_order_deliver_goto);
        mData = new ArrayList<Map<String, Object>>();
        // For debugging:
        /*for (int i = 0; i < 15; i++) {
            Map<String, Object> hisData0 = new HashMap<String, Object>();
            hisData0.put("good_name", "Rice");
            hisData0.put("good_count", "5.00kg");
            mData.add(hisData0);
        }
        tv_good_count.setText(Integer.toString(mData.size()));
*/
        adapter = new SimpleAdapter(getActivity(), mData, R.layout.history_details_fragment_list, new String[] {
                "good_name",
                "good_count"
        }, new int[] {
                R.id.tv_history_details_list_good_name,
                R.id.tv_history_details_list_good_count
        });
        listView.setAdapter(adapter);

        View.OnClickListener phoneOnClickListener = UtilHelper.getPhoneNumberOnClickListener(getActivity());
        tv_customer_phone.setOnClickListener(phoneOnClickListener);

        ly_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mJSONData != null) {
                        UtilHelper.startMapByAddress(
                                getActivity(),
                                mJSONData.getString("address"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    void loadData() {
        if(mOrderId == null){
            return;
        }

        SQBProvider.getInst().getOrderInfo(mOrderId, new SQBResponseListener() {
            @Override
            public void onResponse(final SQBResponse response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response == null) {
                            return;
                        }
                        Log.i("virgil", "order confirm");
                        Log.i("virgil", response.getCode());
                        Log.i("virgil", response.getMsg());
                        Log.i("virgil", response.getData().toString());

                        if (response.getCode().equals("1000")) {
                            JSONObject result = (JSONObject) response.getData();
                            mJSONData = result;
                            try {
                                String customer_name = result.getString("consignee");
                                String customer_address = result.getString("address");
                                String customer_phone = result.getString("mobile");
                                String customer_pic = result.has("headimgurl") ? result.getString("headimgurl") : "";

                                String remuneration = result.getString("remuneration");

                                JSONArray goods = result.getJSONArray("goods");
                                for (int i = 0; i < goods.length(); i++) {
                                    JSONObject item = goods.getJSONObject(i);
                                    String number = item.getString("goods_number");
                                    String name = item.getString("goods_name");
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("good_name", name);
                                    map.put("good_count", number);
                                    mData.add(map);
                                }

                                adapter.notifyDataSetChanged();

                                tv_customer_name.setText(customer_name);
                                tv_customer_address.setText(customer_address);
                                tv_customer_phone.setText(customer_phone);

                                tv_renueration.setText(remuneration + "元");

                                orderFinishFragment.Remuneration = remuneration;

                                tv_good_count.setText(String.valueOf(goods.length()));

                                if(customer_pic.startsWith("http")) {
                                    AsyncImageLoader.getInst().loadBitmap(customer_pic, new AsyncImageLoader.ImageLoadResultLister() {
                                        @Override
                                        public void onImageLoadResult(final Bitmap bitmap) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    civ_customer_image.setImageBitmap(bitmap);
                                                }
                                            });
                                        }
                                    });
                                }

                                final MyLocation location = GPSLocation.getInst().getCurrentLocation();
                                if(location != null) {
                                    BaiDuLocationService.getInst().getLocationByAddress(customer_address, new BaiDuLocationService.IGeoEncoderCallback() {
                                        @Override
                                        public void handleLocationGot(double latitude, double longitude) {
                                            if (latitude != -1) {
                                                double distance = BaiDuLocationService.getInst().getDistanceBetweenLocations(
                                                        location.getLatitude(),
                                                        location.getLongitude(),
                                                        latitude,
                                                        longitude);
                                                Log.d("orderConfirmFragment", "got distance: " + distance);
                                                tv_distance.setText(String.format("%.2f", distance / 1000.0) + "km");
                                            }
                                        }

                                        @Override
                                        public void handleAddressGot(String address) {

                                        }
                                    });
                                }


                            } catch (Exception e) {
                                Log.e("orderDeliverFragment", "virgil", e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }
}
