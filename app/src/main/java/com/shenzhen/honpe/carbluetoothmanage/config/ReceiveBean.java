package com.shenzhen.honpe.carbluetoothmanage.config;

/**
 * FileName: ReceiveBean
 * Author: asus
 * Date: 2021/5/28 11:57
 * Description:
 */
public class ReceiveBean {
    private String sendInfo;
    private String receiveInfo;
    private byte[] currentPackge;

    public byte[] getCurrentPackge() {
        return currentPackge;
    }

    public void setCurrentPackge(byte[] currentPackge) {
        this.currentPackge = currentPackge;
    }

    public String getSendInfo() {
        return sendInfo;
    }

    public void setSendInfo(String sendInfo) {
        this.sendInfo = sendInfo;
    }

    public String getReceiveInfo() {
        return receiveInfo;
    }

    public void setReceiveInfo(String receiveInfo) {
        this.receiveInfo = receiveInfo;
    }
}
