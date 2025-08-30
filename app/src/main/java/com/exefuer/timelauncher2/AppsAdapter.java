package com.exefuer.timelauncher2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    private List<AppInfo> appsList;
    private final PackageManager packageManager;
    private final Context context;

    public AppsAdapter(List<AppInfo> appsList, PackageManager packageManager, Context context) {
        this.appsList = appsList;
        this.packageManager = packageManager;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 根据当前样式选择布局
        int layoutRes = AppListStyleActivity.getCurrentStyle(context) == AppListStyleActivity.STYLE_LIST ?
                R.layout.item_app_list : R.layout.item_app_grid;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo appInfo = appsList.get(position);

        holder.appName.setText(appInfo.getName());
        holder.appIcon.setImageDrawable(appInfo.getIcon());

        holder.itemView.setOnClickListener(v -> {
            // 添加点击动画
            animateClick(v, () -> {
                Intent launchIntent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
                if (launchIntent != null) {
                    context.startActivity(launchIntent);
                }
            });
        });

        // 长按拖拽排序
        holder.itemView.setOnLongClickListener(v -> {
            // 这里可以添加震动反馈
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    public void moveItem(int fromPosition, int toPosition) {
        AppInfo movedItem = appsList.remove(fromPosition);
        appsList.add(toPosition, movedItem);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void animateClick(View view, Runnable action) {
        // 缩放动画
        view.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction(action)
                            .start();
                })
                .start();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView appIcon;
        public TextView appName;

        public ViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
        }
    }
}