package com.shenzhen.honpe.carbluetoothmanage.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.controler.FscBleCentralCallbacksImp;
import com.feasycom.util.FileUtil;
import com.feasycom.util.ToastUtil;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.shenzhen.honpe.carbluetoothmanage.DataBean;
import com.shenzhen.honpe.carbluetoothmanage.R;
import com.shenzhen.honpe.carbluetoothmanage.adapter.CarLightAdapter;
import com.shenzhen.honpe.carbluetoothmanage.base.BaseActivity;
import com.shenzhen.honpe.carbluetoothmanage.config.ConfigData;
import com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam;
import com.shenzhen.honpe.carbluetoothmanage.config.ReceiveBean;
import com.shenzhen.honpe.carbluetoothmanage.util.DBUtils;
import com.shenzhen.honpe.carbluetoothmanage.util.LatteLogger;
import com.shenzhen.honpe.carbluetoothmanage.util.ProgressUtils;
import com.shenzhen.honpe.carbluetoothmanage.util.event.Event;
import com.shenzhen.honpe.carbluetoothmanage.util.event.EventBusUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam.MainParam;
import static com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam.RECEIVE_DATA;
import static com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam.SendTime;
import static com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam.TIMER_TASK;
import static com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam.TabName;
import static com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam.address;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.iv_circle_headIcon)
    CircleImageView ivCircleHead;

    CarLightAdapter carLightAdapter;
    private String[] titles;
    private List<DataBean> listBean = new ArrayList<>();
    private int[] icons = {R.mipmap.iv_1, R.mipmap.iv_2, R.mipmap.iv_3, R.mipmap.iv_4, R.mipmap.iv_5, R.mipmap.iv_18, R.mipmap.iv_6, R.mipmap.iv_7,
            R.mipmap.iv_8, R.mipmap.iv_9, R.mipmap.iv_10, R.mipmap.iv_12, R.mipmap.iv_13, R.mipmap.iv_14, R.mipmap.iv_11};
    BluetoothDeviceWrapper deviceWrapper;
    byte[] currentPackge, mainPackage;
    String strAddress, strName, strSend; //最后一次操作时的指令
    ReceiveBean bean = new ReceiveBean();
    public Timer workTimer = new Timer();
    public MyTask mWorkTask = new MyTask();
    public Timer mainThread;
    public MainTask mainTask;
    CountDownTimer countDownTimer;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        deviceWrapper = (BluetoothDeviceWrapper) getIntent().getSerializableExtra("device");
        if (deviceWrapper != null) {
            strAddress = deviceWrapper.getAddress();
            strName = deviceWrapper.getName();
        } else {
            strName = (String) DBUtils.get(_mActivity, TabName, ConfigParam.deviceName, "");
            strAddress = (String) DBUtils.get(_mActivity, TabName, ConfigParam.address, "");
        }

        LatteLogger.d("testDevice",  strName);

        setCallBacks();

        fscBleCentralApi.connect(strAddress);
        mainPackage = FileUtil.hexToByte(ConfigData.IS_THREAD);

        ProgressUtils.disLoadView(_mActivity, 1, strName + "连接中...");
        //设置共同沉浸式样式
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        initData();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        carLightAdapter = new CarLightAdapter(listBean, fscBleCentralApi, workTimer, mWorkTask, mainThread, mainTask,_mActivity);
        mRecyclerView.setAdapter(carLightAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == recyclerView.SCROLL_STATE_IDLE || newState == recyclerView.SCROLL_STATE_DRAGGING) {//正在滚动
                    for (int i = 7; i <= 10; i++) {
                        carLightAdapter.stopAddOrSubtract(recyclerView.getAdapter().getItemViewType(i));
                    }
                }
            }
        });
    }

    private void initData() {
        titles = getResources().getStringArray(R.array.carBluetoothTitle);
        for (int i = 0; i < icons.length; i++) {
            DataBean dataBean = new DataBean();
            dataBean.setIcon(icons[i]);
            dataBean.setTitle(titles[i]);
            dataBean.setFlag(false);
            listBean.add(dataBean);
        }
    }

    private void setCallBacks() {
        fscBleCentralApi.setCallbacks(new FscBleCentralCallbacksImp() {
            @Override
            public void packetReceived(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch,
                                       String strValue, String hexString, byte[] rawValue, String timestamp) {
//                Log.e("TAG", "packetReceived: " + hexString.toUpperCase());
//                bean.setSendInfo(strSend);
//                bean.setReceiveInfo(hexString.toUpperCase());
//                Event<ReceiveBean> event = new Event<ReceiveBean>(RECEIVE_DATA, bean);
//                EventBusUtil.sendEvent(event);
            }

            @Override
            public void blePeripheralConnected(BluetoothGatt gatt, BluetoothDevice device) {
                Log.e("TAG", "blePeripheralConnected: " + Thread.currentThread().getName() + "    " + device.getBondState());
                runOnUiThread(() -> {
                    mainThread = new Timer();
                    mainTask = new MainTask();
                    mainThread.schedule(mainTask, MainParam, MainParam);
                    ProgressUtils.disLoadView(_mActivity, 0, strName + getResources().getString(R.string.connected));
                    tvStatus.setText(strName + getResources().getString(R.string.connected));
                    countDownTimer = new CountDownTimer(3000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            ivCircleHead.setVisibility(View.VISIBLE);
                            tvStatus.setText("红品控车系统");
                            countDownTimer.cancel();
                        }
                    };
                    countDownTimer.start();

                });
            }

            @Override
            public void blePeripheralDisonnected(BluetoothGatt gatt, BluetoothDevice device) {

                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        ivCircleHead.setVisibility(View.GONE);
                        tvStatus.setText(strName + getResources().getString(R.string.disconnected));
                        DBUtils.put(_mActivity, TabName, ConfigParam.deviceName, "");
                        DBUtils.put(_mActivity, TabName, ConfigParam.address, "");
//                        countDownTimer = new CountDownTimer(3000, 1000) {
//                            @Override
//                            public void onTick(long millisUntilFinished) {
//
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                startActivity(new Intent(_mActivity, SearchDeviceActivity.class));
//                                finish();
//                                countDownTimer.cancel();
//                            }
//                        };
//                        countDownTimer.start();
                        setCallBacks();
                        fscBleCentralApi.connect(address);
                    }
                });
            }
        });
    }


    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    private String strReceive;

    @Override
    public void onEventBusCome(Event event) {
        switch (event.getCode()) {
            case RECEIVE_DATA:
                bean = (ReceiveBean) event.getData();
                strSend = bean.getSendInfo();
                strReceive = bean.getReceiveInfo();
                if (TextUtils.isEmpty(strReceive)) strReceive = "";
                if (bean.getCurrentPackge() != null) {
                    currentPackge = bean.getCurrentPackge();
                }
                LatteLogger.d("testBean", strSend + "   " + strReceive + "   " + FileUtil.bytesToHex(currentPackge, currentPackge.length));
                if (strReceive.equals(ConfigData.WRONG)) {
                    ToastUtil.show(_mActivity, "接收到错误码：" + ConfigData.WRONG);
                    LatteLogger.d("strReceive", "1");
                    stopMainThread();
                    stopWorkThread();
                } else if (strReceive.equals(ConfigData.RIGHT)) {
                    LatteLogger.d("strReceive", "2");
                    stopMainThread();
                    stopWorkThread();
                } else if (strReceive.equals(ConfigData.IS_THREAD)) {
                    LatteLogger.d("strReceive", "3");
                    stopMainThread();
                    mainThread = new Timer();
                    mainTask = new MainTask();
                    mainThread.schedule(mainTask, MainParam, MainParam);
                } else if (!TextUtils.isEmpty(strSend) && TextUtils.isEmpty(strReceive)) { //发送不为空，接收为空时
                    LatteLogger.d("strReceive", "4");
                    stopMainThread();
                    stopWorkThread();
                    workTimer = new Timer();
                    mWorkTask = new MyTask();
                    workTimer.schedule(mWorkTask, SendTime, SendTime);
                }
                if (bean != null) {
                    bean.setSendInfo("");
                    bean.setReceiveInfo("");
                }
                break;
            case TIMER_TASK:
                //   ToastUtil.show(_mActivity, "老邓我已经发了六次给你，你还是没有收到！");
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        fscBleCentralApi.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!fscBleCentralApi.isConnected()) {
            fscBleCentralApi.connect(strAddress);
            setCallBacks();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        ProgressUtils.ProgressDismiss();
    }


    public class MyTask extends TimerTask {
        int i = 0;

        @Override
        public void run() {
            i++;
            fscBleCentralApi.send(currentPackge);
            LatteLogger.d("repeatTask", FileUtil.bytesToHex(currentPackge, currentPackge.length));
            if (i == 6) {
                i = 0;
                workTimer.cancel();
                mWorkTask.cancel();
                EventBusUtil.sendEvent(new Event(TIMER_TASK));
            }
        }
    }

    public class MainTask extends TimerTask {
        @Override
        public void run() {
            fscBleCentralApi.send(mainPackage);
        }
    }

    public void stopWorkThread() {
        if (workTimer != null) {
            workTimer.cancel();
        }
        if (mWorkTask != null) {
            mWorkTask.cancel();
        }
    }

    public void stopMainThread() {
        if (mainThread != null) {
            mainThread.cancel();
        }
        if (mainTask != null) {
            mainTask.cancel();
        }
    }
}










