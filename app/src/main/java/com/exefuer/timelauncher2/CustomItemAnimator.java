package com.exefuer.timelauncher2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class CustomItemAnimator extends SimpleItemAnimator {

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        View view = holder.itemView;
        view.setAlpha(0f);
        view.setTranslationY(50f);

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 50f, 0f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        alphaAnimator.setDuration(300);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchAddStarting(holder);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAddFinished(holder);
            }
        });

        animator.start();
        alphaAnimator.start();

        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        View view = holder.itemView;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", fromY - toY, 0);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchMoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchMoveFinished(holder);
            }
        });
        animator.start();
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
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
}