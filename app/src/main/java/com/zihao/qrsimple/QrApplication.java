package com.zihao.qrsimple;

import android.app.Application;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

/**
 * ClassName：QrApplication
 * Description：TODO<Application>
 * Author：zihao
 * Date：2017/9/11 14:55
 * Version：v1.0
 */
public class QrApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ZXingLibrary.initDisplayOpinion(this);// 初始化二维码扫描工具
    }
}
