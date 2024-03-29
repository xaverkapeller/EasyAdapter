package com.github.easyadapter.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Basic implementation of {@link android.support.v7.widget.RecyclerView.ItemDecoration} which draws
 * a divider between items.
 *
 * @author Xaver Kapeller
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    public enum Orientation {
        VERTICAL,
        HORIZONTAL
    }

    private final Drawable mDivider;
    private final Orientation mOrientation;

    /**
     * Creates a new instance of {@link DividerItemDecoration}. A {@link DividerItemDecoration}
     * constructed this way will use the divider {@link Drawable} defined in the style used by the
     * passed in {@link Context}.
     *
     * @param context     Your current {@link Context}.
     * @param orientation The orientation of your {@link RecyclerView}.
     */
    public DividerItemDecoration(Context context, Orientation orientation) {
        final TypedArray a = context.obtainStyledAttributes(new int[]{
                android.R.attr.listDivider
        });
        mDivider = a.getDrawable(0);
        a.recycle();

        mOrientation = orientation;
    }

    /**
     * Creates a new instance of {@link DividerItemDecoration}.
     *
     * @param drawable    The {@link Drawable} used as divider.
     * @param orientation The orientation of your {@link RecyclerView}.
     */
    public DividerItemDecoration(Drawable drawable, Orientation orientation) {
        mDivider = drawable;
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        switch (mOrientation) {

            case VERTICAL:
                drawVertical(c, parent);
                break;

            case HORIZONTAL:
                drawHorizontal(c, parent);
                break;

            default:
                break;
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        switch (mOrientation) {

            case VERTICAL:
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                break;

            case HORIZONTAL:
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
                break;

            default:
                break;
        }
    }
}
