package com.exefuer.timelauncher2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 设置初始页面为主时间屏(位置1)
        viewPager.setCurrentItem(1, false);

        // 可选: 禁用用户滑动(如需)
        // viewPager.setUserInputEnabled(false);
    }


    @Override
    public void onBackPressed() {
        // 完全不做任何操作，即可屏蔽返回键
    }
}