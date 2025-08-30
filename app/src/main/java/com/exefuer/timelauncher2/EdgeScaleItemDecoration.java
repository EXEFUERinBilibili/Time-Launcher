package com.exefuer.timelauncher2;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EdgeScaleItemDecoration extends RecyclerView.ItemDecoration {
    private final float minScale;
    private final int triggerDistance;

    public EdgeScaleItemDecoration(float minScale, int triggerDistanceDp) {
        this.minScale = minScale;
        this.triggerDistance = (int) (triggerDistanceDp *
                android.content.res.Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // 为缩放留出空间，防止内容被裁剪
        int padding = (int) (view.getHeight() * (1 - minScale) / 2);
        outRect.top = padding;
        outRect.bottom = padding;
    }

    @Override
    public void onDrawOver(@NonNull android.graphics.Canvas c, @NonNull RecyclerView parent,
                           @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        int lastVisible = layoutManager.findLastVisibleItemPosition();

        for (int i = firstVisible; i <= lastVisible; i++) {
            View child = layoutManager.findViewByPosition(i);
            if (child == null) continue;

            // 计算视图在RecyclerView中的位置
            int viewTop = child.getTop();
            int viewBottom = child.getBottom();
            int recyclerHeight = parent.getHeight();

            // 计算距离顶部和底部的距离
            int distanceFromTop = viewTop;
            int distanceFromBottom = recyclerHeight - viewBottom;

            // 确定缩放比例
            float scale = 1f;
            if (distanceFromTop < triggerDistance) {
                scale = minScale + (1f - minScale) * (distanceFromTop / (float)triggerDistance);
            } else if (distanceFromBottom < triggerDistance) {
                scale = minScale + (1f - minScale) * (distanceFromBottom / (float)triggerDistance);
            }

            // 应用动画
            animateScale(child, scale);
        }
    }

    private void animateScale(View view, float scale) {
        if (view.getScaleX() != scale || view.getScaleY() != scale) {
            view.animate()
                    .scaleX(scale)
                    .scaleY(scale)
                    .setDuration(100)
                    .start();
        }
    }
}