package com.sqbnet.expressassistant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sqbnet.expressassistant.Location.GPSLocation;
import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.MyLocation;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.service.LocalService;
import com.sqbnet.expressassistant.utils.CustomConstants;
import com.sqbnet.expressassistant.utils.UtilHelper;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see
 */
public class mainActivity extends BaseFragmentActivity implements View.OnClickListener{

    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mFragmentPagerAdapter;
    private FragmentViewPagerAdapter mFragmentViewPagerAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    private LinearLayout mTabBtnRobOrder;
    private LinearLayout mTabBtnHistoryOrder;
    private LinearLayout mTabBtnMyWallet;
    private LinearLayout mainLayout;

    private TextView tv_rob_order;
    private TextView tv_history_order;
    private TextView tv_my_wallet;

    //private boolean isHistoryDetailsVisible = false;
    private boolean isWaiting = false;
    private boolean isVisible = false;

    private Resources resources;

    TabRobOrder tabRobOrder;
    TabHistoryOrder tabHistoryOrder;
    TabMyWallet tabMyWallet;
    //historyDetailsActivity tabHistoryDetails;
    orderDoneFragment tabOrderDoneFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager)this.findViewById(R.id.id_viewpager);

        initView();

        mFragmentViewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, mFragments);

        GPSLocation.getInst().GPSProviderStatusChanged = new GPSLocation.GPSProviderStatusChanged(){
            @Override
            public void onStatusChanged(boolean isEnabled) {
                if(!isEnabled){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UtilHelper.checkGPSLocation(mainActivity.this);
                        }
                    });
                }
            }
        };

        XGPushConfig.enableDebug(this, true);

        XGPushManager.registerPush(getApplicationContext(), new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                Log.i("virgil", "XG register success");
            }

            @Override
            public void onFail(Object o, int i, String s) {
                Log.i("virgil", "XG register fail," + s);
            }
        });

        UtilHelper.iHandleXGMessage = new UtilHelper.IHandleXGMessage() {
            @Override
            public void getMessage(final String user_id, final String order_id, final String status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!isVisible || !isWaiting)
                            return;
                        Intent intent = new Intent();
                        intent.setClass(mainActivity.this, orderMainActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("order_id", order_id);
                        intent.putExtra("status", status);
                        intent.putExtra("from", "main");
                        startActivityForResult(intent, RequestCode.ORDER);
                        setStatus(false);
                    }
                });
            }
        };

        Context context = getApplicationContext();
        Intent service = new Intent(context, XGPushService.class);
        context.startService(service);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        Log.i("--virgil", "mainActivity onResume");

        super.onResume();
        isVisible = true;



        if(checkLoginStatus()){
            Intent intent = getIntent();
            if(!intent.hasExtra(CustomConstants.INTENT_FROM))
                return;
            Log.i("virgil", "from:" + intent.getStringExtra(CustomConstants.INTENT_FROM));
            Log.i("virgil", "compare:" + CustomConstants.FROM_NOTIFICATION);
            if(intent.getStringExtra(CustomConstants.INTENT_FROM).equals(CustomConstants.FROM_NOTIFICATION)){
                Log.i("virgil","in notification");
                String action = intent.getStringExtra(CustomConstants.INTENT_ACTION);
                String content = intent.getStringExtra(CustomConstants.INTENT_CONTENT);

                if(action.equals(SQBProvider.URL_GET_ASSIGN_ORDER)) {
                    try {
                        Log.i("virgil", "in getAssignOrder");
                        JSONObject jsonObject = new JSONObject(content);
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        final String user_id = UtilHelper.getSharedUserId();
                        final String order_id = dataObj.getString("order_id");
                        final String status = dataObj.getString("status");

                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                Intent orderIntent = new Intent();
                                orderIntent.setClass(mainActivity.this, orderMainActivity.class);
                                orderIntent.putExtra("user_id", user_id);
                                orderIntent.putExtra("order_id", order_id);
                                orderIntent.putExtra("status", status);
                                startActivityForResult(orderIntent, RequestCode.ORDER);
                                setStatus(false);
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(timerTask, 3000);

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("virgil", "on destroy main activity");
        //GPSLocation.getInst().stop();

        //XGPushManager.registerPush(getApplicationContext(), "*");
        getApplicationContext().stopService(new Intent(getApplicationContext(), LocalService.class));
        XGPushManager.unregisterPush(this);
        SQBProvider.getInst().logout(UtilHelper.getSharedUserId(), new SQBResponseListener() {
            @Override
            public void onResponse(SQBResponse response) {
                if (response == null)
                    return;
                Log.i("virgil", "logout");
                Log.i("virgil", response.getCode());
                Log.i("virgil", response.getMsg());
                Log.i("virgil", response.getData().toString());
            }
        });
        UtilHelper.setSharedUserId(null);
        super.onDestroy();

    }

    private void initView(){
        mTabBtnRobOrder = (LinearLayout)findViewById(R.id.id_tab_btn_rob_order);
        mTabBtnHistoryOrder = (LinearLayout)findViewById(R.id.id_tab_btn_history_order);
        mTabBtnMyWallet = (LinearLayout)findViewById(R.id.id_tab_btn_my_wallet);
        mainLayout = (LinearLayout)findViewById(R.id.ly_main);

        tv_rob_order = (TextView) findViewById(R.id.tv_rob_order);
        tv_history_order = (TextView) findViewById(R.id.tv_history_order);
        tv_my_wallet = (TextView) findViewById(R.id.tv_my_wallet);

        tabRobOrder = new TabRobOrder();
        tabHistoryOrder = new TabHistoryOrder();
        tabMyWallet = new TabMyWallet();
        tabOrderDoneFragment = new orderDoneFragment();

        tabHistoryOrder.gotoDetails = new TabHistoryOrder.IGotoDetails() {
            @Override
            public void gotoDetails(JSONObject data) {

                Intent intent = new Intent();
                intent.putExtra("details", data.toString());

                intent.setClass(mainActivity.this, historyDetailsActivity.class);
                startActivity(intent);
            }

            @Override
            public void back() {
                /*isHistoryDetailsVisible = false;
                mViewPager.setCurrentItem(1);
                mFragments.remove(2);
                mFragmentPagerAdapter.notifyDataSetChanged();
                setBackgroudLight();*/
            }
        };

        tabOrderDoneFragment.IGetOrder = new orderDoneFragment.IGetOrder(){

            @Override
            public void run() {
                mViewPager.setCurrentItem(0);
                setStatus(true);
            }
        };

        mFragments.add(tabRobOrder);
        mFragments.add(tabHistoryOrder);
        //mFragments.add(tabHistoryDetails);
        mFragments.add(tabMyWallet);
        mFragments.add(tabOrderDoneFragment);

        mTabBtnRobOrder.setOnClickListener(this);
        mTabBtnHistoryOrder.setOnClickListener(this);
        mTabBtnMyWallet.setOnClickListener(this);

        resources = getResources();
    }

    private boolean checkLoginStatus() {
        String token = UtilHelper.getSharedUserId();
        if (token == null) {
            Intent intent = new Intent();
            intent.setClass(mainActivity.this, loginActivity.class);
            startActivityForResult(intent, RequestCode.LOGIN);
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_tab_btn_rob_order:
                if (mViewPager.getCurrentItem() != 0) {
                    mViewPager.setCurrentItem(0);

                    // need to reset the fragment's status because some wired issue.
                    setStatus(isWaiting);
                }
                else {
                    setStatus(!isWaiting);
                }

                /*if (isHistoryDetailsVisible) {
                    isHistoryDetailsVisible = false;
                    mFragments.remove(2);
                    mFragmentPagerAdapter.notifyDataSetChanged();
                }*/
                if(!isWaiting) {
                    mTabBtnRobOrder.setBackgroundDrawable(getResources().getDrawable(R.color.bg_blue));
                    tv_rob_order.setTextColor(getResources().getColorStateList(R.color.font_white));
                    //getApplicationContext().stopService(new Intent(getApplicationContext(), LocalService.class));
                    GPSLocation.isSendingLocation = false;
                }else{
                  /*  Context context = getApplicationContext();
                    Intent service = new Intent(context, XGPushService.class);
                    context.startService(service);*/
                    GPSLocation.isSendingLocation = true;
                }
                mTabBtnHistoryOrder.setBackgroundDrawable(getResources().getDrawable(R.color.bg_gray));
                tv_history_order.setTextColor(getResources().getColorStateList(R.color.font_black));
                mTabBtnMyWallet.setBackgroundDrawable(getResources().getDrawable(R.color.bg_gray));
                tv_my_wallet.setTextColor(getResources().getColorStateList(R.color.font_black));
                break;
            case R.id.id_tab_btn_history_order:

                /*if (isHistoryDetailsVisible) {
                    isHistoryDetailsVisible = false;
                    mFragments.remove(2);
                    mFragmentPagerAdapter.notifyDataSetChanged();
                }*/
                mViewPager.setCurrentItem(1);
                setBackgroudLight();
                if(!isWaiting) {
                    mTabBtnRobOrder.setBackgroundDrawable(getResources().getDrawable(R.color.bg_gray));
                    tv_rob_order.setTextColor(getResources().getColorStateList(R.color.font_black));
                }
                mTabBtnHistoryOrder.setBackgroundDrawable(getResources().getDrawable(R.color.bg_blue));
                tv_history_order.setTextColor(getResources().getColorStateList(R.color.font_white));
                mTabBtnMyWallet.setBackgroundDrawable(getResources().getDrawable(R.color.bg_gray));
                tv_my_wallet.setTextColor(getResources().getColorStateList(R.color.font_black));
                break;
            case R.id.id_tab_btn_my_wallet:

                /*if (isHistoryDetailsVisible) {
                    isHistoryDetailsVisible = false;
                    mFragments.remove(2);
                    mFragmentPagerAdapter.notifyDataSetChanged();
                }*/
                mViewPager.setCurrentItem(2);

                setBackgroudLight();
                if(!isWaiting) {
                    mTabBtnRobOrder.setBackgroundDrawable(getResources().getDrawable(R.color.bg_gray));
                    tv_rob_order.setTextColor(getResources().getColorStateList(R.color.font_black));
                }
                mTabBtnHistoryOrder.setBackgroundDrawable(getResources().getDrawable(R.color.bg_gray));
                tv_history_order.setTextColor(getResources().getColorStateList(R.color.font_black));
                mTabBtnMyWallet.setBackgroundDrawable(getResources().getDrawable(R.color.bg_blue));
                tv_my_wallet.setTextColor(getResources().getColorStateList(R.color.font_white));
                break;
        }
    }

    private void setUserStatus(final boolean b_status){
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String user_id = UtilHelper.getSharedUserId();
                MyLocation location = GPSLocation.getInst().getCurrentLocation();
                String status = "0";
                if (b_status) {
                    status = "1";
                }
                Log.i("virgil", "status:" + status);
                if (location != null) {
                    SQBProvider.getInst().updateUserStatus(user_id, status, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), new SQBResponseListener() {
                        @Override
                        public void onResponse(SQBResponse response) {
                            if (response == null)
                                return;
                            Log.i("virgil", "updateUserStatus");
                            Log.i("virgil", response.getCode());
                            Log.i("virgil", response.getMsg());
                            Log.i("virgil", response.getData().toString());
                        }
                    });
                }
                return null;
            }
        };
        task.execute();
    }

    /**
     * switch the status between waiting & rest
     */
    private void setStatus(boolean status) {
        isWaiting = status;
        setUserStatus(status);
        Log.d("Status Change :", Boolean.toString(isWaiting));
        if (isWaiting) {
            mTabBtnRobOrder.setBackgroundDrawable(resources.getDrawable(R.color.button_green));
            tv_rob_order.setText(resources.getString(R.string.tab_btn_rest));
            setBackgroudLight();
            //TODO: start to wait for order from server
            getAssignOrder();

            //Got Order !! for debug
            //Intent intent = new Intent();
            //intent.setClass(mainActivity.this, orderMainActivity.class);

            //startActivityForResult(intent, RequestCode.ORDER);

        } else {
            mTabBtnRobOrder.setBackgroundDrawable(resources.getDrawable(R.color.button_blue));
            tv_rob_order.setText(resources.getString(R.string.tab_btn_rob_order));
            setBackgroudDark();
            //TODO: stop the listening
        }
        tabRobOrder.setIsWaiting(isWaiting);
    }

    private void setBackgroudLight() {
        setBackground(true);
    }

    private void setBackgroudDark() {
        setBackground(false);
    }

    private void setBackground(boolean isLight) {
        mainLayout.setBackgroundDrawable(resources.getDrawable(isLight ? R.drawable.bg : R.drawable.bg2));
    }

    private void getAssignOrder(){
        final String user_id = UtilHelper.getSharedUserId();

        SQBProvider.getInst().getAssignOrder(user_id, new SQBResponseListener() {
                    @Override
                    public void onResponse(SQBResponse response) {
                        if(response == null)
                            return;
                        Log.i("virgil", "getAssignOrder");
                        Log.i("virgil", response.getCode());
                        Log.i("virgil", response.getMsg());
                        Log.i("virgil", response.getData().toString());

/*                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setClass(mainActivity.this, orderMainActivity.class);
                                intent.putExtra("user_id", user_id);
                                intent.putExtra("order_id", "921");
                                intent.putExtra("status", "0");
                                intent.putExtra("from", "main");
                                startActivityForResult(intent, RequestCode.ORDER);
                                setStatus(false);
                            }
                        });*/

                        if(response.getCode().equals("1000")){
                            JSONObject result = (JSONObject)response.getData();
                            try {
                                final String order_id = result.getString("order_id");
                                final String status = result.getString("d_status");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent();
                                        intent.setClass(mainActivity.this, orderMainActivity.class);
                                        intent.putExtra("user_id", user_id);
                                        intent.putExtra("order_id", order_id);
                                        intent.putExtra("status", status);
                                        intent.putExtra("from", "main");
                                        startActivityForResult(intent, RequestCode.ORDER);
                                        setStatus(false);
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
        });
    }
    /**
     * handler for the login
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("mainActivityResult", "Got activity Result: " + requestCode + "|" + resultCode);
        switch (requestCode) {
            case RequestCode.LOGIN: {
                switch (resultCode){
                    case ResultCode.LOGIN_SUCCESS:
                        //startGPSLocation();
                        break;
                    case ResultCode.QUIT:
                        finish();
                        break;
                }
            }
            break;
            case RequestCode.ORDER: {
                switch (resultCode) {
                    case ResultCode.ORDER_DONE:
                        Bundle bundle = data.getExtras();
                        String remuneration = bundle.getString("data");
                        orderDoneFragment.Remuneration = remuneration;
                        mViewPager.setCurrentItem(3);
                        setBackgroudDark();
                        break;
                    case ResultCode.ORDER_CANCELED:
                        Log.i("virgil", "order_cancel");
                        break;
                }
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
        new AlertDialog.Builder(mainActivity.this).setTitle(resources.getString(R.string.dialog_title_info))
                .setMessage(resources.getString(R.string.dialog_confirm_exit))
                .setPositiveButton(resources.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(resources.getString(R.string.dialog_no), null)
                .show();
    }
}
