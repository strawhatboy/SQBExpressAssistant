package com.sqbnet.expressassistant.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Andy on 7/3/2015.
 */
public class AsyncImageLoader {

    private static AsyncImageLoader sInst;
    private LoaderThread thread;
    private HashMap<String, SoftReference<Bitmap>> imageCache;

    private AsyncImageLoader() {
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }

    public static AsyncImageLoader getInst(){
        if(sInst == null){
            synchronized (AsyncImageLoader.class){
                if(sInst == null){
                    sInst = new AsyncImageLoader();
                }
            }
        }
        return sInst;
    }

    public void loadBitmap(String url, ImageLoadResultLister callback) {
        if (imageCache.containsKey(url)) {
            SoftReference<Bitmap> softReference = imageCache.get(url);
            Bitmap bitmap = softReference.get();
            if (bitmap != null) {
                callback.onImageLoadResult(bitmap);
                Log.d("AsyncImageLoader", "got bitmap from cache for url: " + url);
                return;
            } else {
                Log.e("AsyncImageLoader", "cache bitmap is null");
                imageCache.remove(url);
            }
        }

        if (thread == null) {
            thread = new LoaderThread(url, callback);
            thread.start();
        } else {
            thread.load(url, callback);
        }

    }

    private class LoaderThread extends Thread {
        LinkedHashMap<ImageLoadResultLister, String> mTaskMap;
        private boolean mIsWait;

        public LoaderThread(String url, ImageLoadResultLister callback) {
            mTaskMap = new LinkedHashMap<ImageLoadResultLister, String>();
            mTaskMap.put(callback, url);
        }

        public void load(String url, ImageLoadResultLister callback) {
            mTaskMap.remove(callback);
            mTaskMap.put(callback, url);
            if (mIsWait) {
                synchronized (this) {
                    this.notify();
                }
            }
        }

        @Override
        public void run() {
            while (mTaskMap.size() > 0) {
                mIsWait = false;
                final ImageLoadResultLister callback = mTaskMap.keySet().iterator().next();
                final String url = mTaskMap.remove(callback);
                final Bitmap bitmap;
                if (imageCache.containsKey(url)) {
                    Log.d("AsyncImageLoader", "got bitmap from cache for url: " + url);
                    bitmap = imageCache.get(url).get();
                } else {
                    Log.d("AsyncImageLoader", "got bitmap from url: " + url);
                    bitmap = UtilHelper.getBitmapFromUrl(url);
                    imageCache.put(url, new SoftReference<Bitmap>(bitmap));
                }

                callback.onImageLoadResult(bitmap);

                if (mTaskMap.isEmpty()) {
                    try {
                        mIsWait = true;
                        synchronized (this) {
                            this.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public interface ImageLoadResultLister {
        void onImageLoadResult(final Bitmap bitmap);
    }

    public static class AsyncImageViewBinder implements SimpleAdapter.ViewBinder {

        private Activity activity;

        public AsyncImageViewBinder(Activity activity) {
            if (activity != null) {
                this.activity = activity;
            } else {
                throw new IllegalArgumentException("Activity cannot be null");
            }
        }

        @Override
        public boolean setViewValue(View view, Object o, String s) {
            // hook the default behavior of SimpleAdapter
            if (view instanceof ImageView) {
                final ImageView iv = (ImageView) view;
                if (s.startsWith("http")) {
                    try {
                        AsyncImageLoader.getInst().loadBitmap(s, new AsyncImageLoader.ImageLoadResultLister() {
                            @Override
                            public void onImageLoadResult(final Bitmap bitmap) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            iv.setImageBitmap(bitmap);
                                        }catch (Exception e){
                                            Log.e("AsyncImageLoader", "virgil", e);
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });
                    }catch (Exception e){
                        Log.e("AsyncImageLoader", "virgil", e);
                        e.printStackTrace();
                    }
                }
                return true;
            }
            return false;
        }
    }
}
