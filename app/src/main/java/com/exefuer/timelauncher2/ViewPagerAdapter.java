package com.exefuer.timelauncher2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.exefuer.timelauncher2.WatchFaceFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LeftFragment(); // 负一屏
            case 1:
                return new WatchFaceFragment();     // 主时间屏
            case 2:
                return new SecondFragment();  // 第二屏
            default:
                return new WatchFaceFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 现在有三个页面
    }
}