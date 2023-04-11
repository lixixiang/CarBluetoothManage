package com.shenzhen.honpe.carbluetoothmanage.util.event;


import org.greenrobot.eventbus.EventBus;

/**
 * FileName: EventBusUtil
 * Author: asus
 * Date: 2021/5/4 10:04
 * Description:事件分发工具
 */
public class EventBusUtil {
    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static void sendEvent(Event event) {
        EventBus.getDefault().post(event);
    }

    public static void sendStickyEvent(Event event) {
        EventBus.getDefault().postSticky(event);
    }
}

