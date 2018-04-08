package com.swifty.fillcolor.ads;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swifty.fillcolor.R;

import cn.waps.AppConnect;
import cn.waps.AppListener;
import cn.waps.UpdatePointsListener;

public class DemoApp extends Activity implements View.OnClickListener, UpdatePointsListener {

    private TextView pointsTextView;
    private TextView SDKVersionView;

    private String displayPointsText;

    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_main);

        findViewById(R.id.popAdButton).setOnClickListener(this);

        pointsTextView = (TextView) findViewById(R.id.PointsTextView);
        SDKVersionView = (TextView) findViewById(R.id.SDKVersionView);

        // 设置插屏广告展示时，可使用设备的back键进行关闭
        // 设置为true表示可通过back键关闭，不调用该句代码则使用默认值false
        AppConnect.getInstance(this).setPopAdBack(true);

        // 带有默认参数值的在线配置，使用此方法，程序第一次启动使用的是"defaultValue"，之后再启动则是使用的服务器端返回的参数值
        String showAd = AppConnect.getInstance(this).getConfig("showAd", "defaultValue");

        SDKVersionView.setText("在线参数:showAd = " + showAd);

        SDKVersionView.setText(SDKVersionView.getText() + "\nSDK版本: " + AppConnect.LIBRARY_VERSION_NUMBER);

        // 设置互动广告无数据时的回调监听（该方法必须在showBannerAd之前调用）
        AppConnect.getInstance(this).setBannerAdNoDataListener(new AppListener() {

            @Override
            public void onBannerNoData() {
                Log.i("debug", "banner广告暂无可用数据");
            }

        });
        // 互动广告调用方式
        LinearLayout layout = (LinearLayout) this.findViewById(R.id.AdLinearLayout);
        AppConnect.getInstance(this).showBannerAd(this, layout);

        // 迷你广告调用方式
        // AppConnect.getInstance(this).setAdBackColor(Color.argb(50, 120, 240,
        // 120));//设置迷你广告背景颜色
        // AppConnect.getInstance(this).setAdForeColor(Color.YELLOW);//设置迷你广告文字颜色
        LinearLayout miniLayout = (LinearLayout) findViewById(R.id.miniAdLinearLayout);
        AppConnect.getInstance(this).showMiniAd(this, miniLayout, 20);// 20秒刷新一次
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 调用退屏广告
            QuitPopAd.getInstance().show(this);
        }
        return true;
    }

    // 建议加入onConfigurationChanged回调方法
    // 注:如果当前Activity没有设置android:configChanges属性,或者是固定横屏或竖屏模式,则不需要加入
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 横竖屏状态切换时,关闭处于打开状态中的退屏广告
        QuitPopAd.getInstance().close();
        super.onConfigurationChanged(newConfig);
    }

    public void onClick(View v) {
        if (v instanceof Button) {
            switch (v.getId()) {
                case R.id.popAdButton:
                    // 设置插屏广告无数据时的回调监听（该方法必须在showPopAd之前调用）
                    AppConnect.getInstance(this).setPopAdNoDataListener(new AppListener() {

                        @Override
                        public void onPopNoData() {
                            Log.i("debug", "插屏广告暂无可用数据");
                        }

                    });
                    // 显示插屏广告
                    AppConnect.getInstance(this).showPopAd(this);
                    break;
//                AppConnect.getInstance(this).spendPoints(10, this);
//                AppConnect.getInstance(this).awardPoints(10, this);
            }
        }
    }

    @Override
    protected void onResume() {
        // 从服务器端获取当前用户的虚拟货币.
        // 返回结果在回调函数getUpdatePoints(...)中处理
        AppConnect.getInstance(this).getPoints(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 释放资源，原finalize()方法名修改为close()
        AppConnect.getInstance(this).close();
        super.onDestroy();
    }

    /**
     * 用于监听插屏广告的显示与关闭
     */
    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Dialog dialog = AppConnect.getInstance(this).getPopAdDialog();
        if (dialog != null) {
            if (dialog.isShowing()) {
                // 插屏广告正在显示
            }
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // 监听插屏广告关闭事件
                }
            });
        }
    }*/

    // 创建一个线程
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            if (pointsTextView != null) {
                pointsTextView.setText(displayPointsText);
            }
        }
    };

    /**
     * AppConnect.getPoints()方法的实现，必须实现
     *
     * @param currencyName 虚拟货币名称.
     * @param pointTotal   虚拟货币余额.
     */
    public void getUpdatePoints(String currencyName, int pointTotal) {
        displayPointsText = currencyName + ": " + pointTotal;
        mHandler.post(mUpdateResults);
    }

    /**
     * AppConnect.getPoints() 方法的实现，必须实现
     *
     * @param error 请求失败的错误信息
     */
    public void getUpdatePointsFailed(String error) {
        displayPointsText = error;
        mHandler.post(mUpdateResults);
    }
}