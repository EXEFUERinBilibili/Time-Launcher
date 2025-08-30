package com.exefuer.timelauncher2;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        adjustDensity(this);
    }

    public static void adjustDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();

        // 统一设置为 320dpi (xhdpi)
        int targetDensityDpi = 320;
        float targetDensity = targetDensityDpi / (float) DisplayMetrics.DENSITY_DEFAULT;

        // 计算缩放比例
        float scale = context.getResources().getDisplayMetrics().density;
        float targetScale = targetDensity;

        // 更新显示指标
        displayMetrics.density = targetScale;
        displayMetrics.densityDpi = targetDensityDpi;
        displayMetrics.scaledDensity = targetScale;

        // 更新配置
        configuration.densityDpi = targetDensityDpi;

        // 应用新配置
        resources.updateConfiguration(configuration, displayMetrics);
    }
}