package com.shenzhen.honpe.carbluetoothmanage.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.feasycom.controler.FscBleCentralApi;
import com.feasycom.controler.FscBleCentralApiImp;
import com.gyf.immersionbar.ImmersionBar;
import com.shenzhen.honpe.carbluetoothmanage.R;
import com.shenzhen.honpe.carbluetoothmanage.util.ScreenAdapterUtils;
import com.shenzhen.honpe.carbluetoothmanage.util.event.Event;
import com.shenzhen.honpe.carbluetoothmanage.util.event.EventBusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * FileName: BaseActivity
 * Author: asus
 * Date: 2021/5/22 14:56
 * Description:
 */
public abstract class BaseActivity extends AppCompatActivity {
    public Context mContext;
    private Unbinder unbinder;
    public AppCompatActivity _mActivity;
    public FscBleCentralApi fscBleCentralApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ScreenAdapterUtils.getPhoneScreen(this);
        // 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
//        RxPermissionsTool.with(this)
//                .addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                .addPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
//                .addPermission(Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
//                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .initPermission();
        // 已经申请过权限，做想做的事
        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
        setContentView(getLayoutResource());
        unbinder = ButterKnife.bind(this);
        View titleBar = findViewById(setTitleBar());
        ImmersionBar.setTitleBar(this, titleBar);
        View statusBarView = findViewById(setStatusBarView());
        ImmersionBar.setStatusBarView(this, statusBarView);
        if (isImmersionBarEnabled()) {
            //初始化沉浸式
            initImmersionBar();
        } else {
            //设置共同沉浸式样式
            ImmersionBar.with(this).statusBarColor(R.color.color_nb).navigationBarColor(R.color.color_nb).init();
        }
        this.initBluetooth();

        mContext = this;
        _mActivity = this;
        this.initPresenter();
        this.initView();
        this.setListener();
    }

    private void initBluetooth() {
        fscBleCentralApi = FscBleCentralApiImp.getInstance(this);
        fscBleCentralApi.initialize();
    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    protected int setTitleBar() {
        return R.id.toolbar;
    }

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {

    }

    protected int setStatusBarView() {
        return 0;
    }
    //=============================================

    protected boolean isRegisterEventBus() {
        return false;
    }





    /**
     * 设置监听
     */
    protected void setListener() {

    }

    //获取布局文件
    protected abstract int getLayoutResource();

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    protected void initPresenter() {
    }

    //初始化view
    protected abstract void initView();


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusCome(Event event) {
        if (event != null) {
            receiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(Event event) {
        if (event != null) {
            receiveStickyEvent(event);
        }
    }

    /**
     * 接收到分发的粘性事件
     *
     * @param event
     */
    protected void receiveStickyEvent(Event event) {

    }

    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    protected void receiveEvent(Event event) {

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
        unbinder.unbind();
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}



































