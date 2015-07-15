package com.sqbnet.expressassistant;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
public class orderTakeDeliveryFragment extends OrderBaseFragment {

    CheckBox chkbox_items_checked;
    Button btn_confirm;
    private TextView tv_good_count;
    private TextView tv_renueration;
    private TextView tv_distance;
    private TextView tv_company_name;
    private TextView tv_company_address;
    private TextView tv_company_phone;
    private CircleImageView civ_company_image;

    private LinearLayout ly_goto;

    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> mData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_take_delivery_fragment, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
                        Log.i("virgil", "order take delivery");
                        Log.i("virgil", response.getCode());
                        Log.i("virgil", response.getMsg());
                        Log.i("virgil", response.getData().toString());

                        if (response.getCode().equals("1000")) {
                            JSONObject result = (JSONObject) response.getData();
                            mJSONData = result;
                            try {
                                JSONObject company = result.getJSONObject("company");
                                String company_name = company.getString("name");
                                String company_addr = company.getString("addr");
                                String company_phone = company.getString("phone");
                                double company_latitude = company.getDouble("latitude");
                                double company_longitude = company.getDouble("longitude");
                                String company_pic = company.has("pic") ? company.getString("pic") : "";

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

                                tv_company_name.setText(company_name);
                                tv_company_address.setText(company_addr);
                                tv_company_phone.setText(company_phone);

                                tv_renueration.setText(remuneration + "元");
                                tv_good_count.setText(String.valueOf(goods.length()));

                                MyLocation location = GPSLocation.getInst().getCurrentLocation();
                                if(location != null) {
                                    double distance = BaiDuLocationService.getInst().getDistanceBetweenLocations(company_latitude, company_longitude, location.getLatitude(), location.getLongitude());
                                    tv_distance.setText(String.format("%.2f", distance / 1000.0) + "km");
                                }

                                if (company_pic.startsWith("http")) {
                                    AsyncImageLoader.getInst().loadBitmap(company_pic, new AsyncImageLoader.ImageLoadResultLister() {
                                        @Override
                                        public void onImageLoadResult(final Bitmap bitmap) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    civ_company_image.setImageBitmap(bitmap);
                                                }
                                            });
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    void initView(View view) {
        btn_confirm = (Button) view.findViewById(R.id.btn_order_take_confirm_taken);
        chkbox_items_checked = (CheckBox) view.findViewById(R.id.chkbox_order_item_checked);
        chkbox_items_checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                btn_confirm.setEnabled(b);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delegate != null) {
                    SQBProvider.getInst().updateOrderStatus(mOrderId, CustomConstants.ORDER_SHIPPING, new SQBResponseListener() {
                        @Override
                        public void onResponse(final SQBResponse response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (response == null) {
                                        return;
                                    }
                                    Log.i("virgil", "take delivery confirm");
                                    Log.i("virgil", response.getCode());
                                    Log.i("virgil", response.getMsg());
                                    Log.i("virgil", response.getData().toString());
                                    if (response.getCode().equals("1000")) {
                                        delegate.goNext();
                                    }
                                }
                            });
                        }
                    });

                }
            }
        });


        tv_good_count = (TextView) view.findViewById(R.id.tv_order_take_good_count);
        tv_renueration = (TextView) view.findViewById(R.id.tv_order_remuneration);
        tv_distance = (TextView) view.findViewById(R.id.tv_order_distance);
        tv_company_name = (TextView) view.findViewById(R.id.tv_order_company_name);
        tv_company_address = (TextView) view.findViewById(R.id.tv_order_company_address);
        tv_company_phone = (TextView) view.findViewById(R.id.tv_order_company_phone);
        civ_company_image = (CircleImageView) view.findViewById(R.id.civ_order_company_image);
        ly_goto = (LinearLayout) view.findViewById(R.id.ly_order_take_deliver_goto);

        listView = (ListView) view.findViewById(R.id.lv_order_take_details);
        mData = new ArrayList<Map<String, Object>>();
        // For debugging:
       /* for (int i = 0; i < 15; i++) {
            Map<String, Object> hisData0 = new HashMap<String, Object>();
            hisData0.put("good_name", "Rice");
            hisData0.put("good_count", "5.00kg");
            mData.add(hisData0);
        }
        tv_good_count.setText(Integer.toString(mData.size()));*/

        adapter = new SimpleAdapter(getActivity(), mData, R.layout.history_details_fragment_list, new String[] {
                "good_name",
                "good_count"
        }, new int[] {
                R.id.tv_history_details_list_good_name,
                R.id.tv_history_details_list_good_count
        });
        listView.setAdapter(adapter);

        View.OnClickListener phoneOnClickListener = UtilHelper.getPhoneNumberOnClickListener(getActivity());
        tv_company_phone.setOnClickListener(phoneOnClickListener);

        ly_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mJSONData != null) {
                        JSONObject company = mJSONData.getJSONObject("company");
                        UtilHelper.startMapByLocation(
                                getActivity(),
                                company.getDouble("latitude"),
                                company.getDouble("longitude"),
                                company.getString("name"),
                                company.getString("addr"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
