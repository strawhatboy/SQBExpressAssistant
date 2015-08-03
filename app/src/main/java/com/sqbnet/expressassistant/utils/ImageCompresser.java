package com.sqbnet.expressassistant.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Andy on 8/3/2015.
 */
public class ImageCompresser {
    private static ImageCompresser inst;
    public static final int BITMAP_COMPRESS_QUALITY = 60;

    public static ImageCompresser getInst(){
        if(inst == null){
            synchronized (ImageCompresser.class){
                if(inst == null){
                    inst = new ImageCompresser();
                }
            }
        }
        return inst;
    }

    public byte[] getCompressedBitmapBytes(String filePath, int newWidth, int newHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, BITMAP_COMPRESS_QUALITY, baos);
            bitmap.recycle();
            Log.d("ImageCompresser", "getCompressedBitmapBytes called");
            return baos.toByteArray();
        } catch (Exception e) {
            // OutOfMemory
            Log.e("ImageCompresser", "failed to compress the bitmap");
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getCompressedBitmap(String filePath, int newWidth, int newHeight) {
        try {
            byte[] bytes = getCompressedBitmapBytes(filePath, newWidth, newHeight);
            ByteArrayInputStream isBm = new ByteArrayInputStream(bytes);
            Bitmap bm = BitmapFactory.decodeStream(isBm, null, null);
            Log.d("ImageCompresser", "getCompressedBitmap(String filePath, int newWidth, int newHeight) called");
            return bm;
        } catch (Exception e) {
            // OutOfMemory
            Log.e("ImageCompresser", "failed to compress the bitmap");
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getCompressedBitmap(Bitmap originalBitmap, int newWidth, int newHeight) {
        try {
            Bitmap bitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, BITMAP_COMPRESS_QUALITY, baos);
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            Bitmap bm = BitmapFactory.decodeStream(isBm, null, null);
            bitmap.recycle();
            Log.d("ImageCompresser", "getCompressedBitmap(Bitmap originalBitmap, int newWidth, int newHeight) called");
            return bm;
        } catch (Exception e) {
            // OutOfMemory
            Log.e("ImageCompresser", "failed to compress the bitmap");
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getCompressedBitmap(Bitmap originalBitmap, double ratio) {
        int newWidth = (int)(originalBitmap.getWidth() * ratio);
        int newHeight = (int)(originalBitmap.getHeight() * ratio);
        Log.d("ImageCompresser", "getCompressedBitmap(Bitmap originalBitmap, double ratio) called");
        return getCompressedBitmap(originalBitmap, newWidth, newHeight);
    }

    public Bitmap getCompressedBitmapWithMaxBorder(Bitmap originalBitmap, int maxBorder) {
        Log.d("ImageCompresser", "getCompressedBitmapWithMaxBorder(Bitmap originalBitmap, int maxBorder) called");
        return getCompressedBitmap(originalBitmap, maxBorder, true);
    }

    public Bitmap getCompressedBitmapWithMinBorder(Bitmap originalBitmap, int minBorder) {
        Log.d("ImageCompresser", "getCompressedBitmapWithMinBorder(Bitmap originalBitmap, int minBorder) called");
        return getCompressedBitmap(originalBitmap, minBorder, false);
    }

    public Bitmap getCompressedBitmap(Bitmap originalBitmap, int limitedBorderSize, boolean isLongerBorderLimited) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        boolean isWidthMin = isLongerBorderLimited ? width > height : width < height;
        int newWidth = -1;
        int newHeight = -1;
        if (isWidthMin && width > limitedBorderSize) {
            newWidth = limitedBorderSize;
            newHeight = (int)((double)limitedBorderSize * (double)height / (double)width);
        }

        if (!isWidthMin && height > limitedBorderSize) {
            newHeight = limitedBorderSize;
            newWidth = (int)((double)limitedBorderSize * (double)width / (double)height);
        }
        Log.d("ImageCompresser", "getCompressedBitmap(Bitmap originalBitmap, int limitedBorderSize, boolean isLongerBorderLimited) called");

        if (newWidth != -1 && newHeight != -1) {
            return getCompressedBitmap(originalBitmap, newWidth, newHeight);
        } else {
            return originalBitmap;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
