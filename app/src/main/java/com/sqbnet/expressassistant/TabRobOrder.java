package com.sqbnet.expressassistant;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.controls.CircleImageView;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.AsyncImageLoader;
import com.sqbnet.expressassistant.utils.UtilHelper;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabRobOrder extends Fragment {
    private boolean isWaiting;

    private CircleImageView civ_avatar;
    private CircleImageView civ_map;
    private TextView tv_waiting;
    private TextView tv_hint;
    private TextView tv_hint_content;
    private TextView tv_timer;

    private ImageView iv_radar;
    private ImageView iv_white_circle;
    private Animation animation;
    private Animation anim_white_circle;
    private Animation anim_dot;
    private List<ImageView> orange_dots;
    private AsyncTask<Integer, Integer, Integer> startDotAnimationTask;

    private Timer timer;

    private int timerCount = 0;

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
        civ_map = (CircleImageView) view.findViewById(R.id.civ_searching_map);
        tv_waiting = (TextView) view.findViewById(R.id.tv_searching_waiting);
        tv_hint = (TextView) view.findViewById(R.id.tv_searching_hint);
        tv_hint_content = (TextView) view.findViewById(R.id.tv_searching_hint_content);
        iv_radar = (ImageView) view.findViewById(R.id.iv_searching_radar);
        tv_timer = (TextView) view.findViewById(R.id.tv_searching_timer);
        iv_white_circle = (ImageView) view.findViewById(R.id.iv_white_circle);
        orange_dots = new ArrayList<ImageView>();
        orange_dots.add((ImageView) view.findViewById(R.id.iv_searching_dot_1));
        orange_dots.add((ImageView) view.findViewById(R.id.iv_searching_dot_2));
        orange_dots.add((ImageView) view.findViewById(R.id.iv_searching_dot_3));

        animation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.razor_rolling);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);

        anim_white_circle = AnimationUtils.loadAnimation(this.getActivity(), R.anim.radar_circle_expand);
        anim_dot = AnimationUtils.loadAnimation(getActivity(), R.anim.radar_circle_blink);

        String userId = UtilHelper.getSharedUserId();
        if (userId != null && userId.length() > 0) {
            SQBProvider.getInst().getDispatchPerson(userId, new SQBResponseListener() {
                @Override
                public void onResponse(final SQBResponse response) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("virgil", "TabHsitoryOrder get DispatchPerson");
                                if (response == null) {
                                    Toast.makeText(getActivity().getApplicationContext(), "没有找到用户", Toast.LENGTH_SHORT);
                                    return;
                                }

                                if (!response.getCode().equals("1000")) {
                                    Toast.makeText(getActivity().getApplicationContext(), response.getMsg(), Toast.LENGTH_SHORT);
                                    return;
                                }

                                try {
                                    JSONObject userInfo = (JSONObject) response.getData();
                                    String avatar = userInfo.getString("cardphoto");
                                    if (avatar != null && avatar.length() > 0) {
                                        AsyncImageLoader.getInst().loadBitmap(avatar, new AsyncImageLoader.ImageLoadResultLister() {
                                            @Override
                                            public void onImageLoadResult(final Bitmap bitmap) {
                                                if (getActivity() != null) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            civ_avatar.setImageBitmap(bitmap);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(), "出错了", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                }
            });
        }
        
    }

    public boolean getIsWaiting() {
        return isWaiting;
    }

    public void setIsWaiting(boolean value) {
        Log.d("Fragment Status", Boolean.toString(value));
        boolean needRefreshTimer = isWaiting != value;
        setTimerSeconds(timerCount);
        isWaiting = value;
        if (isWaiting) {
            civ_avatar.setVisibility(View.VISIBLE);
            tv_waiting.setVisibility(View.VISIBLE);
            tv_hint.setVisibility(View.INVISIBLE);
            tv_hint_content.setVisibility(View.INVISIBLE);
            iv_radar.setVisibility(View.VISIBLE);
            iv_white_circle.setVisibility(View.VISIBLE);
            tv_timer.setVisibility(View.VISIBLE);
            civ_map.setVisibility(View.VISIBLE);
            iv_radar.clearAnimation();
            iv_radar.setAnimation(animation);
            iv_white_circle.clearAnimation();
            iv_white_circle.setAnimation(anim_white_circle);
            startDotAnimation();
            if (needRefreshTimer) startTimer();
        } else {
            civ_avatar.setVisibility(View.INVISIBLE);
            tv_waiting.setVisibility(View.INVISIBLE);
            tv_hint.setVisibility(View.VISIBLE);
            tv_hint_content.setVisibility(View.VISIBLE);
            iv_radar.setVisibility(View.INVISIBLE);
            iv_white_circle.setVisibility(View.INVISIBLE);
            tv_timer.setVisibility(View.INVISIBLE);
            civ_map.setVisibility(View.INVISIBLE);
            iv_radar.clearAnimation();
            iv_white_circle.clearAnimation();
            for (int i = 0; i < orange_dots.size(); i++) {
                orange_dots.get(i).clearAnimation();
                orange_dots.get(i).setVisibility(View.INVISIBLE);
            }
            if (needRefreshTimer) stopTimer();
        }
    }

    private void startTimer() {
        timer = new Timer();
        timerCount = 0;
        setTimerSeconds(timerCount);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                timerCount++;
                setTimerSeconds(timerCount);
                super.handleMessage(msg);
            }
        };
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if(timer != null) {
            timer.cancel();
        }
    }

    private void setTimerSeconds(int seconds) {
        int minutes = seconds / 60;
        int seconds_left = seconds % 60;
        tv_timer.setText((minutes < 10 ? "0" : "") + minutes + ":" + (seconds_left < 10 ? "0" : "") + seconds_left);
    }

    private void startDotAnimation() {
        startDotAnimationTask = new AsyncTask<Integer, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Integer... integers) {
                for (int i = 0; i < orange_dots.size(); i++) {
                    final int index = i;
                    try {
                        Thread.sleep(MyApplication.getInst().getRandom().nextInt(5000));
                        if (getActivity() != null && isWaiting) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    orange_dots.get(index).setVisibility(View.VISIBLE);
                                    orange_dots.get(index).setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.radar_circle_blink));
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        startDotAnimationTask.execute();
    }

    public void setAvatar(String url) {
        AsyncImageLoader.getInst().loadBitmap(url, new AsyncImageLoader.ImageLoadResultLister() {
            @Override
            public void onImageLoadResult(final Bitmap bitmap) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            civ_avatar.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });
    }
}
