# QRCode
基于ZXing的二维码扫描.
---
# 使用步骤
## 添加依赖库
1. 在app/build.gradle中添加zxing精简库
`compile 'cn.yipianfengye.android:zxing-library:2.2'`
2. 在AndroidManifest.xml中添加如下权限：
```java
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.FLASHLIGHT" />

    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />

    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.INTERNET" />
```
## 开始扫描
1. 在Application.onCreate进行工具初始化操作
`ZXingLibrary.initDisplayOpinion(this);// 初始化二维码扫描工具`
2. 使用如下代码进入扫描界面：
```java
    // 打开默认二维码扫描界面
    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
    startActivityForResult(intent, REQUEST_CODE);
```
同时，在onActivityResult中添加如下代码接收扫描结果：
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 处理二维码扫描结果
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
```
## 自定义扫描界面
1. 新建一个Activity，这里命名为ScanActivity，其布局`activity_scan.xml`代码为（`scan_container`用于控制真正的扫描界面出现的位置、区域）：
```java
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="vertical">

    <FrameLayout
        android:id="@+id/scan_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <Button
        android:layout_width="240dp"
        android:layout_height="120dp"
        android:layout_alignParentTop="true"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="10dp"
        android:gravity="center"
        android:text="这里是条形码配图区域"/>

    <Button
        android:id="@+id/scan_btn_cancel"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="10dp"
        android:layout_marginLeft="20dp"
        android:layout_marginRight="20dp"
        android:layout_marginTop="20dp"
        android:text="@string/scan_cancel"/>

    <TextView
        android:layout_width="160dp"
        android:layout_height="wrap_content"
        android:layout_above="@id/scan_btn_cancel"
        android:layout_centerHorizontal="true"
        android:gravity="center"
        android:paddingBottom="6dp"
        android:paddingTop="6dp"
        android:text="@string/scan_tips_text"/>
</RelativeLayout>
```
2. 设计自定义的扫描区域布局，`capture_carmera.xml`布局代码如下：
```java
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
             xmlns:app="http://schemas.android.com/apk/res-auto"
             android:layout_width="fill_parent"
             android:layout_height="fill_parent">

    <SurfaceView
        android:id="@+id/preview_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <com.uuzuche.lib_zxing.view.ViewfinderView
        android:id="@+id/viewfinder_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:inner_corner_color="@color/scan_corner_color"
        app:inner_corner_length="30dp"
        app:inner_corner_width="5dp"
        app:inner_height="200dp"
        app:inner_margintop="150dp"
        app:inner_scan_bitmap="@drawable/scan_image"
        app:inner_scan_iscircle="false"
        app:inner_scan_speed="10"
        app:inner_width="200dp"/>

</FrameLayout>
```
3. 在Activity.onCreate进行布局初始化并通过replaceCaptureFragment()方法来更换默认的扫描界面：
```java
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initView();
    }
    
    private void initView() {
        findViewById(R.id.scan_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanActivity.this.finish();
            }
        });

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
```
4. 添加扫描回调并通知调用`ScanActivity`的Activity：
```java
    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }
    };
```
## 其它功能设置
1. **打开/关闭闪光灯**
```java
CodeUtils.isLightEnable(true);// 打开闪光灯
CodeUtils.isLightEnable(false);// 关闭闪光灯
```
2. **更换扫描提示音**
在res目录下新建名为raw的文件夹，然后将提示音文件命名为beep.ogg拷贝到res/raw下即可。