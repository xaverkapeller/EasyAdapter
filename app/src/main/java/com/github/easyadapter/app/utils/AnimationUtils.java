package com.github.easyadapter.app.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/02/15
 */
public class AnimationUtils {

    public interface AnimationBuilder {
        public Animator height(int height);
        public Animator width(int width);
    }

    public static abstract class AnimatorEndListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public static AnimationBuilder animate(View view) {
        return new AnimationBuilderImpl(view);
    }

    private static class AnimationBuilderImpl implements AnimationBuilder {

        private final View mView;

        private AnimationBuilderImpl(View view) {
            mView = view;
        }

        @Override
        public Animator height(int height) {
            ValueAnimator animator = ValueAnimator.ofInt(mView.getLayoutParams().height, height);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    mView.requestLayout();
                }
            });
            return animator;
        }

        @Override
        public Animator width(int width) {
            ValueAnimator animator = ValueAnimator.ofInt(mView.getLayoutParams().width, width);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mView.getLayoutParams().width = (Integer) animation.getAnimatedValue();
                    mView.requestLayout();
                }
            });
            return animator;
        }
    }
}
