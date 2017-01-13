package com.xiao.indicator.widget.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.xiao.indicator.R;

/**
 * 数字指示器
 */
public class NumberIndicater extends View implements OnPageScrollListener {

    private Context mContext;

    private int mCircleColor;
    private int mCircleSize;
    private int mNumberColor;
    private int mNumberSize;

    private int mCount;
    private int mCurrent;

    private Paint mCirclePaint;
    private Paint mTextPaint;

    private float offset;       // 页面偏移百分比
    private boolean isUp;       // 指示器数字是否向上滑动

    public NumberIndicater(Context context) {
        this(context, null);
    }

    public NumberIndicater(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberIndicater(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        mContext = context;
        mCircleSize = dp2px(48f);
        mCircleColor = 0xfffdd63b;
        mNumberSize = sp2px(14f);
        mNumberColor = 0xff353535;
        // 自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NumberIndicater);
        mCircleColor = ta.getColor(R.styleable.NumberIndicater_circle_color, mCircleColor);
        mCircleSize = (int) ta.getDimension(R.styleable.NumberIndicater_circle_size, mCircleSize);
        mNumberColor = ta.getColor(R.styleable.NumberIndicater_number_color, mNumberColor);
        mNumberSize = (int) ta.getDimension(R.styleable.NumberIndicater_number_size, mNumberSize);
        ta.recycle();
    }

    private void initPaint() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(mNumberColor);
        mTextPaint.setTextSize(mNumberSize);

        offset = 1;
        mCount = 3;
        mCurrent = 1;
        isUp = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置测量后的尺寸
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int measureSpec) {
        int size = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                size = specSize;
                break;
            case MeasureSpec.AT_MOST:
                size = mCircleSize;
                break;
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制圆形底图
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, mCircleSize / 2f, mCirclePaint);
        // 绘制分割线
        drawSplit(canvas);
        // 绘制右边总数数字
        drawTotleNumber(canvas);
        // 绘制左边指示数字
        drawIndicatNumber(canvas);
    }

    private void drawSplit(Canvas canvas) {
        String text = "/";
        float width = mTextPaint.measureText(text);
        float x = (getWidth() - width) / 2f;
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float y = getHeight() / 2f + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2f;
        // x为绘制文本左边缘距离X轴的距离，y为绘制文本基线距离Y轴的位置
        canvas.drawText(text, x, y, mTextPaint);
    }

    private void drawTotleNumber(Canvas canvas) {
        String text = String.valueOf(mCount);
        float x = getWidth() / 2f + mTextPaint.measureText("/") / 2f + 3;
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float y = getHeight() / 2f + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2f;
        canvas.drawText(text, x, y, mTextPaint);
    }

    private void drawIndicatNumber(Canvas canvas) {
        mTextPaint.setTextSize(mNumberSize * 1.3f);
        String text = String.valueOf(mCurrent);
        Rect rect = new Rect();
        // 获取文本的宽度
        float width = mTextPaint.measureText(text);
        // 获取文本的高度
        mTextPaint.getTextBounds(text, 0, text.length(), rect);
        float height = rect.height();
        // 文本左边缘距离X轴的距离
        float x = getWidth() / 2f - mTextPaint.measureText("/") / 2f - 3 - width;
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        // 文本基线位置距离Y轴的距离
        float y = getHeight() / 2f + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2f;
        if (isUp) { // 指示数字向上滑动
            y = offset * y + (1 - offset) * (getHeight() / 2f - mCircleSize / 2f + mCircleSize + height);
        } else {    // 指示数字向下滑动
            y = offset * y + (1 - offset) * (getHeight() / 2f - mCircleSize / 2f);
        }
        canvas.drawText(text, x, y, mTextPaint);
        mTextPaint.setTextSize(mNumberSize);
    }

    /**
     * 将指示器绑定到ViewPager
     *
     * @param viewPager view pager
     */
    public void bindViewPager(ViewPager viewPager) {
        if (viewPager != null && viewPager.getAdapter() != null) {
            mCount = viewPager.getAdapter().getCount();
            new ViewPagerHelper().bindScrollListener(viewPager, this);
            invalidate();    // 绑定ViewPager后指示器重绘，因为指示器的数字与初始的可能不同
        }
    }

    @Override
    public void onPageScroll(int enterPosition, int leavePosition, float percent) {
        mCurrent = enterPosition + 1;
        offset = percent;
        isUp = enterPosition > leavePosition;
        postInvalidate();       // 滑动过程中不断重绘
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

    private int sp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                dpValue,
                mContext.getResources().getDisplayMetrics());
    }
}
