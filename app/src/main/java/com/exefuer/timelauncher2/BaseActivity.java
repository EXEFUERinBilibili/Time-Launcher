package com.exefuer.timelauncher2;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        // 应用密度调整
        Context context = adjustDensity(newBase);
        super.attachBaseContext(context);
    }

    private Context adjustDensity(Context context) {
        // 获取原始资源
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();

        // 计算目标密度 (320dpi)
        int targetDensityDpi = 320;
        float targetDensity = targetDensityDpi / (float) DisplayMetrics.DENSITY_DEFAULT;

        // 更新显示指标
        DisplayMetrics newMetrics = new DisplayMetrics();
        newMetrics.setTo(displayMetrics);
        newMetrics.density = targetDensity;
        newMetrics.densityDpi = targetDensityDpi;
        newMetrics.scaledDensity = targetDensity;

        // 创建新配置
        Configuration newConfig = new Configuration(configuration);
        newConfig.densityDpi = targetDensityDpi;

        // 创建新上下文
        Context newContext = context.createConfigurationContext(newConfig);
        newContext.getResources().updateConfiguration(newConfig, newMetrics);

        return newContext;
    }
}