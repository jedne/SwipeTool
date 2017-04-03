package com.jeden.fanmenu.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.common.FanMenuViewTools;
import com.jeden.fanmenu.view.base.CommonPositionView;

/**
 * Created by Administrator on 2017/3/12.
 */

public class FanCenterView extends CommonPositionView {

    private static final int CENTER_TEXT_WIDTH = 1;
    private static final float CENTER_TEXT_OFFSET = 0.35f;

    private int mCenterX;
    private int mCenterY;

    private int mViewWidth;
    private int mTextOffset;
    private int mTextSize;
    private int mBgColorStart;
    private int mBgColorEnd;

    private Paint mTextPaint;
    private Paint mBgPaint;

    private float[] mLines = new float[8];

    public FanCenterView(Context context) {
        super(context);
        initView(context);
    }

    public FanCenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FanCenterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {

        Resources rs = context.getResources();
        int textColor = rs.getColor(R.color.fan_menu_common_white_color);
        mBgColorStart = rs.getColor(R.color.fan_menu_center_bg_color_start);
        mBgColorEnd = rs.getColor(R.color.fan_menu_center_bg_color_end);
        int textWidth = FanMenuViewTools.dip2px(context, CENTER_TEXT_WIDTH);
        mViewWidth = rs.getDimensionPixelSize(R.dimen.fan_menu_center_view_width);
        mTextOffset = rs.getDimensionPixelSize(R.dimen.fan_menu_center_text_offset_x);
        mTextSize = rs.getDimensionPixelSize(R.dimen.fan_menu_center_text_width_half);

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setStrokeWidth(textWidth);
        mTextPaint.setStyle(Paint.Style.STROKE);

        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.FILL);

        mCenterY = getMeasuredHeight() - mCenterX;
        mCenterX = isLeft() ? mCenterX : getMeasuredWidth() - mCenterX;
    }

    protected void afterOnMeasure() {

        LinearGradient lg = new LinearGradient(isLeft() ? (mCenterX + mCenterY) : (mCenterX - mCenterY), 0,
                isLeft() ? 0 : getMeasuredWidth(), getMeasuredHeight(), mBgColorStart, mBgColorEnd, Shader.TileMode.REPEAT);
        mBgPaint.setShader(lg);

        setPivotX(isLeft() ? 0 : getWidth());
        setPivotY(getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : mViewWidth, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : mViewWidth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mCenterX = (int) (getMeasuredHeight() * CENTER_TEXT_OFFSET);

        mCenterY = getMeasuredHeight() - mCenterX;
        mCenterX = isLeft() ? mCenterX : getMeasuredWidth() - mCenterX;

        int offsetTextX;
        int offsetTextY;

        offsetTextX = mCenterX + (isLeft() ? mTextOffset : -mTextOffset);
        offsetTextY = mCenterY - mTextOffset;

        mLines[0] = offsetTextX - mTextSize;
        mLines[1] = offsetTextY - mTextSize;
        mLines[2] = offsetTextX + mTextSize;
        mLines[3] = offsetTextY + mTextSize;

        mLines[4] = offsetTextX - mTextSize;
        mLines[5] = offsetTextY + mTextSize;
        mLines[6] = offsetTextX + mTextSize;
        mLines[7] = offsetTextY - mTextSize;

        afterOnMeasure();
    }

    @Override
    public void setPositionState(int state) {
        super.setPositionState(state);

        setPivotX(isLeft() ? 0 : getWidth());
        setPivotY(getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {

//        mBgPaint.setShadowLayer(mCenterY, 0, 20, Color.parseColor("#DC000000"));
        canvas.drawCircle(mCenterX, mCenterY, mCenterY, mBgPaint);
        canvas.drawLines(mLines, mTextPaint);
    }
}
