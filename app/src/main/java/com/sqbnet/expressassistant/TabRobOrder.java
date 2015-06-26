package com.sqbnet.expressassistant;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.sqbnet.expressassistant.controls.CircleImageView;



/**
 * A simple {@link Fragment} subclass.
 */
public class TabRobOrder extends Fragment {
    private boolean isWaiting;

    private CircleImageView civ_avatar;
    private TextView tv_waiting;
    private TextView tv_hint;
    private TextView tv_hint_content;

    private ImageView iv_radar;
    private Animation animation;

    public TabRobOrder() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_rob_order, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        civ_avatar = (CircleImageView) view.findViewById(R.id.civ_searching_avatar);
        tv_waiting = (TextView) view.findViewById(R.id.tv_searching_waiting);
        tv_hint = (TextView) view.findViewById(R.id.tv_searching_hint);
        tv_hint_content = (TextView) view.findViewById(R.id.tv_searching_hint_content);
        iv_radar = (ImageView) view.findViewById(R.id.iv_searching_radar);


        animation = AnimationUtils.loadAnimation(this.getActivity(), R.animator.razor_rolling);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
    }

    public boolean getIsWaiting() {
        return isWaiting;
    }

    public void setIsWaiting(boolean value) {
        Log.d("Fragment Status", Boolean.toString(value));
        isWaiting = value;
        if (isWaiting) {
            civ_avatar.setVisibility(View.VISIBLE);
            tv_waiting.setVisibility(View.VISIBLE);
            tv_hint.setVisibility(View.INVISIBLE);
            tv_hint_content.setVisibility(View.INVISIBLE);
            iv_radar.setVisibility(View.VISIBLE);
            iv_radar.clearAnimation();
            iv_radar.setAnimation(animation);
        } else {
            civ_avatar.setVisibility(View.INVISIBLE);
            tv_waiting.setVisibility(View.INVISIBLE);
            tv_hint.setVisibility(View.VISIBLE);
            tv_hint_content.setVisibility(View.VISIBLE);
            iv_radar.setVisibility(View.INVISIBLE);
            iv_radar.clearAnimation();
        }
    }
}
