package com.shenzhen.honpe.carbluetoothmanage.config;

import java.security.PublicKey;
import java.util.List;

/**
 * FileName: ConfigData
 * Author: asus
 * Date: 2021/5/26 16:00
 * Description:执行操作时动作
 */
public class ConfigData {

    /**
     * 1-总开关
     */
     public static final String MAIN_SWITCH_ON = "F5 A0 A0 0A";
    public static final String MAIN_SWITCH_OFF = "F5 A1 A1 1A";
    /**
     * 2-大灯
     */
    public static final String BIG_LIGHT_ON = "F5 A2 A2 2A";
    public static final String BIG_LIGHT_OFF = "F5 A3 A3 3A";
    /**
     * 3-日行灯
     */
    public static final String DAYTIME_RUNNING_LIGHT_ON = "F5 A4 A4 4A";
    public static final String DAYTIME_RUNNING_LIGHT_OFF = "F5 A5 A5 5A";
    /**
     * 4-左转向灯
     */
    public static final String LEFT_LIGHT_ON = "F5 A6 A6 6A";
    public static final String LEFT_LIGHT_OFF = "F5 A7 A7 7A";
    /**
     * 5-右转向灯
     */
    public static final String RIGHT_LIGHT_ON = "F5 A8 A8 8A";
    public static final String RIGHT_LIGHT_OFF = "F5 A9 A9 9A";
    /**
     * 6-刹车灯
     */
    public static final String STOP_LIGHT_ON = "F5 B0 B0 0B";
    public static final String STOP_LIGHT_OFF = "F5 B1 B1 1B";
    /**
     * 7-倒车灯
     */
    public static final String BACK_CAR_LIGHT_ON = "F5 B2 B2 2B";
    public static final String BACK_CAR_LIGHT_OFF = "F5 B3 B3 3B";
    /**
     * 8-主座移位
     */
    public static final String MAIN_SEAT_BEFORE = "F5 B4 B4 4B";
    public static final String MAIN_SEAT_BACK = "F5 B5 B5 5B";
    public static final String MAIN_SEAT_STOP = "F5 B6 B6 6B";
    /**
     * 9-副座移位
     */
    public static final String FONT_SEAT_BEFORE = "F5 B7 B7 7B";
    public static final String FONT_SEAT_BACK = "F5 B8 B8 8B";
    public static final String FONT_SEAT_STOP = "F5 B9 B9 9B";
    /**
     * 10-内饰按键灯
     */
    public static final String INTERIOR_BUTTON_LIGHT_ON = "F5 C0 C0 0C";
    public static final String INTERIOR_BUTTON_LIGHT_OFF = "F5 C1 C1 1C";
    /**
     *11-内饰氛围灯
     */
    public static final String INTERIOR_AMBIENT_LIGHT_ON = "F5 C2 C2 2C";
    public static final String INTERIOR_AMBIENT_LIGHT_OFF = "F5 C3 C3 3C";
    /**
     * 12-手球氛围灯
     */
    public static final String HANDBALL_ATMOSPHERE_LIGHT_ON = "F5 C4 C4 4C";
    public static final String HANDBALL_ATMOSPHERE_LIGHT_OFF = "F5 C5 C5 5C";
    /**
     * 13-主屏
     */
    public static final String MAIN_SCREEN_ON = "F5 C6 C6 6C";
    public static final String MAIN_SCREEN_OFF = "F5 C7 C7 7C";
    /**
     *14-预留接口
     */
    public static final String RESERVED_INTERFACE_ON = "F5 D2 D2 2D";
    public static final String RESERVED_INTERFACE_OFF = "F5 D3 D3 3D";
    /**
     * 15-预留接口
     */
    public static final String RESERVED_INTERFACE_2_ON = "F5 D0 D0 0D";
    public static final String RESERVED_INTERFACE_2_OFF = "F5 D1 D1 1D";

    /**
     * 接收正确
     */
    public static final String RIGHT = "F5 A0 ";
    /**
     * 接收错误
     */
    public static final String WRONG = "5F 0A ";
    /**
     * 防止线程死机
     */
    public static final String IS_THREAD = "F5 F5 F5 F5 ";
}
























