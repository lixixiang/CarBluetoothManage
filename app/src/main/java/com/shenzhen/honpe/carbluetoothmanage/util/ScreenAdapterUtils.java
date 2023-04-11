package com.shenzhen.honpe.carbluetoothmanage.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * FileName: ScreenAdapterUtils
 * Author: asus
 * Date: 2021/3/26 14:19
 * Description:屏幕适配类
 */
public class ScreenAdapterUtils {

    /***手机屏幕相关信息**/
    public static void getPhoneScreen(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();//获取屏幕分辨率
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels; //宽度px
        int height = metrics.heightPixels; //高度px
        float density = metrics.density; //密度 (0.75/1.0/1.5)
        int densityDpi = metrics.densityDpi; //密度DPI(120/160/240)
        //屏幕宽度算法：屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density); //屏幕宽度（DP）
        int screenHeight = (int) (height / density); //屏幕高度（DP）
        LatteLogger.d("testesgt", "宽度:" + width + " 高度:" + height + " 密度:" + density + " 密度DPI:" + densityDpi
                + "\r\n屏幕dp宽度：" + screenWidth + " 屏幕dp高度：" + screenHeight);
    }

    public static int getScreenHeight(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();//获取屏幕分辨率
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int height = metrics.heightPixels; //高度px
        return height;
    }
}
