package com.exefuer.timelauncher2;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BounceRecyclerView extends RecyclerView {

    private static final float OVERSCROLL_DAMPING = 0.5f;
    private static final float BOUNCE_BACK_FACTOR = 0.3f;
    private static final int BOUNCE_DURATION = 400;
    private static final float OVERSHOOT_TENSION = 1.5f;

    private float lastY;
    private boolean isOverScrolling = false;
    private int overScrollAmount = 0;

    public BounceRecyclerView(Context context) {
        super(context);
    }

    public BounceRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BounceRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = e.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaY = e.getY() - lastY;
                lastY = e.getY();

                if (!canScrollVertically((int) -deltaY)) {
                    // 计算回弹效果
                    overScrollAmount = (int) (deltaY * OVERSCROLL_DAMPING);
                    scrollBy(0, -overScrollAmount);
                    isOverScrolling = true;
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isOverScrolling) {
                    // 添加回弹动画
                    animate()
                            .translationY(0)
                            .setDuration(BOUNCE_DURATION)
                            .setInterpolator(new OvershootInterpolator(OVERSHOOT_TENSION))
                            .start();

                    isOverScrolling = false;
                    overScrollAmount = 0;
                }
                break;
        }

        return super.onTouchEvent(e);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        // 滚动到边界时添加回弹效果
        if (!canScrollVertically(1) && dy > 0) {
            // 滚动到底部
            bounceBackBottom();
        } else if (!canScrollVertically(-1) && dy < 0) {
            // 滚动到顶部
            bounceBackTop();
        }
    }

    private void bounceBackBottom() {
        animate()
                .translationYBy(-30)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> animate()
                        .translationY(0)
                        .setDuration(300)
                        .setInterpolator(new OvershootInterpolator(1.2f))
                        .start());
    }

    private void bounceBackTop() {
        animate()
                .translationYBy(30)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> animate()
                        .translationY(0)
                        .setDuration(300)
                        .setInterpolator(new OvershootInterpolator(1.2f))
                        .start());
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        // 添加fling时的回弹效果
        float reducedVelocityY = velocityY * 0.8f;
        return super.fling(velocityX, (int) reducedVelocityY);
    }
}