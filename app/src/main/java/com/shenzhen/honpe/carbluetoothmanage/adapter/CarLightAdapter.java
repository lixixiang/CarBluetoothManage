package com.shenzhen.honpe.carbluetoothmanage.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.feasycom.controler.FscBleCentralApi;
import com.feasycom.util.FileUtil;
import com.feasycom.util.ToastUtil;
import com.shenzhen.honpe.carbluetoothmanage.DataBean;
import com.shenzhen.honpe.carbluetoothmanage.R;
import com.shenzhen.honpe.carbluetoothmanage.activity.MainActivity;
import com.shenzhen.honpe.carbluetoothmanage.config.ConfigData;
import com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam;
import com.shenzhen.honpe.carbluetoothmanage.config.ReceiveBean;
import com.shenzhen.honpe.carbluetoothmanage.util.LatteLogger;
import com.shenzhen.honpe.carbluetoothmanage.util.event.Event;
import com.shenzhen.honpe.carbluetoothmanage.util.event.EventBusUtil;
import com.shenzhen.honpe.carbluetoothmanage.view.BottomMusicDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.yokeyword.fragmentation.SupportActivity;

import static com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam.RECEIVE_DATA;
import static com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam.TIMER_TASK;

/**
 * FileName: CarLightAdapter
 * Author: asus
 * Date: 2021/5/22 8:57
 * Description:
 */
@SuppressLint("ClickableViewAccessibility")
public class CarLightAdapter extends BaseQuickAdapter<DataBean, BaseViewHolder> {
    private ImageView ivIcon;
    private TextView tvTitle, tvTips;
    private ScheduledExecutorService scheduledExecutor;
    private List<DataBean> data;
    private AppCompatActivity activity;
    private ReceiveBean bean = new ReceiveBean();
    private  FscBleCentralApi fscBleCentralApi;
    private  MediaPlayer mediaPlayer;
    private  byte[] currentPackge;
    private String strSend;
    private final ReceiveBean receiveBean = new ReceiveBean();
    public  Timer workTimer;
    public  MainActivity.MyTask mWorkTask ;
    public  Timer mainTimer;
    public  MainActivity.MainTask mainTask;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int viewId = msg.what;
            switch (viewId) {
                case 7:
                    data.get(viewId).setControl(ConfigData.MAIN_SEAT_BEFORE);
                    break;
                case 8:
                    data.get(viewId).setControl(ConfigData.MAIN_SEAT_BACK);
                    break;
                case 9:
                    data.get(viewId).setControl(ConfigData.FONT_SEAT_BEFORE);
                    break;
                case 10:
                    data.get(viewId).setControl(ConfigData.FONT_SEAT_BACK);
                    break;
            }
            bean.setSendInfo(data.get(viewId).getControl());
            bean.setCurrentPackge(currentPackge);
            Event<ReceiveBean> event = new Event<ReceiveBean>(RECEIVE_DATA, bean);
            EventBusUtil.sendEvent(event);
            currentPackge = FileUtil.hexToByte(data.get(viewId).getControl());
            fscBleCentralApi.send(currentPackge);
        }
    };

    public void updateData(List<DataBean> data) {
        if (data != null) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    public CarLightAdapter(@Nullable List<DataBean> data, FscBleCentralApi fscBleCentralApi,
                           Timer mTimer, MainActivity.MyTask task, Timer mainTimer, MainActivity.MainTask mainTask, AppCompatActivity activity) {
        super(R.layout.item_car_bluetooth, data);
        this.activity = activity;
        this.data = data;
        this.fscBleCentralApi = fscBleCentralApi;
        this.workTimer = mTimer;
        this.mWorkTask = task;
        this.mainTimer = mainTimer;
        this.mainTask = mainTask;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, DataBean bean) {
        ivIcon = holder.getView(R.id.icon);
        tvTitle = holder.getView(R.id.tv_title);
        tvTips = holder.getView(R.id.tips);
        tvTitle.setText(bean.getTitle());
        ivIcon.setImageResource(bean.getIcon());
        if (bean.isFlag()) {
            ivIcon.setColorFilter(android.R.color.transparent);
            tvTitle.setTextColor(getContext().getResources().getColor(R.color.white));
        } else {
            ivIcon.setColorFilter(R.color.alpha_gray);
            tvTitle.setTextColor(getContext().getResources().getColor(R.color.grey_home));
        }
        tvTitle.setTextColor(getContext().getResources().getColor(R.color.grey_home));
        tvTips.setTextColor(getContext().getResources().getColor(R.color.grey_home));
        if (bean.getTips() != null) {
            tvTips.setText(bean.getTips());
        } else {
            tvTips.setText("");
        }
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != 7 || holder.getAdapterPosition() != 8 || holder.getAdapterPosition() != 9 || holder.getAdapterPosition() != 10) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }
                    }
                    if (bean.isFlag()) {
                        bean.setFlag(false);
                        ivIcon.setColorFilter(R.color.alpha_gray);
                        tvTitle.setTextColor(getContext().getResources().getColor(R.color.grey_home));
                        tvTips.setTextColor(getContext().getResources().getColor(R.color.grey_home));
                        mediaPlayer = MediaPlayer.create(getContext(), R.raw.ar_close);
                    } else {
                        ivIcon.setColorFilter(android.R.color.transparent);
                        bean.setFlag(true);
                        tvTitle.setTextColor(getContext().getResources().getColor(R.color.white));
                        tvTips.setTextColor(getContext().getResources().getColor(R.color.white));
                        mediaPlayer = MediaPlayer.create(getContext(), R.raw.ar_open);
                    }
                    mediaPlayer.start();
                    stopWorkThread();
                    stopMainThread();
                    switch (holder.getAdapterPosition()) {
                        case 0: //总开关
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.MAIN_SWITCH_ON);
                            } else {
                                bean.setControl(ConfigData.MAIN_SWITCH_OFF);
                            }
                            break;
                        case 1: //大灯
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.BIG_LIGHT_ON);
                            } else {
                                bean.setControl(ConfigData.BIG_LIGHT_OFF);
                            }
                            break;
                        case 2: //日行灯
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.DAYTIME_RUNNING_LIGHT_ON);
                            } else {
                                bean.setControl(ConfigData.DAYTIME_RUNNING_LIGHT_OFF);
                            }
                            break;
                        case 3://左转向灯
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.LEFT_LIGHT_ON);
                            } else {
                                bean.setControl(ConfigData.LEFT_LIGHT_OFF);
                            }
                            break;
                        case 4://右转向灯
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.RIGHT_LIGHT_ON);
                            } else {
                                bean.setControl(ConfigData.RIGHT_LIGHT_OFF);
                            }
                            break;
                        case 5://刹车灯
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.STOP_LIGHT_ON);
                            } else {
                                bean.setControl(ConfigData.STOP_LIGHT_OFF);
                            }
                            break;
                        case 6://倒车灯
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.BACK_CAR_LIGHT_ON);
                            } else {
                                bean.setControl(ConfigData.BACK_CAR_LIGHT_OFF);
                            }
                            break;

                        case 11://内饰按键灯
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.INTERIOR_BUTTON_LIGHT_ON);
                            } else {
                                bean.setControl(ConfigData.INTERIOR_BUTTON_LIGHT_OFF);
                            }
                            break;
                        case 12://内饰氛围灯
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.INTERIOR_AMBIENT_LIGHT_ON);
                            } else {
                                bean.setControl(ConfigData.INTERIOR_AMBIENT_LIGHT_OFF);
                            }
                            break;
                        case 13://手球氛围灯
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.HANDBALL_ATMOSPHERE_LIGHT_ON);
                            } else {
                                bean.setControl(ConfigData.HANDBALL_ATMOSPHERE_LIGHT_OFF);
                            }
                            break;
                        case 14://主屏
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.MAIN_SCREEN_ON);
                            } else {
                                bean.setControl(ConfigData.MAIN_SCREEN_OFF);
                            }
                            break;
                        case 15://预留接口
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.RESERVED_INTERFACE_ON);
                            } else {
                                bean.setControl(ConfigData.RESERVED_INTERFACE_OFF);
                            }
                            break;
                        case 16:
                            if (bean.isFlag()) {
                                bean.setControl(ConfigData.RESERVED_INTERFACE_2_ON);
                            } else {
                                bean.setControl(ConfigData.RESERVED_INTERFACE_2_OFF);
                            }
                            ToastUtil.show(getContext(), "正在研发，莫急~");
                            break;
                        case 17:
                             ToastUtil.show(getContext(), "正在研发，莫急~");
                            BottomMusicDialog d = new BottomMusicDialog(activity);
                            d.showDialog();
                            break;
                    }
                    if (!TextUtils.isEmpty(bean.getControl()) && !"".equals(bean.getControl())) {
                        currentPackge = FileUtil.hexToByte(bean.getControl());
                        fscBleCentralApi.send(currentPackge);
                        data.set(holder.getAdapterPosition(), bean);
                        updateData(data);

                        strSend = bean.getControl();
                        receiveBean.setSendInfo(strSend);
                        receiveBean.setReceiveInfo("");
                        receiveBean.setCurrentPackge(currentPackge);
                        Event<ReceiveBean> event = new Event<ReceiveBean>(RECEIVE_DATA, receiveBean);
                        EventBusUtil.sendEvent(event);
                    }
                }
            }
        });

        ivIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (holder.getAdapterPosition() == 7) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            updateAddOrSubtract(holder.getAdapterPosition());
                            setIvTvColor(holder, android.R.color.transparent, R.color.white);
                            break;
                        case MotionEvent.ACTION_UP:
                            LatteLogger.d("getAdapterPosition()", "抬起");
                            stopAddOrSubtract(holder.getAdapterPosition());
                            setIvTvColor(holder, R.color.alpha_gray, R.color.grey_home);
                            break;
                    }
                } else if (holder.getAdapterPosition() == 8) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            updateAddOrSubtract(holder.getAdapterPosition());
                            setIvTvColor(holder, android.R.color.transparent, R.color.white);
                            break;
                        case MotionEvent.ACTION_UP:
                            LatteLogger.d("getAdapterPosition()", "抬起");
                            stopAddOrSubtract(holder.getAdapterPosition());
                            setIvTvColor(holder, R.color.alpha_gray, R.color.grey_home);
                            break;
                    }
                } else if (holder.getAdapterPosition() == 9) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            updateAddOrSubtract(holder.getAdapterPosition());
                            setIvTvColor(holder, android.R.color.transparent, R.color.white);
                            break;
                        case MotionEvent.ACTION_UP:
                            LatteLogger.d("getAdapterPosition()", "抬起");
                            stopAddOrSubtract(holder.getAdapterPosition());
                            setIvTvColor(holder, R.color.alpha_gray, R.color.grey_home);
                            break;
                    }
                } else if (holder.getAdapterPosition() == 10) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            updateAddOrSubtract(holder.getAdapterPosition());
                            setIvTvColor(holder, android.R.color.transparent, R.color.white);
                            break;
                        case MotionEvent.ACTION_UP:
                            LatteLogger.d("getAdapterPosition()", "抬起");
                            stopAddOrSubtract(holder.getAdapterPosition());
                            setIvTvColor(holder, R.color.alpha_gray, R.color.grey_home);
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void stopWorkThread(){
        if (workTimer != null) {
            workTimer.cancel();
        }
        if (mWorkTask != null) {
            mWorkTask.cancel();
        }
    }

    public void stopMainThread(){
        if (mainTimer != null) {
            mainTimer.cancel();
        }
        if (mainTask != null) {
            mainTask.cancel();
        }
    }

    private void setIvTvColor(@NotNull BaseViewHolder holder, int transparent, int p) {
        ((ImageView) holder.getView(R.id.icon)).setColorFilter(transparent);
        ((TextView) holder.getView(R.id.tv_title)).setTextColor(getContext().getResources().getColor(p));
    }

    private void updateAddOrSubtract(int viewId) {
        stopMainThread();
        final int vid = viewId;
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = vid;
                handler.sendMessage(msg);
            }
        }, ConfigParam.SendTime, ConfigParam.SendTime, TimeUnit.MILLISECONDS);    //每间隔100ms发送Message
    }

    public void stopAddOrSubtract(int vid) {
        stopMainThread();
        if (vid == 7 || vid == 8) {
            data.get(vid).setControl(ConfigData.MAIN_SEAT_STOP);
        } else if (vid == 9 || vid == 10) {
            data.get(vid).setControl(ConfigData.FONT_SEAT_STOP);
        }
        if (data.get(vid).getControl() != null) {
            currentPackge = FileUtil.hexToByte(data.get(vid).getControl());
            fscBleCentralApi.send(currentPackge);
            bean.setCurrentPackge(currentPackge);
            bean.setSendInfo(data.get(vid).getControl());
            Event<ReceiveBean> event = new Event<ReceiveBean>(RECEIVE_DATA, bean);
            EventBusUtil.sendEvent(event);
        }
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
        notifyDataSetChanged();
    }
}







