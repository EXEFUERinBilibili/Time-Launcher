package com.exefuer.timelauncher2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import androidx.appcompat.app.AppCompatActivity;

public class AppListStyleActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "AppListPrefs";
    public static final String STYLE_KEY = "list_style";
    public static final int STYLE_GRID = 0;
    public static final int STYLE_LIST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list_style);

        RadioGroup styleRadioGroup = findViewById(R.id.style_radio_group);

        // 加载当前选择的样式
        int currentStyle = getCurrentStyle(this);
        if (currentStyle == STYLE_LIST) {
            styleRadioGroup.check(R.id.radio_list);
        } else {
            styleRadioGroup.check(R.id.radio_grid);
        }

        // 设置单选按钮选择监听器
        styleRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int style = STYLE_GRID; // 默认宫格样式

                if (checkedId == R.id.radio_list) {
                    style = STYLE_LIST;
                }

                // 保存到SharedPreferences
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().putInt(STYLE_KEY, style).apply();

                // 立即返回
                finish();
            }
        });
    }

    public static int getCurrentStyle(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(STYLE_KEY, STYLE_GRID); // 默认宫格样式
    }
}