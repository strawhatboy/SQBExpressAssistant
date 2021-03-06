package com.sqbnet.expressassistant;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Random;
import java.util.Stack;

/**
 * Created by virgil on 7/1/15.
 */
public class MyApplication extends Application {

    private static Stack<Activity> activityStack;
    private static MyApplication sInst;
    private Random random;

    @Override
    public void onCreate() {
        Log.i("virgil", "my application create");
        super.onCreate();
        sInst = this;
        random = new Random();
        CrashHandler.getInstance().init(getApplicationContext());
        SDKInitializer.initialize(this);
        String appId = "900006394";
        boolean isDebug = false;
        CrashReport.initCrashReport(getApplicationContext(), appId, isDebug);
    }

    public static MyApplication getInst(){
        return sInst;
    }

    public void addActivity(Activity activity){
        Log.i("virgil", "add activity");
        if(activityStack == null){
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    public Activity currentActivity(){
        Activity activity = activityStack.lastElement();
        return activity;
    }

    public void finishActivity(){
        Log.i("virgil", "finish activity");
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    public void finishActivity(Activity activity){
        Log.i("virgil", "finish activity");
        if(activity != null){
            activityStack.remove(activity);
        }
    }

    public void finishActivity(Class<?> cls){
        for(Activity activity : activityStack){
            if(activity.getClass().equals(cls)){
                finishActivity(activity);
            }
        }
    }

    public void finishAllActivity(){
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                Log.i("virgil", "finish all activity:" + i);
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public void AppExit() {
        Log.i("virgil", "app exit");
        try {
            finishAllActivity();
            //Process.killProcess(Process.myPid());
            //System.exit(1);
        } catch (Exception e) {
        }
    }

    public Random getRandom() {
        return random;
    }
}
