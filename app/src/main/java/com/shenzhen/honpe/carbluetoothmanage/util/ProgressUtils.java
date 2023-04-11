package com.shenzhen.honpe.carbluetoothmanage.util;

import android.content.Context;

import com.feasycom.util.ToastUtil;
import com.kaopiz.kprogresshud.KProgressHUD;


/**
 * @ProjectName: Honpe
 * @CreateDate: 2020/7/6 12:30
 * @Author: 李熙祥
 * @Description: java类作用描述 IOS进度工具
 */
public class ProgressUtils {
    private static KProgressHUD hud;

    /**
     * 显示加载中
     * status 0 表示  不显示
     **/

    public static void disLoadView(Context context, int status, String tips) {

        if (hud != null && status == 0) {
            ToastUtil.show(context,tips);
            hud.dismiss();
        } else if (status == 1) {
            ProgressDismiss();
            hud = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(false)
                    .setLabel(tips);
            try {
                hud.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void ProgressDismiss(){
        if (hud != null) {
            hud.dismiss();
        }
    }
}
