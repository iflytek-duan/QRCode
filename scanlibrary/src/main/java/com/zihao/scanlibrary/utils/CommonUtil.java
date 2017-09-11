package com.zihao.scanlibrary.utils;

import android.hardware.Camera;

/**
 * ClassName：CommonUtil
 * Description：TODO<通用工具类>
 * Author：zihao
 * Date：2017/9/11 14:08
 * Version：v1.0
 */
public class CommonUtil {

    /**
     * 检查摄像头是否可用
     *
     * @return true:可用；false，不可用。
     */
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse && mCamera != null) {
            mCamera.release();
        }
        return canUse;
    }

}
