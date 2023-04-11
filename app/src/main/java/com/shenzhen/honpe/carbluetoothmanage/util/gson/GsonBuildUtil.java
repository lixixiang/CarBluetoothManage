package com.shenzhen.honpe.carbluetoothmanage.util.gson;

import android.content.Context;
import android.content.res.AssetManager;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @ProjectName: Honpe
 * @CreateDate: 2020/7/6 13:34
 * @Author: 李熙祥
 * @Description: java类作用描述
 */
public class GsonBuildUtil {
    public static String GsonBuilder(Object obj) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String result = gson.toJson(obj);
        return result;
    }


    /**
     * 在Assets中引用的json数据调用方法
     *
     * @param context
     * @param testJson
     * @return
     */
    public static String getCityJson(Context context, String testJson) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(testJson)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    public static String GsonToString(Object o){
        Gson gson = new Gson();
        String result = gson.toJson(o);
        return result;
    }
    /**
     * 如果字符串是 null 则可以转为 "" 字符，避免 null 字段不打印
     *
     * @return
     */
    public static Gson NullToString() {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new Convert.NullStringToEmptyAdapterFactory()).create();
        return gson;
    }
}
