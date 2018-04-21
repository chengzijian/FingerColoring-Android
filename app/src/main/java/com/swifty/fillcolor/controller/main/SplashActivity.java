package com.swifty.fillcolor.controller.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.swifty.fillcolor.R;
import com.swifty.fillcolor.ads.DemoApp;

/**
 * 开屏广告演示窗口
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        String tag = getIntent().getStringExtra("show_tag");
        if (!TextUtils.isEmpty(tag)) {
            final Intent intent = new Intent(this, DemoApp.class);
            startActivity(intent);
            this.finish();
        } else {
            final Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}
