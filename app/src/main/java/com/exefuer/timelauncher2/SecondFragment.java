package com.exefuer.timelauncher2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {

    private RecyclerView appsRecyclerView;
    private AppsAdapter adapter;
    private List<AppInfo> appList = new ArrayList<>();
    private BroadcastReceiver packageChangedReceiver;
    private boolean firstLoad = true;
    private int currentStyle = AppListStyleActivity.STYLE_GRID;

    // 拖拽排序回调
    private ItemTouchHelper.Callback touchCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            int fromPos = viewHolder.getAdapterPosition();
            int toPos = target.getAdapterPosition();
            adapter.moveItem(fromPos, toPos);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // 不处理侧滑
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                // 拖拽开始时放大项目
                if (viewHolder != null) {
                    viewHolder.itemView.animate()
                            .scaleX(1.05f)
                            .scaleY(1.05f)
                            .setDuration(200)
                            .start();
                }
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            // 拖拽结束时恢复项目大小
            viewHolder.itemView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start();
        }
    };

    private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appsRecyclerView = view.findViewById(R.id.appsRecyclerView);

        // 加载当前样式设置
        currentStyle = AppListStyleActivity.getCurrentStyle(requireContext());

        // 初始化布局管理器
        setupLayoutManager();

        // 加载应用列表
        loadApps();

        // 注册应用变化广播接收器
        registerPackageReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();

        // 检查样式设置是否变化
        int newStyle = AppListStyleActivity.getCurrentStyle(requireContext());
        if (newStyle != currentStyle) {
            currentStyle = newStyle;
            setupLayoutManager();
            // 创建新的适配器以强制刷新视图
            adapter = new AppsAdapter(appList, getActivity().getPackageManager(), requireContext());
            appsRecyclerView.setAdapter(adapter);
            playListAnimation();
        } else if (!firstLoad) {
            playListAnimation();
        }
        firstLoad = false;
    }

    private void setupLayoutManager() {
        // 根据设置选择布局管理器
        if (currentStyle == AppListStyleActivity.STYLE_LIST) {
            // 列表样式 - 使用线性布局管理器
            appsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            // 宫格样式 - 使用网格布局管理器 (3列)
            appsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }

        itemTouchHelper.attachToRecyclerView(appsRecyclerView);
        appsRecyclerView.setItemAnimator(new CustomItemAnimator());
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfoList = requireActivity().getPackageManager()
                .queryIntentActivities(mainIntent, 0);

        appList.clear();

        // 过滤掉自身应用
        String ownPackageName = requireContext().getPackageName();

        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (!packageName.equals(ownPackageName)) {
                String appName = resolveInfo.loadLabel(getActivity().getPackageManager()).toString();
                Drawable icon = resolveInfo.loadIcon(getActivity().getPackageManager());
                appList.add(new AppInfo(appName, packageName, icon));
            }
        }

        adapter = new AppsAdapter(appList, getActivity().getPackageManager(), requireContext());
        appsRecyclerView.setAdapter(adapter);

        // 播放列表动画
        playListAnimation();
    }

    private void playListAnimation() {
        // 设置列表入场动画
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
                getContext(), R.anim.layout_animation_fade_in);
        appsRecyclerView.setLayoutAnimation(animation);

        // 启动动画
        appsRecyclerView.scheduleLayoutAnimation();
    }

    private void registerPackageReceiver() {
        // 应用安装/卸载广播接收器
        packageChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == null) return;

                switch (intent.getAction()) {
                    case Intent.ACTION_PACKAGE_ADDED:
                        String addedPackage = intent.getData().getSchemeSpecificPart();
                        addApp(addedPackage);
                        break;
                    case Intent.ACTION_PACKAGE_REMOVED:
                        String removedPackage = intent.getData().getSchemeSpecificPart();
                        removeApp(removedPackage);
                        break;
                    case Intent.ACTION_PACKAGE_CHANGED:
                        String changedPackage = intent.getData().getSchemeSpecificPart();
                        updateApp(changedPackage);
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        requireActivity().registerReceiver(packageChangedReceiver, filter);
    }

    private void addApp(String packageName) {
        try {
            PackageManager pm = requireActivity().getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                ResolveInfo resolveInfo = pm.resolveActivity(launchIntent, 0);
                if (resolveInfo != null) {
                    String appName = resolveInfo.loadLabel(pm).toString();
                    Drawable icon = resolveInfo.loadIcon(pm);

                    // 添加到列表开头
                    appList.add(0, new AppInfo(appName, packageName, icon));

                    // 添加动画
                    adapter.notifyItemInserted(0);
                    appsRecyclerView.smoothScrollToPosition(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeApp(String packageName) {
        for (int i = 0; i < appList.size(); i++) {
            if (appList.get(i).getPackageName().equals(packageName)) {
                appList.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    private void updateApp(String packageName) {
        for (int i = 0; i < appList.size(); i++) {
            AppInfo app = appList.get(i);
            if (app.getPackageName().equals(packageName)) {
                try {
                    PackageManager pm = requireActivity().getPackageManager();
                    Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
                    if (launchIntent != null) {
                        ResolveInfo resolveInfo = pm.resolveActivity(launchIntent, 0);
                        if (resolveInfo != null) {
                            String appName = resolveInfo.loadLabel(pm).toString();
                            Drawable icon = resolveInfo.loadIcon(pm);

                            // 更新应用信息
                            appList.set(i, new AppInfo(appName, packageName, icon));
                            adapter.notifyItemChanged(i);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 取消注册广播接收器
        if (packageChangedReceiver != null) {
            requireActivity().unregisterReceiver(packageChangedReceiver);
        }
    }

    // 自定义ItemAnimator实现
    private static class CustomItemAnimator extends RecyclerView.ItemAnimator {
        private final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

        @Override
        public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override
        public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
            View view = viewHolder.itemView;
            view.setAlpha(0f);
            view.setTranslationY(50f);

            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 50f, 0f);
            animator.setDuration(300);
            animator.setInterpolator(interpolator);

            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            alphaAnimator.setDuration(300);

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    dispatchAnimationStarted(viewHolder);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    dispatchAnimationFinished(viewHolder);
                }
            });

            animator.start();
            alphaAnimator.start();

            return false;
        }

        @Override
        public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override
        public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
            return false;
        }

        @Override
        public void runPendingAnimations() {
            // 不需要实现
        }

        @Override
        public void endAnimation(RecyclerView.ViewHolder item) {
            // 不需要实现
        }

        @Override
        public void endAnimations() {
            // 不需要实现
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        private static Context getDisplayContext(Context context) {
            Context displayContext = context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                DisplayManager displayManager = (DisplayManager) TimeLauncherApp.getContext().getSystemService(Context.DISPLAY_SERVICE);
                Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY); // 获取默认显示
                displayContext = context.createDisplayContext(display);

            }
            return displayContext;
        }

        private static Context getFitDisplayContext(Context old) {
            Context newContext = old;

            float density = (float) old.getResources().getDisplayMetrics().widthPixels / 320;
            try {
                Configuration configuration = old.getResources().getConfiguration();
                configuration.smallestScreenWidthDp = 320;
                configuration.densityDpi = (int) (320 * density);
                newContext = old.createConfigurationContext(configuration);
            } catch (Exception e) {
                Toast.makeText(newContext, "调整缩放失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return newContext;
        }
    }
}