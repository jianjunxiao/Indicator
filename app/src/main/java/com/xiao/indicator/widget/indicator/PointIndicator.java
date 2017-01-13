package com.xiao.indicator.widget.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.xiao.indicator.R;

/**
 * 圆点指示器
 */
public class PointIndicator extends View implements OnPageScrollListener {

    private Context mContext;

    private int mNormalColor;
    private int mSelectColor;
    private int mPointSize;
    private int mPointSpace;

    private Paint mNormalPaint;
    private Paint mSelectPaint;

    private int mCount;
    private int enterPosition;
    private int leavePosition;
    private float percent;

    public PointIndicator(Context context) {
        this(context, null);
    }

    public PointIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mNormalPaint = new Paint();
        mNormalPaint.setColor(mNormalColor);
        mNormalPaint.setAntiAlias(true);

        mSelectPaint = new Paint();
        mSelectPaint.setColor(mSelectColor);
        mSelectPaint.setAntiAlias(true);

        mCount = 4;
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mContext = context;

        mNormalColor = 0x66cccccc;
        mSelectColor = 0xfffdd63b;
        mPointSize = dp2px(3f);
        mPointSpace = dp2px(3f);

        // 自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PointIndicator);
        mNormalColor = ta.getColor(R.styleable.PointIndicator_normal_color, mNormalColor);
        mSelectColor = ta.getColor(R.styleable.PointIndicator_select_color, mSelectColor);
        mPointSize = (int) ta.getDimension(R.styleable.PointIndicator_point_size, mPointSize);
        mPointSpace = (int) ta.getDimension(R.styleable.PointIndicator_point_space, mPointSpace);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int size = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                size = specSize;
                break;
            case MeasureSpec.AT_MOST:
                size = mCount * mPointSize + (mCount - 1) * mPointSpace;
                break;
        }
        return size;
    }

    private int measureHeight(int measureSpec) {
        int size = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                size = specSize;
                break;
            case MeasureSpec.AT_MOST:
                size = mPointSize;
                break;
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制normalPoint
        drawNormalPoint(canvas);
        // 绘制selectPoint
        drawSelectPoint(canvas);

    }

    private void drawSelectPoint(Canvas canvas) {
        float cx;
        if (enterPosition > leavePosition) {
            cx = (leavePosition + 0.5f) * mPointSize
                    + leavePosition * mPointSpace
                    + (mPointSize + mPointSpace) * percent;
        } else {
            cx = (leavePosition + 0.5f) * mPointSize
                    + leavePosition * mPointSpace
                    - (mPointSize + mPointSpace) * percent;
        }
        float cy = getHeight() / 2;
        float radius = mPointSize / 2f;
        canvas.drawCircle(cx, cy, radius, mSelectPaint);
    }

    private void drawNormalPoint(Canvas canvas) {
        for (int i = 0; i < mCount; i++) {
            float cx = mPointSize / 2f + (mPointSize + mPointSpace) * i;
            float cy = getHeight() / 2;
            float radius = mPointSize / 2f;
            canvas.drawCircle(cx, cy, radius, mNormalPaint);
        }
    }

    public void bindViewPager(ViewPager viewPager) {
        if (viewPager != null) {
            if (viewPager.getAdapter() != null) {
                mCount = viewPager.getAdapter().getCount();
                new ViewPagerHelper().bindScrollListener(viewPager, this);
                requestLayout(); // 绑定ViewPager后指示器重新布局，因为指示器的数量和宽度可能有变化
            }
        }
    }

    @Override
    public void onPageScroll(int enterPosition, int leavePosition, float percent) {
        this.enterPosition = enterPosition;
        this.leavePosition = leavePosition;
        this.percent = percent;
        postInvalidate();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue,
                mContext.getResources().getDisplayMetrics());
    }

}
