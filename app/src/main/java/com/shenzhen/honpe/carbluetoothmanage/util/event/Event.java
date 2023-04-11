package com.shenzhen.honpe.carbluetoothmanage.util.event;

/**
 * FileName: Event
 * Author: asus
 * Date: 2021/5/4 10:13
 * Description:事件分发实体
 */
public class Event<T> {
    private int code;
    private T data;

    public Event(int code) {
        this.code = code;
    }
    public Event(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

