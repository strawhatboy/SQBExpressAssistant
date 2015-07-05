package com.sqbnet.expressassistant;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by virgil on 7/5/15.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final  String TAG = "CrashHandler";
    private static CrashHandler sInst;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private  CrashHandler(){

    }

    public static CrashHandler getInst(){
        if(sInst == null){
            synchronized (CrashHandler.class){
                if(sInst == null ){
                    sInst = new CrashHandler();
                }
            }
        }
        return sInst;
    }

    public void init(Context context){
        mContext = context;
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if(!handleException(throwable) && mDefaultUncaughtExceptionHandler !=null){
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
        }else {
            try{
                Thread.sleep(3000);
            }catch (InterruptedException e){
                Log.e(TAG, "virgil", e);
            }

            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    private  boolean handleException(final Throwable throwable){
        if(throwable == null){
            return  false;
        }
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序发生异常即将退出", Toast.LENGTH_SHORT).show();
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                throwable.getCause().printStackTrace(printWriter);
                printWriter.close();
                String result = writer.toString();
                Log.i("virgil", result);
                Looper.loop();
            }
        }.start();


        return  true;
    }
}
