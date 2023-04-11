package com.shenzhen.honpe.carbluetoothmanage.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.feasycom.bean.BluetoothDeviceWrapper;
import com.shenzhen.honpe.carbluetoothmanage.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * FileName: SearchDeviceListAdapter
 * Author: asus
 * Date: 2021/5/22 16:02
 * Description:
 */
@SuppressLint("SetTextI18n")
public class SearchDeviceListAdapter extends BaseQuickAdapter<BluetoothDeviceWrapper, BaseViewHolder> {
    private List<BluetoothDeviceWrapper> mDevices;
    TextView tvName,tvAddress,deviceMode,tvRssi;
    ProgressBar pbRssi;
    LinearLayout deviceView;
    boolean filterRssiSwitch,filterNameSwitch;
    int filterRssi;
    String filterName;
    private Boolean isClick = false;
    private Handler handler = new Handler(Looper.myLooper());
    public SearchDeviceListAdapter(@Nullable List<BluetoothDeviceWrapper> data) {
        super(R.layout.search_device_info, data);
        mDevices = data;
//        filterRssiSwitch = (boolean) DBUtils.get(getContext(),ConfigDB.DB_TAB_NAME, FILTER_RSSI_SWITCH, false);
//        filterRssi = (int) DBUtils.get(getContext(), ConfigDB.DB_TAB_NAME, FILTER_VALUE, -100);
//        filterNameSwitch = (boolean) DBUtils.get(getContext(), ConfigDB.DB_TAB_NAME, FILTER_NAME_SWITCH, false);
//        filterName = (String) DBUtils.get(getContext(), ConfigDB.DB_TAB_NAME, FILTER_NAME, "");
    }

    public void addDevice(List<BluetoothDeviceWrapper> deviceDetail){
        mDevices = deviceDetail;
        notifyDataSetChanged();
    }

    public void sort() {
        for (int i=0; i < mDevices.size() - 1; i++) {
            for (int j = 0; j < mDevices.size() - 1 - i; j++) {
                if (mDevices.get(j).getRssi() < mDevices.get(j + 1).getRssi() && mDevices.get(j).getBondState() != BluetoothDevice.BOND_BONDED && mDevices.get(j+1).getBondState() != BluetoothDevice.BOND_BONDED) {
                    BluetoothDeviceWrapper bd = mDevices.get(j);
                    mDevices.set(j, mDevices.get(j + 1));
                    mDevices.set(j + 1, bd);
                }
            }
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, BluetoothDeviceWrapper deviceDetail) {
        tvName = holder.getView(R.id.tv_name);
        tvAddress = holder.getView(R.id.tv_addr);
        deviceMode = holder.getView(R.id.device_mode);
        pbRssi = holder.getView(R.id.pb_rssi);
        tvRssi = holder.getView(R.id.tv_rssi);
        deviceView = holder.getView(R.id.device_view);
        String deviceName = deviceDetail.getName();
        String deviceAddress = deviceDetail.getAddress();
        int deviceRssi = deviceDetail.getRssi().intValue();
        if (deviceName != null && deviceName.length() > 0) {
            //设备名长度限制,最大30
            if (deviceName.length() >= 30) {
                deviceName = deviceName.substring(0, 30);
            }
            if (deviceDetail.getBondState() == BluetoothDevice.BOND_BONDED) {
                tvName.setText(getContext().getString(R.string.paired)  + deviceName);
            } else {
                tvName.setText(deviceName);
            }
        } else {
            tvName.setText("unKnow");
        }
        if (deviceAddress != null && deviceAddress.length() > 0) {
            tvAddress.setText("("+deviceAddress+")");
        } else {
            tvAddress.setText("(unKnow)");
        }
        if (deviceRssi <= -100) {
            deviceRssi = -1;
        } else if (deviceRssi > 0) {
            deviceRssi = 0;
        }
        String str_rssi = "(" + deviceRssi + ")";
        if (str_rssi.equals("(-100)")) {
            str_rssi = "null";
        }
        deviceMode.setText(deviceDetail.getModel());
        pbRssi.setProgress(100 + deviceRssi);
        tvRssi.setText(getContext().getString(R.string.rssi)+"("+deviceDetail.getRssi().toString()+")");
        deviceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isClick = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isClick = false;
                        break;
                }
                return false;
            }
        });
    }
}



