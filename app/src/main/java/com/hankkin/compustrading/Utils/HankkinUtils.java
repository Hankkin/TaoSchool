/**
 * YUtils.java[V1.0.0]
 * classes : com.wadiankeji.creditsmanager.util.YUtils
 *
 * @author Hankkin Create at 2014年11月27日 下午7:48:31
 */
package com.hankkin.compustrading.Utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * by 李斐 at 2014年11月27日 下午7:48:31
 * 增加getPath相关方法
 * by：李斐 at:2015-06-18 10:51:16
 */
public class HankkinUtils {
    private static final String TAG = "YUtils";

    // 记录屏幕的高度、宽度、密度等信息。
    public static int screenH;
    public static int screenW;
    public static float screenDensity; // 屏幕密度（0.75 / 1.0 / 1.5）
    public static int screenDensityDpi; // 屏幕密度DPI（120 / 160 / 240）
    public static int statusBarHeight; // 状态栏高度

    @SuppressLint("SimpleDateFormat")
    public static String longtimeToDate(long time) {
        Date now = new Date(time * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期格式
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String dateStr = dateFormat.format(now);
        return dateStr;
    }

    /**
     * 修改时间戳转化时间
     * by黄海杰 at:2015年7月27日 15:44:16
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String longtimeToDayDate(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");// 可以方便地修改日期格式
        String dateStr = dateFormat.format(new Date(time));
        return dateStr;
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String currentTime = sdf.format(date);
        return currentTime;
    }


    @SuppressLint("SimpleDateFormat")
    public static String longtimeToDateYMD(long time) {
        Date now = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期格式
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String dateStr = dateFormat.format(now);
        return dateStr;
    }


    /**
     * 根据yyyy-MM-dd HH:mm:ss格式时间字符串转为long型时间戳
     *
     * @param dateStr
     * @return date long
     * by:Hankkin at:2015年6月25日 17:38:25 修改时区设置
     */
    public static long stringDateToLong(String dateStr) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }


    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     * 修改为向下取整
     * by黄海杰 at:2015年7月1日 17:08:55
     *
     * @param dateTime 时间戳
     * @return
     */
    public static String getStandardDate(long dateTime) {

        StringBuffer sb = new StringBuffer();
//		long t = Long.parseLong(timeStr);
//		long time = System.currentTimeMillis() - (t*1000);
        long time = System.currentTimeMillis() - (dateTime);
        long mill = (long) Math.floor(time / 1000);//秒前

        long minute = (long) Math.floor(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.floor(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.floor(time / 24 / 60 / 60 / 1000.0f);// 天前

        if (day - 1 > 0) {
            sb.append(day + "天");
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                sb.append("1天");
            } else {
                sb.append(hour + "小时");
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1小时");
            } else {
                sb.append(minute + "分钟");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1分钟");
            } else {
                sb.append(mill + "秒");
            }
        } else {
            sb.append("刚刚");
        }
        if (!sb.toString().equals("刚刚")) {
            sb.append("前");
        }
        return sb.toString();
    }

    /**
     * 根据时间戳的差获取时间差
     * by黄海杰 at:2015年7月13日 11:24:25
     * 修改超过一天的显示时间
     * by黄海杰 at:2015年7月27日 15:44:32
     *
     * @param dateTime
     * @return
     */
    public static String getDateAgo(long dateTime) {

        String days = null;
//		long t = Long.parseLong(timeStr);
//		long time = System.currentTimeMillis() - (t*1000);
        long timeInterval = (System.currentTimeMillis() - (dateTime)) / 1000;


        if (timeInterval < 60) {
            days = "1分钟前";
        } else if (timeInterval < 3600) {
            days = "" + (int) Math.round(timeInterval / 60) + "分钟内";
        } else if (timeInterval < 86400) {
            if (timeInterval % 3600 > 1800) {
                days = "" + (int) Math.round((timeInterval / 3600) + 1) + "小时内";
            } else {
                days = "" + (int) Math.round((timeInterval / 3600)) + "小时内";
            }
        }
//		else if (timeInterval<2592000){
//			days = ""+(int)Math.floor(timeInterval/86400)+"天前";
//		}
//		else if (timeInterval<31536000){
//			days = ""+(int)Math.floor(timeInterval/2592000)+"个月前";
//		}
//		else {
//			days = ""+(int)Math.floor(timeInterval/31536000)+"年前";
//		}
        else {
            days = longtimeToDayDate(dateTime);
        }
        return days;
    }

    /**
     * 升级检测
     *
     * @param locVersionName
     * @param lastVersion
     * @return 是否升级
     */
    public static boolean checkUpdate(String locVersionName, String lastVersion) {
        boolean hasUpdate = false;
        String[] locVersionS = locVersionName.split("\\.");
        String[] lastVersionS = lastVersion.split("\\.");

        if (!locVersionName.equals(lastVersion)) {
            if (locVersionS != null && lastVersion != null) {
                int localLenth = locVersionS.length;
                int lastVerLenth = lastVersionS.length;

                // int netLenth = lastVersion.length();
                for (int i = 0; i < lastVerLenth; i++) {
                    if (localLenth < lastVerLenth && i == localLenth) {
                        hasUpdate = true;
                        return hasUpdate;
                    }

                    if (Integer.valueOf(lastVersionS[i]) > Integer
                            .valueOf(locVersionS[i])) {
                        hasUpdate = true;
                        return hasUpdate;
                    } else if (Integer.valueOf(lastVersionS[i]) < Integer
                            .valueOf(locVersionS[i])) {
                        hasUpdate = false;
                        return hasUpdate;
                    }
                }
            }
        } else {
            hasUpdate = false;
        }
        return hasUpdate;
    }

    /**
     * bitmap转byte数组
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp,
                                        final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 实现文本复制功能 add by lif
     *
     * @param content
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        HankkinUtils.showToast(context, "内容已复制");
    }

    /**
     * 实现粘贴功能 add by lif
     *
     * @param context
     * @return
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInputMethod(Activity act) {
        View view = act.getWindow().peekDecorView();
        if (view != null) {
            // 隐藏虚拟键盘
            InputMethodManager inputmanger = (InputMethodManager) act
                    .getSystemService(act.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 切换软件盘 显示隐藏
     */
    public static void switchSoftInputMethod(Activity act) {
        // 方法一(如果输入法在窗口上已经显示，则隐藏，反之则显示)
        InputMethodManager iMM = (InputMethodManager) act
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        iMM.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }




    /**
     * 验证是否手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 中文识别
     */
    public static boolean hasChinese(String source) {
        String reg_charset = "([\\u4E00-\\u9FA5]*+)";
        Pattern p = Pattern.compile(reg_charset);
        Matcher m = p.matcher(source);
        boolean hasChinese = false;
        while (m.find()) {
            if (!"".equals(m.group(1))) {
                hasChinese = true;
            }
        }
        return hasChinese;
    }

    /**
     * 用户名规则判断
     *
     * @param uname
     * @return
     */
    public static boolean isAccountStandard(String uname) {
        Pattern p = Pattern.compile("[A-Za-z0-9_]+");
        Matcher m = p.matcher(uname);
        return m.matches();
    }

    // java 合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * 删除文件夹下所有文件
     * Hankkin at:2015年4月21日 20:05:01
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
//			file.delete();
        }
    }


    /**
     * 取得系统版本号
     * by: Hankkin at: 2015-04-13
     *
     * @param context
     * @return version 当前项目版本号
     */
    public static String GetVersion(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return manager.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }





    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

//	public static String getDateAgo(int timeline){
//		long curTimeline = System.currentTimeMillis();
//		int timeInterval = timeline - curTimeline;
//		if (timeInterval<60){
//			return "1分钟内";
//		}
//		else if (timeInterval<3600){
//			return ""+Math.floor(timeInterval/60)+"分钟前";
//		}
//		else if (timeInterval<86400){
//			return ""+Math.floor(timeInterval/3600)+"小时前";
//		}
//		else if (timeInterval<2592000){
//			return ""+Math.floor(timeInterval/86400)+"天前";
//		}
//		else if (timeInterval<31536000){
//			return ""+Math.floor(timeInterval/2592000)+"个月前";
//		}
//		else {
//			return ""+Math.floor(timeInterval/31536000)+"年前";
//		}
//	}


    /**
     * 创建视频临时帧图片
     * by Hankkin at:2015年8月18日 11:21:03
     *
     * @param filePath
     * @return
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    /**
     * 记录上次点击时间
     */
    private static long lastClickTime;

    /**
     * 是否快速双击点击
     *
     * @return isFastDoubleClick
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 600) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

/*-----------------------toast start-----------------------*/

    /**
     * 提示字符串
     * short Toast
     *
     * @param context
     * @param text
     */
    public static void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 提示字符串
     * short Toast
     *
     * @param context
     * @param text
     */
    public static void showLToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 提示根据ResId关联字符串
     * short Toast
     * by:Hankkin at:2015年4月30日 14:39:41
     *
     * @param context
     * @param resId
     */
    public static void showToast(Context context, int resId) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 提示根据ResId关联字符串
     * 时常long	Toast
     * by:Hankkin at:2015年4月30日 14:39:41
     *
     * @param context
     * @param resId
     */
    public static void showLToast(Context context, int resId) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
        ;
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    /*-----------------------toast end-----------------------*/
    /*获得屏幕相关的辅助类*/
    /*-----------------------screen start-----------------------*/

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取屏幕密度
     *
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * dip转px像素
     *
     * @param context
     * @param px
     * @return
     */
    public static int dip2px(Context context, float px) {
        final float scale = getScreenDensity(context);
        return (int) (px * scale + 0.5);
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }



    /*-----------------------screen end-----------------------*/
    /*日志打印*/
    /*-----------------------log start-----------------------*/
    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }
    /*-----------------------log end-----------------------*/

    /**
     * 字符串转MD5
     * by黄海杰 at:2015-10-29 16:15:32
     *
     * @param string
     * @return
     */
    public static String md5(String string) {

        byte[] hash;

        try {

            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("Huh, MD5 should be supported?", e);

        } catch (UnsupportedEncodingException e) {

            throw new RuntimeException("Huh, UTF-8 should be supported?", e);

        }


        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {

            if ((b & 0xFF) < 0x10) hex.append("0");

            hex.append(Integer.toHexString(b & 0xFF));

        }

        return hex.toString();

    }


    /**
     * 获取手机号
     * by Hankkin
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context){
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)  context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }




}
