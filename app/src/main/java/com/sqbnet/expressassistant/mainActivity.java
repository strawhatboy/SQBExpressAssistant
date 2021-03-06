package com.sqbnet.expressassistant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sqbnet.expressassistant.Location.BaiDuLocationService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private LinearLayout ly_main_locate_failed;
    private Animation in_from_bottom;
    private Animation out_from_top;

    private TextView tv_rob_order;
    private TextView tv_history_order;
    private TextView tv_my_wallet;
    private TextView tv_main_hint;

    //private boolean isHistoryDetailsVisible = false;
    private boolean isWaiting = false;
    private boolean isVisible = false;

    private Timer timer;
    private Map<Timer, Boolean> cancelTimerMap;
    private int cancelCounter = 0;
    private boolean isCancelled = false;

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

        XGPushConfig.enableDebug(this, false);

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
                        Log.d("mainActivity", "handle XG Message !!!");
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("mainActivity", "FromNotification - THIS IS IN UI THREAD!!!");
                                        Intent orderIntent = new Intent();
                                        orderIntent.setClass(mainActivity.this, orderMainActivity.class);
                                        orderIntent.putExtra("user_id", user_id);
                                        orderIntent.putExtra("order_id", order_id);
                                        orderIntent.putExtra("status", status);
                                        setStatus(false);
                                        startActivityForResult(orderIntent, RequestCode.ORDER);
                                    }
                                });
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
        BaiDuLocationService.getInst().unregisterLocationListener("MAIN_ACTIVITY");
        
        // for unhandle exitting when searching for orders.
        setUserStatus(false);
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

        ly_main_locate_failed = (LinearLayout) findViewById(R.id.ly_main_locate_failed);
        in_from_bottom = AnimationUtils.loadAnimation(mainActivity.this, R.anim.in_from_bottom);
        out_from_top = AnimationUtils.loadAnimation(mainActivity.this, R.anim.out_from_top);
        in_from_bottom.setFillAfter(true);
        out_from_top.setFillAfter(true);

        tv_main_hint = (TextView) findViewById(R.id.tv_main_hint);

        cancelTimerMap = new HashMap<Timer, Boolean>();

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
                    if (UtilHelper.getIsLocateSuccess() == false) {
                        showHint(R.string.searching_locate_failed);
                        return;
                    }
                    setStatus(!isWaiting);
                }

                if(!isWaiting) {
                    mTabBtnRobOrder.setBackgroundDrawable(getResources().getDrawable(R.color.bg_blue));
                    tv_rob_order.setTextColor(getResources().getColorStateList(R.color.font_white));
                    GPSLocation.isSendingLocation = false;
                }else{
                    GPSLocation.isSendingLocation = true;
                }
                mTabBtnHistoryOrder.setBackgroundDrawable(getResources().getDrawable(R.color.bg_gray));
                tv_history_order.setTextColor(getResources().getColorStateList(R.color.font_black));
                mTabBtnMyWallet.setBackgroundDrawable(getResources().getDrawable(R.color.bg_gray));
                tv_my_wallet.setTextColor(getResources().getColorStateList(R.color.font_black));
                break;
            case R.id.id_tab_btn_history_order:
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
        if (b_status) {
            BaiDuLocationService.getInst().registerLocationListener("MAIN_ACTIVITY", true, new BaiDuLocationService.ILocateCallback() {
                @Override
                public void handleLocationGot(double latitude, double longitude) {
                    setUserStatus(b_status, latitude, longitude);
                }
            });
        } else {
            isCancelled = true;
            MyLocation lastLocation = BaiDuLocationService.getInst().getLocationGotFromBaidu();
            if (lastLocation != null) {
                setUserStatus(b_status, lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                setUserStatus(b_status, 0d, 0d);
            }
        }
    }
    
    private void setUserStatus(final boolean b_status, double latitude, double longitude) {
        final String user_id = UtilHelper.getSharedUserId();
        String status = "0";
        if (b_status) {
            status = "1";
        }
        Log.i("setUserStatus", "status:" + status);
        SQBProvider.getInst().updateUserStatus(user_id, status, String.valueOf(latitude), String.valueOf(longitude), new SQBResponseListener() {
            @Override
            public void onResponse(SQBResponse response) {
                if (response == null)
                    return;
                Log.i("setUserStatus", "updateUserStatus");
                Log.i("setUserStatus", response.getCode());
                Log.i("setUserStatus", response.getMsg());
                Log.i("setUserStatus", response.getData().toString());
                if (b_status && isWaiting) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("setUserStatus", "start to get order when b_status = " + b_status + ", isWaiting = " + isWaiting);
                            getAssignOrder();
                        }
                    });
                } else {
                    Log.i("setUserStatus", "won't get order when b_status = " + b_status + ", isWaiting = " + isWaiting);
                }
            }
        });
    }

    /**
     * switch the status between waiting & rest
     */
    private void setStatus(boolean status) {
        if(isWaiting != status) {
            setUserStatus(status);
        }
            isWaiting = status;
            Log.d("Status Change :", Boolean.toString(isWaiting));
            if (isWaiting) {
                Log.d("mainActivity", "start location service");
                Intent service = new Intent(MyApplication.getInst().getApplicationContext(), LocalService.class);
                MyApplication.getInst().getApplicationContext().startService(service);
                mTabBtnRobOrder.setBackgroundDrawable(resources.getDrawable(R.color.button_green));
                tv_rob_order.setText(resources.getString(R.string.tab_btn_rest));
                setBackgroudLight();
                //TODO: start to wait for order from server
                //getAssignOrder();

                //Got Order !! for debug
                //Intent intent = new Intent();
                //intent.setClass(mainActivity.this, orderMainActivity.class);

                //startActivityForResult(intent, RequestCode.ORDER);

            } else {
                Log.d("mainActivity", "stop location service");
                getApplicationContext().stopService(new Intent(getApplicationContext(), LocalService.class));
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

    private void getAssignOrder() {
        getAssignOrder(false, null);
    }

    private void getAssignOrder(final boolean ignoreStatus, final IGetAssignedOrderFailedCallback failedCallback){
        final String user_id = UtilHelper.getSharedUserId();

        SQBProvider.getInst().getAssignOrder(user_id, new SQBResponseListener() {
            @Override
            public void onResponse(SQBResponse response) {
                if (response == null)
                    return;

                if (ignoreStatus || isWaiting) {
                    Log.i("virgil", "getAssignOrder");
                    Log.i("virgil", response.getCode());
                    Log.i("virgil", response.getMsg());
                    Log.i("virgil", response.getData().toString());
                    if (isCancelled) {
                        isCancelled = false;
                        Log.d("cancelCounter", cancelCounter + ", will +1");
                        cancelCounter++;
                    }
                    try {
                        if (response.getCode().equals("1000")) {
                            JSONObject result = (JSONObject) response.getData();
                            try {
                                final String order_id = result.getString("order_id");
                                final String status = result.getString("d_status");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("main", "getAssignOrder !!!");
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

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (failedCallback != null) {
                                failedCallback.failed();
                            }
                            Thread.sleep(10000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isWaiting && cancelCounter <= 0) {
                                        //getAssignOrder();
                                        Log.d("mainActivity", "won't get order anymore if failed the first time");
                                    } else {
                                        Log.d("cancelCounter", cancelCounter + ", will -1");
                                        cancelCounter--;
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e("getAssignOrder", "got error: " + e.getMessage());
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
                    default:
                        Log.e("mainActivity", "UNEXPECTED resultcode got in requestCode.ORDER");
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

    public void showHint(@StringRes int resId) {
        showHint(resources.getString(resId));
    }

    public void showHint(@Nullable CharSequence text) {
        ly_main_locate_failed.setVisibility(View.VISIBLE);
        tv_main_hint.setText(text);
        ly_main_locate_failed.clearAnimation();
        ly_main_locate_failed.startAnimation(in_from_bottom);

        if (timer != null) {
            timer.cancel();
            if (cancelTimerMap.containsKey(timer)) {
                cancelTimerMap.put(timer, true);
            }
        }

        timer = new Timer();
        cancelTimerMap.put(timer, false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!cancelTimerMap.get(timer)) {
                            ly_main_locate_failed.setVisibility(View.VISIBLE);
                            ly_main_locate_failed.clearAnimation();
                            ly_main_locate_failed.startAnimation(out_from_top);
                        }
                        cancelTimerMap.remove(timer);
                        timer = null;
                    }
                });
            }
        }, 5000);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        final String from = intent.getStringExtra("from");
        final String user_id = intent.getStringExtra("user_id");
        final String order_id = intent.getStringExtra("order_id");
        final String status = intent.getStringExtra("status");
        Log.d("mainActivity", "onNewIntent called !!!" + from);
        super.onNewIntent(intent);
        setIntent(intent);
        if (from.equals("XG")) {
            Log.d("mainActivity", "handling order from NOTIFICATION !!!" + from);
            SQBProvider.getInst().getOrderInfo(order_id, new SQBResponseListener() {
                @Override
                public void onResponse(SQBResponse response) {
                    Log.i("virgil", "order confirm");
                    Log.i("virgil", response.getCode());
                    Log.i("virgil", response.getMsg());
                    Log.i("virgil", response.getData().toString());

                    if (response.getCode().equals("1000")) {
                        JSONObject result = (JSONObject) response.getData();
                        try {

                            String id = result.getString("d_id");
                            if (!id.equals(UtilHelper.getSharedUserId())) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(mainActivity.this).setTitle(getApplicationContext().getResources().getString(R.string.dialog_title_info))
                                                .setMessage("接单超时，该单已指派给他人")
                                                        /*.setPositiveButton("退出接单", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                finish();
                                                            }
                                                        })*/
                                                .show();

                                    }
                                });
                                return;
                            }
                            getAssignOrder(true, new IGetAssignedOrderFailedCallback() {
                                @Override
                                public void failed() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AlertDialog.Builder(mainActivity.this).setTitle(getApplicationContext().getResources().getString(R.string.dialog_title_info))
                                                    .setMessage("没有指派的订单，或该订单已指派给他人")
                                                            /*.setPositiveButton("退出接单", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    finish();
                                                                }
                                                            })*/
                                                    .show();

                                        }
                                    });
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public interface IGetAssignedOrderFailedCallback {
        void failed();
    }
}
