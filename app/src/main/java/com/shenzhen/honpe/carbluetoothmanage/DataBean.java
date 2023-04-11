package com.shenzhen.honpe.carbluetoothmanage;

/**
 * FileName: DataBean
 * Author: asus
 * Date: 2021/5/20 17:25
 * Description:
 */
public class DataBean {
    private int icon;
    private String title;
    private String tips;
    private String control;
    private boolean isFlag = false; //是否被点击 默认没有点击

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public void setFlag(boolean flag) {
        isFlag = flag;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
