package com.zihao.qrsimple.scan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zihao.qrsimple.R;

/**
 * ClassName：ScanActivity
 * Description：TODO<二维码/条形码扫描界面>
 * Author：zihao
 * Date：2017/9/19 16:43
 * Version：v1.0
 */
public class ScanActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String QR_PREFIX = "06";// 条形码前缀

    private Button btnLight;
    private boolean isEnableLight = false;// 标识是否打开闪光灯，默认为否

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initView();
    }

    private void initView() {
        btnLight = (Button) findViewById(R.id.scan_btn_light);

        findViewById(R.id.scan_btn_cancel).setOnClickListener(this);
        btnLight.setOnClickListener(this);

        replaceCaptureFragment();
    }

    /**
     * 替换扫描Fragment
     */
    private void replaceCaptureFragment() {
        // 执行扫面Fragment的初始化操作
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.capture_camera);
        // 添加解析回调
        captureFragment.setAnalyzeCallback(analyzeCallback);
        // 替换我们的扫描控件
        getSupportFragmentManager().beginTransaction().replace(R.id.scan_container, captureFragment).commit();
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();

            if (result.startsWith(QR_PREFIX)) {// 如果包含"06"前缀，则表示扫描成功
                bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
                bundle.putString(CodeUtils.RESULT_STRING, result);
                ZXingLibrary.getInstance().setPlayBeep(true);
                ZXingLibrary.getInstance().setVibrate(false);// 成功时关闭震动效果
            } else {// 不包含"06"前缀表明格式不正确
                bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
                bundle.putString(CodeUtils.RESULT_STRING, "条形码格式不正确");
                ZXingLibrary.getInstance().setPlayBeep(false);// 失败时关闭提示音
                ZXingLibrary.getInstance().setVibrate(true);
            }

            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "解析条形码失败");
            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_btn_cancel:// 取消
                ScanActivity.this.finish();
                break;
            case R.id.scan_btn_light:// 灯光控制
                isEnableLight = !isEnableLight;
                CodeUtils.isLightEnable(isEnableLight);
                btnLight.setText(isEnableLight ? R.string.scan_close_light : R.string.scan_open_light);
                break;
        }
    }
}
