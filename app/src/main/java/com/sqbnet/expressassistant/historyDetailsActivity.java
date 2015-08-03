package com.sqbnet.expressassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sqbnet.expressassistant.utils.AsyncImageLoader;
import com.sqbnet.expressassistant.utils.UtilHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andy on 6/27/2015.
 */
public class historyDetailsActivity extends BaseActivity {
    private ImageButton backButton;
    private Animation textRotateAnimation;
    private TextView tv_done;
    public TabHistoryOrder.IGotoDetails gotoDetails;
    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, Object>> mData;

    private TextView mCompanyName;
    private TextView mCompanyAddress;
    private TextView mCompanyPhone;
    private TextView mGoodsCount;
    private TextView mGoodsHasWeight;
    private TextView mDistance;
    private TextView mStartTime;
    private TextView mPickTime;
    private TextView mEndTime;
    private TextView mRemuneration;
    private TextView mTotalDuration;
    private TextView mRemuneration2;
    private TextView mConsigneeName;
    private TextView mConsigneeAddress;
    private TextView mConsigneePhone;
    private TextView mDuration1;
    private TextView mDuration2;

    private ImageView iv_Company;
    private ImageView iv_Consignee;

    private LinearLayout ly_FromGoto;
    private LinearLayout ly_ToGoto;
    protected JSONObject mJSONData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_details_fragment);

        initView();
        Intent intent = getIntent();
        if (intent != null) {
            try {
                JSONObject data = new JSONObject(intent.getStringExtra("details"));
                setData(data);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("historyDetailsActivity", "Failed to get the input object");
            }
        }
    }

    private void initView() {
        mCompanyName = (TextView) findViewById(R.id.tv_history_details_company_name);
        mCompanyAddress = (TextView) findViewById(R.id.tv_history_details_company_address);
        mCompanyPhone = (TextView) findViewById(R.id.tv_history_details_company_phone);
        mGoodsCount = (TextView) findViewById(R.id.tv_history_details_list_good_count);
        mDistance = (TextView) findViewById(R.id.tv_history_details_distance);
        mStartTime = (TextView) findViewById(R.id.tv_history_details_order_got_time);
        mPickTime = (TextView) findViewById(R.id.tv_history_details_delivery_taken_time);
        mEndTime = (TextView) findViewById(R.id.tv_history_details_delivered_time);
        mRemuneration = (TextView) findViewById(R.id.tv_history_details_remuneration);
        mTotalDuration = (TextView) findViewById(R.id.tv_history_details_total_duration);
        mRemuneration2 = (TextView) findViewById(R.id.tv_history_details_remuneration2);
        mConsigneeName = (TextView) findViewById(R.id.tv_history_details_consignee);
        mConsigneeAddress = (TextView) findViewById(R.id.tv_history_details_consignee_address);
        mConsigneePhone = (TextView) findViewById(R.id.tv_history_details_consignee_phone);
        iv_Company = (ImageView) findViewById(R.id.civ_history_details_from_avatar);
        iv_Consignee = (ImageView) findViewById(R.id.civ_history_details_to_avatar);
        ly_FromGoto = (LinearLayout) findViewById(R.id.ly_history_details_from_goto);
        ly_ToGoto = (LinearLayout) findViewById(R.id.ly_history_details_to_goto);
        mDuration1 = (TextView) findViewById(R.id.tv_history_details_1st_duration);
        mDuration2 = (TextView) findViewById(R.id.tv_history_details_2nd_duration);

        textRotateAnimation = AnimationUtils.loadAnimation(historyDetailsActivity.this, R.anim.rotated_text_view);
        textRotateAnimation.setFillAfter(true);

        tv_done = (TextView) findViewById(R.id.tv_history_details_done);
        tv_done.setAnimation(textRotateAnimation);

        listView = (ListView) findViewById(R.id.lv_history_details);

        backButton = (ImageButton) findViewById(R.id.ibtn_histroy_details_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gotoDetails != null) {
                    gotoDetails.back();
                }
                finish();
            }
        });

        mData = new ArrayList<Map<String, Object>>();
        // For debugging:
       /* for (int i = 0; i < 15; i++) {
            Map<String, Object> hisData0 = new HashMap<String, Object>();
            hisData0.put("good_name", "Rice");
            hisData0.put("good_count", "5.00kg");
            data.add(hisData0);
        }*/

        adapter = new SimpleAdapter(historyDetailsActivity.this, mData, R.layout.history_details_fragment_list, new String[] {
                "good_name",
                "good_count"
        }, new int[] {
                R.id.tv_history_details_list_good_name,
                R.id.tv_history_details_list_good_count
        });

        listView.setAdapter(adapter);

        View.OnClickListener phoneOnClickListener = UtilHelper.getPhoneNumberOnClickListener(historyDetailsActivity.this);
        mCompanyPhone.setOnClickListener(phoneOnClickListener);
        mConsigneePhone.setOnClickListener(phoneOnClickListener);

        ly_FromGoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mJSONData != null) {
                        JSONObject orderInfo = mJSONData.getJSONObject("orderInfo");
                        JSONObject company = orderInfo.getJSONObject("company");
                        UtilHelper.startMapByLocation(
                                historyDetailsActivity.this,
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

        ly_ToGoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mJSONData != null) {
                        JSONObject orderInfo = mJSONData.getJSONObject("orderInfo");
                        UtilHelper.startMapByAddress(
                                historyDetailsActivity.this,
                                orderInfo.getString("address"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void setData(JSONObject data) {
        if(data == null){
            return;
        }

        mJSONData = data;

        try {
            JSONObject orderInfo = data.getJSONObject("orderInfo");
            JSONObject company = orderInfo.getJSONObject("company");
            mCompanyName.setText(company.getString("name"));
            mCompanyAddress.setText(company.getString("addr"));
            mCompanyPhone.setText(company.getString("phone"));

            mData.clear();
            JSONArray goods = orderInfo.getJSONArray("goods");
            mGoodsCount.setText(String.valueOf(goods.length()));
            for(int i=0; i<goods.length(); i++){
                JSONObject item = (JSONObject)goods.get(i);
                Map<String, Object> goodData = new HashMap<String, Object>();
                goodData.put("good_name", item.getString("goods_name"));
                goodData.put("good_count", item.getString("goods_number"));
                mData.add(goodData);
            }
            adapter.notifyDataSetChanged();

            String companyAvatar = company.has("pic") ? company.getString("pic") : "";
            String consigneeAvatar = orderInfo.has("headimgurl") ? orderInfo.getString("headimgurl") : "";
            if (companyAvatar.startsWith("http")) {
                AsyncImageLoader.getInst().loadBitmap(companyAvatar, new AsyncImageLoader.ImageLoadResultLister() {
                    @Override
                    public void onImageLoadResult(final Bitmap bitmap) {
                        try {
                            historyDetailsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_Company.setImageBitmap(bitmap);
                                }
                            });
                        }catch (Exception e){
                            Log.e("historyDetailFragment", "virgil", e);
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (consigneeAvatar.startsWith("http")) {
                AsyncImageLoader.getInst().loadBitmap(consigneeAvatar, new AsyncImageLoader.ImageLoadResultLister() {
                    @Override
                    public void onImageLoadResult(final Bitmap bitmap) {
                        try {
                            historyDetailsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_Consignee.setImageBitmap(bitmap);
                                }
                            });
                        }catch (Exception e){
                            Log.e("historyDetailFragment", "virgil", e);
                            e.printStackTrace();
                        }
                    }
                });

                if (data.getString("status").equals("0")) {
                    tv_done.setVisibility(View.INVISIBLE);
                }
            }

            long startTimestamp = data.optLong("starttime", 0);
            long endTimestamp = data.optLong("endtime", 0);
            long recieveTimestamp = data.optLong("receivetime", 0);
            mStartTime.setText(UtilHelper.getDateString(startTimestamp).split("\\s+")[1]);
            mPickTime.setText(UtilHelper.getDateString(recieveTimestamp).split("\\s+")[1]);
            mEndTime.setText(UtilHelper.getDateString(endTimestamp).split("\\s+")[1]);
            long durationTimestamp = endTimestamp - startTimestamp;
            Date durationDate = UtilHelper.getDate(durationTimestamp);
            long duration1to2 = recieveTimestamp - startTimestamp;
            Date duration1to2Date = UtilHelper.getDate(duration1to2);
            long duration2to3 = endTimestamp - recieveTimestamp;
            Date duration2to3Date = UtilHelper.getDate(duration2to3);

            mRemuneration.setText(data.getString("remuneration") + getResources().getString(R.string.unit_yuan));
            mRemuneration2.setText(data.getString("remuneration"));
            mTotalDuration.setText(Integer.toString(durationDate.getMinutes()));
            mDuration1.setText(Integer.toString(duration1to2Date.getMinutes()));
            mDuration2.setText(Integer.toString(duration2to3Date.getMinutes()));

            mConsigneeName.setText(orderInfo.getString("consignee"));
            mConsigneeAddress.setText(orderInfo.getString("address"));
            mConsigneePhone.setText(orderInfo.getString("mobile"));
            int status = orderInfo.optInt("status", 0);
            if(status == 0){
                tv_done.setVisibility(View.GONE);
            }

            Object o_distance = data.get("distance");
            Double distance = -1d;
            if (o_distance != null) {
                try {
                    distance = data.getDouble("distance");
                } catch (Exception e) {
                    distance = -1d;
                }
            }

            mDistance.setText((distance > 0 ? String.format("%.2f", distance / 1000.0) : "- - -") + getResources().getString(R.string.history_list_km));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
