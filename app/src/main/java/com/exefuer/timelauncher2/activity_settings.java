package com.exefuer.timelauncher2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class activity_settings extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "app_hiding";
    private static final String HIDE_FEATURE_ENABLED = "hide_feature_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 关于设置
        findViewById(R.id.card_settings_about).setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });

        findViewById(R.id.card_settings_app_list_style).setOnClickListener(v -> {
            startActivity(new Intent(this, AppListStyleActivity.class));
        });



    }
}