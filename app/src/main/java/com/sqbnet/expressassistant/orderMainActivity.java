package com.sqbnet.expressassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sqbnet.expressassistant.controls.UnScrollableViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy on 6/29/2015.
 */
public class orderMainActivity extends FragmentActivity {

    private UnScrollableViewPager viewPager;
    private orderGotFragment _orderGotFragment;
    private orderConfirmFragment _orderConfirmFragment;
    private orderTakeDeliveryFragment _orderTakeDeliveryFragment;
    private orderDeliverFragment _orderDeliverFragment;
    private orderFinishFragment _orderFinishFragment;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<Fragment> fragments = new ArrayList<Fragment>();

    private LinearLayout ly_processbar;
    private TextView tv_take;
    private TextView tv_confirm;
    private TextView tv_deliver;
    private TextView tv_finish;
    private LinearLayout ly_bar_take;
    private LinearLayout ly_bar_confirm;
    private LinearLayout ly_bar_deliver;
    private LinearLayout ly_bar_finish;
    private LinearLayout ly_circle_take;
    private LinearLayout ly_circle_confirm;
    private LinearLayout ly_circle_deliver;
    private LinearLayout ly_circle_finish;

    private LinearLayout mainLayout;

    private List<List<View>> processbarElements = new ArrayList<List<View>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main);

        initView();
    }

    private void initView() {
        initFragments();
        initProcessbar();
        mainLayout = (LinearLayout) findViewById(R.id.ly_order_main);
    }

    private void initFragments() {
        viewPager = (UnScrollableViewPager) findViewById(R.id.id_order_viewpager);

        IWizardPageDelegate iWizardPageDelegate = new IWizardPageDelegate() {
            @Override
            public void goNext() {
                int currentPage = viewPager.getCurrentItem();
                Log.d("orderMain", "current page: " + currentPage + " fragments size: " + fragments.size());
                // move the processbar
                if (currentPage <= 2)
                    setProgressBarIndex(currentPage, true);
                // move to next fragment
                if (currentPage < fragments.size() - 1) {
                    viewPager.setCurrentItem(currentPage + 1);
                }

                if (currentPage == 1 || currentPage == 2) {
                    setBackgroudLight();
                } else {
                    setBackgroudDark();
                }
            }

            @Override
            public void exit(int exitCode, String data) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("data", data);
                intent.putExtras(bundle);
                setResult(exitCode, intent);
                finish();
            }
        };

        _orderGotFragment = new orderGotFragment();
        _orderGotFragment.setNextDelegate(iWizardPageDelegate);
        _orderConfirmFragment = new orderConfirmFragment();
        _orderConfirmFragment.setNextDelegate(iWizardPageDelegate);
        _orderTakeDeliveryFragment = new orderTakeDeliveryFragment();
        _orderTakeDeliveryFragment.setNextDelegate(iWizardPageDelegate);
        _orderDeliverFragment = new orderDeliverFragment();
        _orderDeliverFragment.setNextDelegate(iWizardPageDelegate);
        _orderFinishFragment = new orderFinishFragment();
        _orderFinishFragment.setNextDelegate(iWizardPageDelegate);

        fragments.add(_orderGotFragment);
        fragments.add(_orderConfirmFragment);
        fragments.add(_orderTakeDeliveryFragment);
        fragments.add(_orderDeliverFragment);
        fragments.add(_orderFinishFragment);

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
    }

    private void initProcessbar() {
        ly_processbar = (LinearLayout) findViewById(R.id.ly_order_main_processbar);

        tv_take = (TextView) findViewById(R.id.tv_order_pb_take);
        tv_confirm = (TextView) findViewById(R.id.tv_order_pb_confirm);
        tv_deliver = (TextView) findViewById(R.id.tv_order_pb_deliver);
        tv_finish = (TextView) findViewById(R.id.tv_order_pb_finish);
        ly_bar_take = (LinearLayout) findViewById(R.id.ly_order_pb_bar_take);
        ly_bar_confirm = (LinearLayout) findViewById(R.id.ly_order_pb_bar_confirm);
        ly_bar_deliver = (LinearLayout) findViewById(R.id.ly_order_pb_bar_deliver);
        ly_bar_finish = (LinearLayout) findViewById(R.id.ly_order_pb_bar_finish);
        ly_circle_take = (LinearLayout) findViewById(R.id.ly_order_pb_circle_take);
        ly_circle_confirm = (LinearLayout) findViewById(R.id.ly_order_pb_circle_confirm);
        ly_circle_deliver = (LinearLayout) findViewById(R.id.ly_order_pb_circle_deliver);
        ly_circle_finish = (LinearLayout) findViewById(R.id.ly_order_pb_circle_finish);

        List<View> _1st = new ArrayList<View>();
        _1st.add(tv_confirm);
        _1st.add(ly_bar_confirm);
        _1st.add(ly_circle_confirm);
        List<View> _2nd = new ArrayList<View>();
        _2nd.add(tv_take);
        _2nd.add(ly_bar_take);
        _2nd.add(ly_circle_take);
        List<View> _3rd = new ArrayList<View>();
        _3rd.add(tv_deliver);
        _3rd.add(ly_bar_deliver);
        _3rd.add(ly_circle_deliver);
        List<View> _4th = new ArrayList<View>();
        _4th.add(tv_finish);
        _4th.add(ly_bar_finish);
        _4th.add(ly_circle_finish);
        processbarElements.add(_1st);
        processbarElements.add(_2nd);
        processbarElements.add(_3rd);
        processbarElements.add(_4th);
    }

    private void setProgressBarIndex(int index, boolean isEnabled) {
        if (index >= 0) {
            ly_processbar.setVisibility(View.VISIBLE);
            List<View> views = processbarElements.get(index);
            int colorId = isEnabled ? R.color.font_green : R.color.font_grey;
            ((TextView) views.get(0)).setTextColor(getResources().getColorStateList(colorId));
            views.get(1).setBackground(getResources().getDrawable(colorId));
            views.get(2).setBackground(getResources().getDrawable(isEnabled ? R.drawable.green_circle : R.drawable.grey_circle));

        } else {
            ly_processbar.setVisibility(View.INVISIBLE);
        }
    }

    public interface IWizardPageDelegate {
        void goNext();
        void exit(int exitCode, String data);
    }

    public interface IWizardPage {
        void setNextDelegate(IWizardPageDelegate delegate);
    }

    private void setBackgroudLight() {
        setBackground(true);
    }

    private void setBackgroudDark() {
        setBackground(false);
    }

    private void setBackground(boolean isLight) {
        mainLayout.setBackground(getResources().getDrawable(isLight ? R.drawable.bg : R.drawable.bg2));
    }
}
