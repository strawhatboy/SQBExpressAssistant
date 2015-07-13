package com.sqbnet.expressassistant.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sqbnet.expressassistant.Location.GPSLocation;
import com.sqbnet.expressassistant.MyApplication;
import com.sqbnet.expressassistant.R;
import com.sqbnet.expressassistant.external.map.IMapProvider;
import com.sqbnet.expressassistant.external.map.MapProviderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by virgil on 6/29/15.
 * this is a helper class
 */
public class UtilHelper {

    public static String userId;
    public static String token;
    public static int intentId = 0;

    public interface IHandleXGMessage{
        void test();
    }

    public static int getIntentId(){
        UtilHelper.intentId += 1;
        return UtilHelper.intentId;
    }

    public static boolean isMobileNO(String mobiles){
        Pattern p = Pattern.compile("^1[34578]{1}\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static String getSharedUserId(){
        return UtilHelper.userId;
    }

    public static void setSharedUserId(String userId){
        UtilHelper.userId = userId;
    }

    public static void setXGToken(String token){
        UtilHelper.token = token;
    }

    public static String getXGToken(String token){
        return UtilHelper.token;
    }

    public static String getDateString(long timeStamp) {
        return getDateString(timeStamp, "yyyy.MM.dd HH:mm:ss");
    }

    public static String getDateString(long timeStamp, String format){
        Calendar calendar = Calendar.getInstance();
        DateFormat objFormatter = new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE);
        TimeZone timeZone = calendar.getTimeZone();
        objFormatter.setTimeZone(timeZone);
        String result = objFormatter.format(getDate(timeStamp));
        calendar.clear();
        return result;
    }
    
    public static Date getDate(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);//edit
        return calendar.getTime();
    }


    public static String ImagePathToB64(String imagePath){
        File file = new File(imagePath);
        InputStream in;
        byte[] data = null;
        try{
            in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        if( data != null) {
            return Base64.encodeToString(data, Base64.DEFAULT);
        }else {
            return "";
        }
    }

    public static String MD5(String str)
    {
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }catch(Exception e)
        {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for(int i = 0; i < charArray.length; i++)
        {
            byteArray[i] = (byte)charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for( int i = 0; i < md5Bytes.length; i++)
        {
            int val = ((int)md5Bytes[i])&0xff;
            if(val < 16)
            {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String IDCardValidate(String IDStr) throws ParseException {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2" };
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2" };
        String Ai = "";
        // ================ 号码的长�15位或18�================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数�================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            errorInfo = "身份�5位号码都应为数字 ; 18位号码除最后一位外，都应为数字";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "身份证生日无效";
            return errorInfo;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                || (gc.getTime().getTime() - s.parse(
                strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
            errorInfo = "身份证生日不在有效范围内";
            return errorInfo;
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            return errorInfo;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            return errorInfo;
        }
        // =====================(end)=====================

        // ================ 地区码时候有�================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误";
            return errorInfo;
        }
        // ==============================================

        // ================ 判断最后一位的�================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                errorInfo = "身份证无效，不是合法的身份证号码";
                return errorInfo;
            }
        } else {
            return "";
        }
        // =====================(end)=====================
        return "";
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    private static Hashtable<String, String> GetAreaCode() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    public static boolean isDataFormat(String str){
        boolean flag=false;
        //String regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr="^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1=Pattern.compile(regxStr);
        Matcher isNo=pattern1.matcher(str);
        if(isNo.matches()){
            flag=true;
        }
        return flag;
    }

    public static ProgressDialog getProgressDialog(String message, Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("商圈宝提示");
        progressDialog.setMessage(message);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }

    public static void showToast(String message){
        Toast.makeText(MyApplication.getInst().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap getBitmapFromUrl(String httpUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            InputStream inputStream = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    public static void checkGPSLocation(final Activity activity){
        Log.i("virgil", "check GPS settings");
        if(!GPSLocation.getInst().openGEPSettings()){
            Log.i("virgil", "show dialog");
            new AlertDialog.Builder(activity).setTitle("提示")
                    .setMessage("GPS定位没有开启，请手动开启！")
                    .setPositiveButton("已开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkGPSLocation(activity);
                                }
                            });
                        }
                    })
                    .show();
            return;
        }

        /*Log.i("virgil", "check network settings");
        if(!GPSLocation.getInst().openNetworkSettings()){
            new AlertDialog.Builder(activity).setTitle("提示")
                    .setMessage("移动网络没有开启，请手动开启！")
                    .setPositiveButton("已开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkGPSLocation(activity);
                                }
                            });
                        }
                    })
                    .show();
            return;
        }*/

        Log.i("virgil", "start GPSLocation");
        GPSLocation.getInst().start();
        //BaiDuLocationService.getInst().getLocationClient().start();
    }

    public static void startMapByAddress(final Activity currentActivity, final String address) {
        List<IMapProvider> availableMapApps = MapProviderFactory.getInst().getAvailableMapProviders();
        if (availableMapApps == null) {
            showNoMapWarning(currentActivity);
            return;
        }

        if (availableMapApps.size() != 1) {
            // show selector
            showMapAppSelector(currentActivity, availableMapApps, new OnMapAppSelected() {
                @Override
                public void select(IMapProvider provider) {
                    provider.startMapByAddress(currentActivity, address);
                }
            });
        } else {
            IMapProvider availableMapApp = availableMapApps.get(0);
            availableMapApp.startMapByAddress(currentActivity, address);
        }
    }

    public static void startMapByLocation(final Activity currentActivity, final double latitude, final double longitude, final String title, final String content) {
        List<IMapProvider> availableMapApps = MapProviderFactory.getInst().getAvailableMapProviders();
        if (availableMapApps == null) {
            showNoMapWarning(currentActivity);
            return;
        }

        if (availableMapApps.size() != 1) {
            // show selector
            showMapAppSelector(currentActivity, availableMapApps, new OnMapAppSelected() {
                @Override
                public void select(IMapProvider provider) {
                    provider.startMapByLocation(currentActivity, latitude, longitude, title, content);
                }
            });
        } else {
            IMapProvider availableMapApp = availableMapApps.get(0);
            availableMapApp.startMapByLocation(currentActivity, latitude, longitude, title, content);
        }
    }

    public interface OnMapAppSelected {
        void select(IMapProvider provider);
    }

    public static void showMapAppSelector(Activity currentActivity, final List<IMapProvider> providers, final OnMapAppSelected callback) {
        if (providers.size() == 0) {
            return;
        } else if (providers.size() == 1) {
            callback.select(providers.get(0));
        }
        List<String> appNames = new ArrayList<String>();
        for (IMapProvider provider : providers) {
            String appDisplayName = provider.getPackageDisplayName()
                                            .replace(new String(new char[]{ 160 }), "")
                                            .trim();
            appNames.add(appDisplayName);
            Log.i("Map", "get the app displayname: " + appDisplayName);
        }
        AlertDialog dialog = new AlertDialog.Builder(currentActivity)
                .setTitle(R.string.dialog_open_map)
                .setItems(Arrays.copyOf(appNames.toArray(), appNames.size(), String[].class), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.select(providers.get(i));
                    }
                }).create();
        dialog.show();
    }

    public static void showNoMapWarning(Activity currentActivity) {
        AlertDialog dialog = new AlertDialog.Builder(currentActivity)
                .setTitle(R.string.dialog_title_info)
                .setMessage(R.string.dialog_map_no_map)
                .create();
        dialog.show();
    }

    public static boolean isAppInstalled(String packageName) {
        final PackageManager packageManager = MyApplication.getInst().getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ )
        {
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public static View.OnClickListener getPhoneNumberOnClickListener(final Activity activity) {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof TextView) {
                    final String phoneNumber = ((TextView) view).getText().toString();

                    if (phoneNumber.matches("[\\d-]+")) {
                        new AlertDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.dialog_dial))
                                .setMessage(activity.getResources().getString(R.string.intent_call) + " " + phoneNumber + " ?")
                                .setPositiveButton(activity.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                                        activity.startActivity(intent);
                                    }
                                })
                                .setNegativeButton(activity.getResources().getString(R.string.dialog_no), null)
                                .show();
                    }
                }
            }
        };
    }
}
