package com.sqbnet.expressassistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sqbnet.expressassistant.Location.BaiDuLocationService;
import com.sqbnet.expressassistant.Location.GPSLocation;
import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.UtilHelper;

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
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    private LinearLayout mTabBtnRobOrder;
    private LinearLayout mTabBtnHistoryOrder;
    private LinearLayout mTabBtnMyWallet;
    private LinearLayout mainLayout;

    private TextView tv_rob_order;
    private TextView tv_history_order;
    private TextView tv_my_wallet;

    private boolean isHistoryDetailsVisible = false;
    private boolean isWaiting = false;

    private Resources resources;

    private  Timer timer;

    TabRobOrder tabRobOrder;
    TabHistoryOrder tabHistoryOrder;
    TabMyWallet tabMyWallet;
    historyDetailsFragment tabHistoryDetails;
    orderDoneFragment tabOrderDoneFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager)this.findViewById(R.id.id_viewpager);

        initView();

        mFragmentPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public int getItemPosition(Object object) {
                return PagerAdapter.POSITION_NONE;
            }
        };

        mViewPager.setAdapter(mFragmentPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


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
    }

    @Override
    protected void onResume() {
        Log.i("--virgil", "onResume");

        super.onResume();
        checkLoginStatus();
    }

    @Override
    protected void onDestroy() {
        Log.i("virgil", "on destroy main activity");
        GPSLocation.getInst().stop();
        if(timer != null){
            timer.cancel();
        }
        SQBProvider.getInst().logout(UtilHelper.getSharedUserId(), new SQBResponseListener() {
            @Override
            public void onResponse(SQBResponse response) {
                if(response == null)
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
        tabHistoryDetails = new historyDetailsFragment();
        tabOrderDoneFragment = new orderDoneFragment();

        tabHistoryOrder.gotoDetails = tabHistoryDetails.gotoDetails = new TabHistoryOrder.IGotoDetails() {
            @Override
            public void gotoDetails(JSONObject data) {
                isHistoryDetailsVisible = true;
                mFragments.add(2, tabHistoryDetails);
                mFragmentPagerAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(2);
                tabHistoryDetails.setData(data);
                setBackgroudLight();
            }

            @Override
            public void back() {
                isHistoryDetailsVisible = false;
                mViewPager.setCurrentItem(1);
                mFragments.remove(2);
                mFragmentPagerAdapter.notifyDataSetChanged();
                setBackgroudLight();
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

    private void checkLoginStatus() {
        String token = UtilHelper.getSharedUserId();
        if (token == null) {
            Intent intent = new Intent();
            intent.setClass(mainActivity.this, loginActivity.class);
            startActivityForResult(intent, RequestCode.LOGIN);
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

                if (isHistoryDetailsVisible) {
                    isHistoryDetailsVisible = false;
                    mFragments.remove(2);
                    mFragmentPagerAdapter.notifyDataSetChanged();
                }
                if(!isWaiting) {
                    mTabBtnRobOrder.setBackgroundDrawable(getResources().getDrawable(R.color.bg_blue));
                    tv_rob_order.setTextColor(getResources().getColorStateList(R.color.font_white));
                }
                mTabBtnHistoryOrder.setBackgroundDrawable(getResources().getDrawable(R.color.bg_gray));
                tv_history_order.setTextColor(getResources().getColorStateList(R.color.font_black));
                mTabBtnMyWallet.setBackgroundDrawable(getResources().getDrawable(R.color.bg_gray));
                tv_my_wallet.setTextColor(getResources().getColorStateList(R.color.font_black));

                AsyncTask task = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        String user_id = UtilHelper.getSharedUserId();
                        Location location = GPSLocation.getInst().getCurrentLocation();
                        String status = "0";
                        if(isWaiting){
                            status = "1";
                        }
                        Log.i("virgil", "status:" + status);
                        SQBProvider.getInst().updateUserStatus(user_id, status, String.valueOf(location.getLongitude()), String.valueOf(location.getLongitude()), new SQBResponseListener() {
                            @Override
                            public void onResponse(SQBResponse response) {
                                if(response == null)
                                    return;
                                Log.i("virgil", "updateUserStatus");
                                Log.i("virgil", response.getCode());
                                Log.i("virgil", response.getMsg());
                                Log.i("virgil", response.getData().toString());
                            }
                        });
                        return null;
                    }
                };
                task.execute();
                break;
            case R.id.id_tab_btn_history_order:

                if (isHistoryDetailsVisible) {
                    isHistoryDetailsVisible = false;
                    mFragments.remove(2);
                    mFragmentPagerAdapter.notifyDataSetChanged();
                }
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

                if (isHistoryDetailsVisible) {
                    isHistoryDetailsVisible = false;
                    mFragments.remove(2);
                    mFragmentPagerAdapter.notifyDataSetChanged();
                }
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

    /**
     * switch the status between waiting & rest
     */
    private void setStatus(boolean status) {
        isWaiting = status;
        Log.d("Status Change :", Boolean.toString(isWaiting));
        if (isWaiting) {
            mTabBtnRobOrder.setBackgroundDrawable(resources.getDrawable(R.color.button_green));
            tv_rob_order.setText(resources.getString(R.string.tab_btn_rest));
            setBackgroudLight();
            //TODO: start to wait for order from server
            getAssignOrder();

            //Got Order !! for debug
           // Intent intent = new Intent();
            //intent.setClass(mainActivity.this, orderMainActivity.class);

            //startActivityForResult(intent, RequestCode.ORDER);

        } else {
            mTabBtnRobOrder.setBackgroundDrawable(resources.getDrawable(R.color.button_blue));
            tv_rob_order.setText(resources.getString(R.string.tab_btn_rob_order));
            setBackgroudDark();
            //TODO: stop the listening
            if(timer != null) {
                timer.cancel();
            }
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
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SQBProvider.getInst().getAssignOrder(user_id, new SQBResponseListener() {
                    @Override
                    public void onResponse(SQBResponse response) {
                        if(response == null)
                            return;
                        Log.i("virgil", "getAssignOrder");
                        Log.i("virgil", response.getCode());
                        Log.i("virgil", response.getMsg());
                        Log.i("virgil", response.getData().toString());

                       /* runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setClass(mainActivity.this, orderMainActivity.class);
                                intent.putExtra("user_id", user_id);
                                intent.putExtra("order_id", "940");
                                intent.putExtra("status", "0");
                                startActivityForResult(intent, RequestCode.ORDER);
                                setStatus(false);
                            }
                        });*/

                        if(response.getCode().equals("1000")){
                            timer.cancel();
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
        }, 1000, 30*1000);
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
                        mViewPager.setCurrentItem(4);
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
        if (mViewPager.getCurrentItem() == 2) {
            mViewPager.setCurrentItem(1);
            setBackgroudLight();
        }else{
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
}
