package com.sqbnet.expressassistant;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see
 */
public class mainActivity extends FragmentActivity implements View.OnClickListener{

    private ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    private LinearLayout mTabBtnRobOrder;
    private LinearLayout mTabBtnHistoryOrder;
    private LinearLayout mTabBtnMyWallet;
    private LinearLayout mainLayout;

    private TextView tv_rob_order;

    private boolean isWaiting = false;

    private Resources resources;

    TabRobOrder tabRobOrder;
    TabHistoryOrder tabHistoryOrder;
    TabMyWallet tabMyWallet;
    historyDetailsFragment tabHistoryDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager)this.findViewById(R.id.id_viewpager);

        checkLoginStatus();

        initView();

        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
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
    }

    private void initView(){
        mTabBtnRobOrder = (LinearLayout)findViewById(R.id.id_tab_btn_rob_order);
        mTabBtnHistoryOrder = (LinearLayout)findViewById(R.id.id_tab_btn_history_order);
        mTabBtnMyWallet = (LinearLayout)findViewById(R.id.id_tab_btn_my_wallet);
        mainLayout = (LinearLayout)findViewById(R.id.ly_main);

        tv_rob_order = (TextView) findViewById(R.id.tv_rob_order);

        tabRobOrder = new TabRobOrder();
        tabHistoryOrder = new TabHistoryOrder();
        tabMyWallet = new TabMyWallet();
        tabHistoryDetails = new historyDetailsFragment();

        tabHistoryOrder.gotoDetails = tabHistoryDetails.gotoDetails = new TabHistoryOrder.IGotoDetails() {
            @Override
            public void gotoDetails(JSONObject data) {
                mViewPager.setCurrentItem(2);
                tabHistoryDetails.setData(data);
                setBackgroudLight();
            }

            @Override
            public void back() {
                mViewPager.setCurrentItem(1);
                setBackgroudLight();
            }
        };

        mFragments.add(tabRobOrder);
        mFragments.add(tabHistoryOrder);
        mFragments.add(tabHistoryDetails);
        mFragments.add(tabMyWallet);

        mTabBtnRobOrder.setOnClickListener(this);
        mTabBtnHistoryOrder.setOnClickListener(this);
        mTabBtnMyWallet.setOnClickListener(this);

        resources = getResources();
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
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
                break;
            case R.id.id_tab_btn_history_order:
                mViewPager.setCurrentItem(1);
                setBackgroudLight();
                break;
            case R.id.id_tab_btn_my_wallet:
                mViewPager.setCurrentItem(3);
                setBackgroudLight();
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
            mTabBtnRobOrder.setBackground(resources.getDrawable(R.color.button_green));
            tv_rob_order.setText(resources.getString(R.string.tab_btn_rest));
            setBackgroudLight();
            //TODO: start to wait for order from server

        } else {
            mTabBtnRobOrder.setBackground(resources.getDrawable(R.color.button_blue));
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
        mainLayout.setBackground(resources.getDrawable(isLight ? R.drawable.bg : R.drawable.bg2));
    }


    /**
     * handler for the login
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.LOGIN) {
            if (resultCode == ResultCode.LOGIN_SUCCESS) {

            }
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
        if (mViewPager.getCurrentItem() == 2) {
            mViewPager.setCurrentItem(1);
            setBackgroudLight();
        }
    }
}
