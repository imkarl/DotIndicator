package com.im4j.dotindicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 线条指示器
 */
public class LineIndicator extends View {

    // default value
    private final int DEFAULT_INDICATOR_RADIUS = 10;
    private final int DEFAULT_INDICATOR_MARGIN = 30;
    private final int DEFAULT_INDICATOR_BACKGROUND = Color.LTGRAY;
    private final int DEFAULT_INDICATOR_SELECTED_BACKGROUND = Color.WHITE;
    private final int DEFAULT_INDICATOR_LAYOUT_GRAVITY = Gravity.CENTER|Gravity.BOTTOM;

    private ViewPager viewPager;
    private List<ShapeHolder> tabItems;

    //config list
    private int mCurItemPosition;
    private int mTargetItemPosition;
    private float mCurItemPositionOffset;
    private float mIndicatorRadius;
    private float mIndicatorMargin;
    private int mIndicatorBackground;
    private int mIndicatorSelectedBackground;
    private int mIndicatorLayoutGravity;

    private ViewPager.SimpleOnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int count = viewPager.getAdapter().getCount();
            mTargetItemPosition = mCurItemPosition==position ? mCurItemPosition+1 : mCurItemPosition-1;
            if (mTargetItemPosition == count) {
                mTargetItemPosition = 0;
            } else if (mTargetItemPosition == -1) {
                mTargetItemPosition = count-1;
            }
            mCurItemPositionOffset = positionOffset;
            trigger();
        }
        @Override
        public void onPageSelected(int position) {
            mTargetItemPosition = position;
            mCurItemPositionOffset = 0;
            trigger();
            mCurItemPosition = position;
        }
    };


    public LineIndicator(Context context) {
        super(context);
        init(context);
    }
    public LineIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public LineIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        tabItems = new ArrayList<>();

        mIndicatorRadius = DEFAULT_INDICATOR_RADIUS;
        mIndicatorMargin = DEFAULT_INDICATOR_MARGIN;
        mIndicatorBackground = DEFAULT_INDICATOR_BACKGROUND;
        mIndicatorSelectedBackground = DEFAULT_INDICATOR_SELECTED_BACKGROUND;
        mIndicatorLayoutGravity = DEFAULT_INDICATOR_LAYOUT_GRAVITY;
    }

    public void setViewPager(final ViewPager viewPager){
        if (this.viewPager != null) {
            this.viewPager.removeOnPageChangeListener(mOnPageChangeListener);
        }
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(mOnPageChangeListener);

        this.viewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                recreateTabItems();
            }
            @Override
            public void onInvalidated() {
                super.onInvalidated();
                recreateTabItems();
            }
        });

        recreateTabItems();
    }

    private void trigger(){
        requestLayout();
        invalidate();
    }
    private void recreateTabItems() {
        tabItems.clear();
        for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
            // 外部矩形弧度
            float[] outerR = new float[] { 8, 8, 8, 8, 8, 8, 8, 8 };
            Shape circle = new RoundRectShape(outerR, null, null);
            ShapeDrawable drawable = new ShapeDrawable(circle);
            ShapeHolder shapeHolder = new ShapeHolder(drawable);
            Paint paint = drawable.getPaint();
            paint.setColor(mIndicatorBackground);
            paint.setAntiAlias(true);
            shapeHolder.setPaint(paint);
            tabItems.add(shapeHolder);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int width = getWidth();
        final int height = getHeight();
        layoutTabItems(width, height);
    }

    private void layoutTabItems(final int containerWidth,final int containerHeight){
        if(tabItems == null){
            throw new IllegalStateException("forget to create tabItems?");
        }
        final float yCoordinate = containerHeight - mIndicatorRadius*2f;
        final float startPosition = startDrawPosition(containerWidth);

        int currentPosition = mCurItemPosition;
        int targetPosition = mTargetItemPosition;
        float currentPositionOffset = mCurItemPositionOffset;

        //Log.e("[layoutTabItems]", "currentPosition="+currentPosition+"  targetPosition="+targetPosition);

        float startX = startPosition;
        for(int i=0;i<tabItems.size();i++){
            float offset = 2 * mIndicatorRadius * 3;

            float width = 2 * mIndicatorRadius;
            float x = startX + (mIndicatorMargin + mIndicatorRadius*2)*i;

            ShapeHolder item = tabItems.get(i);
            item.setColor(mIndicatorBackground);
            if (i == currentPosition) {
                if ((targetPosition == 0 && currentPosition == tabItems.size() - 1)
                        || (targetPosition == tabItems.size() - 1 && currentPosition == 0)) {
                    offset = offset * currentPositionOffset;
                } else if (targetPosition > currentPosition) {
                    offset = offset * (1 - currentPositionOffset);
                } else {
                    offset = offset * currentPositionOffset;
                }
                width = width + offset;
                startX += offset;
            } else if (i == targetPosition) {
                if ((targetPosition == 0 && currentPosition == tabItems.size() - 1)
                        || (targetPosition == tabItems.size() - 1 && currentPosition == 0)) {
                    offset = offset * (1 - currentPositionOffset);
                } else if (targetPosition > currentPosition) {
                    offset = offset * currentPositionOffset;
                } else {
                    offset = offset * (1 - currentPositionOffset);
                }
                width = width + offset;
                startX += offset;
            }

            if (width > 3 * mIndicatorRadius) {
                item.setColor(mIndicatorSelectedBackground);
            }

            item.resizeShape(width, 2 * mIndicatorRadius);
            item.setX(x);
            item.setY(yCoordinate - mIndicatorRadius);
        }

    }
    private float startDrawPosition(final int containerWidth){
        if(mIndicatorLayoutGravity == Gravity.LEFT) {
            return 0;
        }

        float tabItemsLength = tabItems.size()*(2* mIndicatorRadius + mIndicatorMargin)- mIndicatorMargin;
        if(containerWidth < tabItemsLength) {
            return 0;
        }

        if(mIndicatorLayoutGravity == Gravity.RIGHT){
            return containerWidth - tabItemsLength;
        }
        return (containerWidth - tabItemsLength)/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,
                Canvas.MATRIX_SAVE_FLAG |
                        Canvas.CLIP_SAVE_FLAG |
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        for(ShapeHolder item : tabItems){
            canvas.save();
            canvas.translate(item.getX(),item.getY());
            item.getShape().draw(canvas);
            canvas.restore();
        }
        canvas.restoreToCount(sc);
    }

    public void setIndicatorRadius(float mIndicatorRadius) {
        this.mIndicatorRadius = mIndicatorRadius;
    }

    public void setIndicatorMargin(float mIndicatorMargin) {
        this.mIndicatorMargin = mIndicatorMargin;
    }

    public void setIndicatorBackground(int color) {
        this.mIndicatorBackground = color;
    }
    public void setIndicatorBackgroundResource(int colorResId) {
        this.mIndicatorBackground = getResources().getColor(colorResId);
    }

    public void setIndicatorSelectedBackground(int color) {
        this.mIndicatorSelectedBackground = color;
    }
    public void setIndicatorSelectedBackgroundResource(int colorResId) {
        this.mIndicatorSelectedBackground = getResources().getColor(colorResId);
    }

    public void setIndicatorLayoutGravity(int gravity) {
        this.mIndicatorLayoutGravity = gravity;
    }

}
