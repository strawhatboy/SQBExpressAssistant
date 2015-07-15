package com.sqbnet.expressassistant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.controls.CircleImageView;
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
 * Created by Andy on 6/29/2015.
 */
public class orderConfirmFragment extends OrderBaseFragment {

    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> mData;

    private TextView tv_company_name;
    private TextView tv_company_address;
    private TextView tv_company_phone;
    private CircleImageView civ_company_image;
    private TextView tv_customer_name;
    private TextView tv_customer_address;
    private TextView tv_customer_phone;
    private CircleImageView civ_customer_image;
    private TextView tv_remuneration;
    private TextView tv_distance;
    private TextView tv_good_count;
    private Button btn_ok;
    private Button btn_cancel;

    private LinearLayout ly_from_goto;
    private LinearLayout ly_to_goto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_confirm_fragment, container, false);
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
                        Log.i("virgil", "order confirm");
                        Log.i("virgil", response.getCode());
                        Log.i("virgil", response.getMsg());
                        Log.i("virgil", response.getData().toString());

                        if(response.getCode().equals("1000")) {
                            JSONObject result = (JSONObject) response.getData();
                            mJSONData = result;
                            try {

                                String id = result.getString("d_id");
                                if(!id.equals(UtilHelper.getSharedUserId())) {
                                    new AlertDialog.Builder(getActivity()).setTitle(getActivity().getResources().getString(R.string.dialog_title_info))
                                            .setMessage("接单超时，该单已指派给他人")
                                            .setPositiveButton("退出接单", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    getActivity().finish();
                                                }
                                            })
                                            .show();

                                    return;
                                }
                                JSONObject company = result.getJSONObject("company");
                                String company_name = company.getString("name");
                                String company_addr = company.getString("addr");
                                String company_phone = company.getString("phone");
                                final double company_latitude = company.getDouble("latitude");
                                final double company_longitude = company.getDouble("longitude");
                                String company_pic = company.has("pic") ? company.getString("pic") : "";

                                String customer_name = result.getString("consignee");
                                String customer_province = result.getString("province");
                                String customer_city = result.getString("city");
                                String customer_address = result.getString("address");
                                String customer_phone = result.getString("mobile");
                                String customer_pic = result.has("headimgurl") ? result.getString("headimgurl") : "";

                                String remuneration = result.getString("remuneration");

                                JSONArray goods = result.getJSONArray("goods");
                                for(int i=0; i<goods.length(); i++){
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

                                tv_customer_name.setText(customer_name);
                                tv_customer_address.setText(customer_address);
                                tv_customer_phone.setText(customer_phone);

                                tv_remuneration.setText(remuneration + "元");
                                tv_good_count.setText(String.valueOf(goods.length()));

                                if(company_pic.startsWith("http")) {
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

                                // calculate distance
                                BaiDuLocationService.getInst().getLocationByAddress(customer_address, new BaiDuLocationService.IGeoEncoderCallback() {
                                    @Override
                                    public void handleLocationGot(double latitude, double longitude) {
                                        if (latitude != -1) {
                                            double distance = BaiDuLocationService.getInst().getDistanceBetweenLocations(
                                                    company_latitude,
                                                    company_longitude,
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

                            }catch (Exception e){
                                Log.e("orderConfirmFragment", "virgil", e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    private void initView(View view) {
        tv_company_name = (TextView) view.findViewById(R.id.tv_order_company_name);
        tv_company_address = (TextView) view.findViewById(R.id.tv_order_company_address);
        tv_company_phone = (TextView) view.findViewById(R.id.tv_order_company_phone);
        civ_company_image = (CircleImageView) view.findViewById(R.id.civ_order_company_image);
        tv_customer_name = (TextView) view.findViewById(R.id.tv_order_customer_name);
        tv_customer_address = (TextView) view.findViewById(R.id.tv_order_customer_address);
        tv_customer_phone = (TextView) view.findViewById(R.id.tv_order_customer_phone);
        civ_customer_image = (CircleImageView) view.findViewById(R.id.civ_order_customer_image);

        tv_remuneration = (TextView) view.findViewById(R.id.tv_order_remuneration);
        tv_distance = (TextView) view.findViewById(R.id.tv_order_distance);
        tv_good_count = (TextView) view.findViewById(R.id.tv_order_confirm_good_count);
        btn_ok = (Button) view.findViewById(R.id.btn_order_confirm_ok);
        btn_cancel = (Button) view.findViewById(R.id.btn_order_confirm_cancel);

        listView = (ListView) view.findViewById(R.id.lv_order_confirm_details);

        ly_from_goto = (LinearLayout) view.findViewById(R.id.ly_order_confirm_from_goto);
        ly_to_goto = (LinearLayout) view.findViewById(R.id.ly_order_confirm_to_goto);

        mData = new ArrayList<Map<String, Object>>();
        // For debugging:
        /*for (int i = 0; i < 15; i++) {
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

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delegate != null) {
                    // add rest to server to confirm the order
                    SQBProvider.getInst().updateOrderStatus(mOrderId, CustomConstants.ORDER_ACCEPT, new SQBResponseListener() {
                        @Override
                        public void onResponse(final SQBResponse response) {
                            if (response == null) {
                                return;
                            }
                            Log.i("virgil", "orderAccept");
                            Log.i("virgil", response.getCode());
                            Log.i("virgil", response.getMsg());
                            Log.i("virgil", response.getData().toString());
                            if (response.getCode().equals("1000")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        delegate.goNext();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delegate != null) {
                    SQBProvider.getInst().giveupOrder(mUserId, mOrderId, new SQBResponseListener() {
                        @Override
                        public void onResponse(SQBResponse response) {
                            if(response == null)
                                return;
                            Log.i("virgil", "giveupOrder");
                            Log.i("virgil", response.getCode());
                            Log.i("virgil", response.getMsg());
                            Log.i("virgil", response.getData().toString());
                        }
                    });
                    delegate.exit(ResultCode.ORDER_CANCELED, null);
                }
            }
        });

        View.OnClickListener phoneOnClickListener = UtilHelper.getPhoneNumberOnClickListener(getActivity());
        tv_company_phone.setOnClickListener(phoneOnClickListener);
        tv_customer_phone.setOnClickListener(phoneOnClickListener);

        ly_from_goto.setOnClickListener(new View.OnClickListener() {
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

        ly_to_goto.setOnClickListener(new View.OnClickListener() {
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
}
