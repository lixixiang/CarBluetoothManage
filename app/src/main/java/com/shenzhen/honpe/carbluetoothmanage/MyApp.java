package com.shenzhen.honpe.carbluetoothmanage;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import me.jessyan.autosize.AutoSizeConfig;

/**
 * FileName: MyApp
 * Author: asus
 * Date: 2021/5/25 11:50
 * Description:
 */
public class MyApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        AutoSizeConfig.getInstance().getUnitsManager()
                .setSupportDP(true)
                .setSupportSP(true);
        context = getApplicationContext();
        Logger.addLogAdapter(new AndroidLogAdapter());


    }



    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 全局上下文
     *
     * @return
     */
    public static Context getContext() {
        return context;
    }

}

