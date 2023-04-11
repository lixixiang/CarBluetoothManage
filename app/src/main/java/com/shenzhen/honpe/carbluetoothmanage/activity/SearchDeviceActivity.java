package com.shenzhen.honpe.carbluetoothmanage.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.feasycom.bean.BluetoothDeviceWrapper;
import com.feasycom.controler.FscBleCentralApi;
import com.feasycom.controler.FscBleCentralApiImp;
import com.feasycom.controler.FscBleCentralCallbacksImp;
import com.feasycom.util.ToastUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.shenzhen.honpe.carbluetoothmanage.R;
import com.shenzhen.honpe.carbluetoothmanage.adapter.SearchDeviceListAdapter;
import com.shenzhen.honpe.carbluetoothmanage.base.BaseActivity;
import com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam;
import com.shenzhen.honpe.carbluetoothmanage.util.DBUtils;
import com.shenzhen.honpe.carbluetoothmanage.util.LatteLogger;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import me.jessyan.autosize.internal.CustomAdapt;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.shenzhen.honpe.carbluetoothmanage.config.ConfigParam.TabName;

/**
 * FileName: ConnectBle
 * Author: asus
 * Date: 2021/5/27 17:49
 * Description:
 */
public class SearchDeviceActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, EasyPermissions.PermissionCallbacks , CustomAdapt {

    @BindView(R.id.recyclerView)
    RecyclerView devicesRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private final int ENABLE_BT_REQUEST_ID = 2;
    List<BluetoothDeviceWrapper> deviceQueue = new LinkedList<BluetoothDeviceWrapper>();
    public SearchDeviceListAdapter adapter;
    public String strName = "", strAddress = "";

    String[] PERMS = {Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.ACCESS_COARSE_LOCATION
            , Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search_device;
    }

    @Override
    protected void initView() {

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (deviceQueue != null) {
                    deviceQueue.clear();
                }
                Log.e("TAG", "initView: 开始扫描1");
                stopAllScan();
                startScan();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        showDevicesList();
    }



    private void showDevicesList() {
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchDeviceListAdapter(deviceQueue);
        devicesRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                stopAllScan();
                BluetoothDeviceWrapper wrapper = (BluetoothDeviceWrapper) adapter.getItem(position);
                ToastUtil.show(_mActivity, wrapper.getName());
                DBUtils.put(_mActivity, TabName, ConfigParam.deviceName, wrapper.getName());
                DBUtils.put(_mActivity, TabName, ConfigParam.address, wrapper.getAddress());
                IntentMethod(wrapper);
            }
        });
    }

    private void IntentMethod(BluetoothDeviceWrapper wrapper) {
        Intent intent = new Intent(_mActivity, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("device", wrapper);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    public void stopAllScan() {
        fscBleCentralApi.stopScan();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 扫描
     */
    private void startScan() {
        if (fscBleCentralApi.isBtEnabled() == false) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
        }
        if (!fscBleCentralApi.checkBleHardwareAvailable()) {
            ToastUtil.show(_mActivity, "is not support ble");
        }
        fscBleCentralApi.startScan(30000);
    }


    @AfterPermissionGranted(10001)
    public void onPermissionSuccess() {
        Toast.makeText(this, "AfterPermission调用成功了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            _mActivity.finish();
        }
        return true;
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout.isRefreshing()) {
                    deviceQueue.clear();
                    adapter.notifyDataSetChanged();
                    Log.e("TAG", "initView: 开始扫描1");
                    stopAllScan();
                    startScan();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initPermission();
        strName = (String) DBUtils.get(_mActivity, TabName, ConfigParam.deviceName, "");
        strAddress = (String) DBUtils.get(_mActivity, TabName, ConfigParam.address,"");
        if (fscBleCentralApi != null && "".equals(strName)&& "".equals(strAddress)) {
            setCallBacks();
        }
        if (!"".equals(strName) && !"".equals(strAddress)){
            Intent intent = new Intent(_mActivity, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fscBleCentralApi.stopScan();
    }

    String PERMISSION_STORAGE_MSG = "请授予权限，否则影响部分使用功能";
    int PERMISSION_STORAGE_CODE = 10001;

    private void initPermission() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            /**
             *@param host Context对象
             *@param rationale  权限弹窗上的提示语。
             *@param requestCode 请求权限的唯一标识码
             *@param perms 一系列权限
             */
            EasyPermissions.requestPermissions(this, PERMISSION_STORAGE_MSG, PERMISSION_STORAGE_CODE, PERMS);
        }
    }

    private void setCallBacks() {
        fscBleCentralApi.setCallbacks(new FscBleCentralCallbacksImp() {
            @Override
            public void startScan() {
                ToastUtil.show(getApplicationContext(), getString(R.string.str_scan));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.setTitle("蓝牙搜索中...");
                    }
                });
            }

            @Override
            public void stopScan() {
                ToastUtil.show(getApplicationContext(), getString(R.string.str_stop_scan));
                swipeRefreshLayout.setRefreshing(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.setTitle("蓝牙搜索停止扫描");
                    }
                });
            }

            @Override
            public void blePeripheralFound(BluetoothDeviceWrapper device, int rssi, byte[] record) {
                if (rssi == 127) {
                    return;
                }

                if (!TextUtils.isEmpty(device.getName())) {
                    int i = 0;
                    for (; i < deviceQueue.size(); i++) {
                        if (device.getName().contains(deviceQueue.get(i).getName())) {
                            deviceQueue.get(i).setName(device.getName());
                            deviceQueue.get(i).setRssi(device.getRssi());
                            deviceQueue.get(i).setAdvData(device.getAdvData());
                            break;
                        }
                    }
                    if (i >= deviceQueue.size()) {
                        deviceQueue.add(device);
                        LatteLogger.d("deviceQueue", i + "   " + deviceQueue.size() + "   " + device.getName());
//                        if (device.getName().contains("HONPE_BT")){
//                            DBUtils.put(_mActivity, TabName, ConfigParam.deviceName, device.getName());
//                            DBUtils.put(_mActivity, TabName, ConfigParam.address, device.getAddress());
//                            IntentMethod(device);
                        adapter.addDevice(deviceQueue);
//                        }
                    }
                }
            }
        });
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this).statusBarColor(R.color.color_nb).navigationBarColor(R.color.color_nb).init();
    }

    /**
     * 申请成功时调用
     *
     * @param requestCode 请求权限的唯一标识码
     * @param perms       一系列权限
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LatteLogger.d("testonPermissionsGranted", "申请成功时调用");
        if (fscBleCentralApi != null)
            stopAllScan();
        startScan();
        setCallBacks();

    }

    /**
     * 申请拒绝时调用
     *
     * @param requestCode 请求权限的唯一标识码
     * @param perms       一系列权限
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    /**
     * 是否按照宽度进行等比例适配 (为了保证在高宽比不同的屏幕上也能正常适配, 所以只能在宽度和高度之中选一个作为基准进行适配)
     *
     * @return {@code true} 为按照宽度适配, {@code false} 为按照高度适配
     */
    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    /**
     * 返回设计图上的设计尺寸, 单位 dp
     * {@link #getSizeInDp} 须配合 {@link #isBaseOnWidth()} 使用, 规则如下:
     * 如果 {@link #isBaseOnWidth()} 返回 {@code true}, {@link #getSizeInDp} 则应该返回设计图的总宽度
     * 如果 {@link #isBaseOnWidth()} 返回 {@code false}, {@link #getSizeInDp} 则应该返回设计图的总高度
     * 如果您不需要自定义设计图上的设计尺寸, 想继续使用在 AndroidManifest 中填写的设计图尺寸, {@link #getSizeInDp} 则返回 {@code 0}
     *
     * @return 设计图上的设计尺寸, 单位 dp
     */
    @Override
    public float getSizeInDp() {
        return 720;
    }
}


