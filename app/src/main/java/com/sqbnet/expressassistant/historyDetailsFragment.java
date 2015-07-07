package com.sqbnet.expressassistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
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
public class historyDetailsFragment extends BaseFragment {
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

    private ImageView iv_Company;
    private ImageView iv_Consignee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_details_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mCompanyName = (TextView) view.findViewById(R.id.tv_history_details_company_name);
        mCompanyAddress = (TextView) view.findViewById(R.id.tv_history_details_company_address);
        mCompanyPhone = (TextView) view.findViewById(R.id.tv_history_details_company_phone);
        mGoodsCount = (TextView) view.findViewById(R.id.tv_history_details_list_good_count);
        mDistance = (TextView) view.findViewById(R.id.tv_history_details_distance);
        mStartTime = (TextView) view.findViewById(R.id.tv_history_details_order_got_time);
        mPickTime = (TextView) view.findViewById(R.id.tv_history_details_delivery_taken_time);
        mEndTime = (TextView) view.findViewById(R.id.tv_history_details_delivered_time);
        mRemuneration = (TextView) view.findViewById(R.id.tv_history_details_remuneration);
        mTotalDuration = (TextView) view.findViewById(R.id.tv_history_details_total_duration);
        mRemuneration2 = (TextView) view.findViewById(R.id.tv_history_details_remuneration2);
        mConsigneeName = (TextView) view.findViewById(R.id.tv_history_details_consignee);
        mConsigneeAddress = (TextView) view.findViewById(R.id.tv_history_details_consignee_address);
        mConsigneePhone = (TextView) view.findViewById(R.id.tv_history_details_consignee_phone);
        iv_Company = (ImageView) view.findViewById(R.id.civ_history_details_from_avatar);
        iv_Consignee = (ImageView) view.findViewById(R.id.civ_history_details_to_avatar);


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

        mData = new ArrayList<Map<String, Object>>();
        // For debugging:
       /* for (int i = 0; i < 15; i++) {
            Map<String, Object> hisData0 = new HashMap<String, Object>();
            hisData0.put("good_name", "Rice");
            hisData0.put("good_count", "5.00kg");
            data.add(hisData0);
        }*/

        adapter = new SimpleAdapter(getActivity(), mData, R.layout.history_details_fragment_list, new String[] {
                "good_name",
                "good_count"
        }, new int[] {
                R.id.tv_history_details_list_good_name,
                R.id.tv_history_details_list_good_count
        });

        listView.setAdapter(adapter);

        View.OnClickListener phoneOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof TextView) {
                    final String phoneNumber = ((TextView) view).getText().toString();

                    new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.dialog_dial))
                            .setMessage(getResources().getString(R.string.intent_call) + " " + phoneNumber + " ?")
                            .setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.dialog_no), null)
                            .show();
                }
            }
        };
        mCompanyPhone.setOnClickListener(phoneOnClickListener);
        mConsigneePhone.setOnClickListener(phoneOnClickListener);
    }

    @Override
    protected void lazyload() {
        
    }

    public void setData(JSONObject data) {
        if(data == null){
            return;
        }

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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_Company.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
            }
            if (consigneeAvatar.startsWith("http")) {
                AsyncImageLoader.getInst().loadBitmap(consigneeAvatar, new AsyncImageLoader.ImageLoadResultLister() {
                    @Override
                    public void onImageLoadResult(final Bitmap bitmap) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_Consignee.setImageBitmap(bitmap);
                            }
                        });
                    }
                });

                if (data.getString("status").equals("0")) {
                    tv_done.setVisibility(View.INVISIBLE);
                }
            }

            long startTimestamp = data.getLong("starttime");
            long endTimestamp = data.getLong("endtime");
            mStartTime.setText(UtilHelper.getDateString(startTimestamp).split("\\s+")[1]);
            //mPickTime.setText(UtilHelper.getDateString(data.getLong("receivetime")).split("\\s+")[1]);
            mEndTime.setText(UtilHelper.getDateString(endTimestamp).split("\\s+")[1]);
            long durationTimestamp = endTimestamp - startTimestamp;
            Date durationDate = UtilHelper.getDate(durationTimestamp);
            mRemuneration.setText(data.getString("remuneration") + getResources().getString(R.string.unit_yuan));
            mRemuneration2.setText(data.getString("remuneration"));
            mTotalDuration.setText(Integer.toString(durationDate.getMinutes()));

            mConsigneeName.setText(orderInfo.getString("consignee"));
            mConsigneeAddress.setText(orderInfo.getString("address"));
            mConsigneePhone.setText(orderInfo.getString("mobile"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
