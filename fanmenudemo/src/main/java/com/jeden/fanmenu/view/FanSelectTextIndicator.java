package com.jeden.fanmenu.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.view.base.CommonPositionView;
import com.jeden.fanmenu.view.base.CardState;

/**
 * Created by jeden on 2017/3/14.
 */

public class FanSelectTextIndicator extends CommonPositionView {

    public static final String TAG = FanSelectTextIndicator.class.getSimpleName();
    private static final float DEFAULT_DEGREE = 270 / 8;
    private int mWidth;
    private int[] mColors;
    private RectF mFanRectF = new RectF();
    private Paint mPaint;
    private float mFanDegree;

    private float mStartDegree;

    public FanSelectTextIndicator(Context context) {
        super(context);
        initView(context);
    }

    public FanSelectTextIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FanSelectTextIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        Resources rs = context.getResources();

        int color1 = rs.getColor(R.color.fan_menu_text_indicator_bg_color1);
        int color2 = rs.getColor(R.color.fan_menu_text_indicator_bg_color2);
        int color3 = rs.getColor(R.color.fan_menu_text_indicator_bg_color3);
        mWidth = rs.getDimensionPixelSize(R.dimen.fan_menu_select_text_bg_radius);

        mColors = new int[4];
        mColors[0] = color1;
        mColors[1] = color2;
        mColors[2] = color3;
        mColors[3] = color3;

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mFanDegree = 90 / 4;
//        mStartDegree = - mFanDegree;
    }

    protected void afterOnMeasure() {

        LinearGradient lg = new LinearGradient(isLeft() ? mWidth : 0, 0,
                isLeft() ? 0 : mWidth, mWidth, mColors, new float[]{0, 0.5f, 0.7f, 1}, Shader.TileMode.REPEAT);
        mPaint.setShader(lg);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (isLeft()) {
            mFanRectF.left = -mWidth;
            mFanRectF.right = mWidth;
            mFanRectF.top = 0;
            mFanRectF.bottom = 2 * mWidth;
        } else {
            mFanRectF.left = 0;
            mFanRectF.right = 2 * mWidth;
            mFanRectF.top = 0;
            mFanRectF.bottom = 2 * mWidth;
        }

        afterOnMeasure();
    }

    /**
     * 旋转指示器指示的位置
     *
     * @param cur    当前指示的是哪个位置，（1,2,3）
     * @param offset 偏移（-1 -0 - 1）
     */
    public void setRotateView(int cur, float offset) {

        int tempCur;
        switch (cur) {
            case CardState.CARD_STATE_FAVORITE:
                if (offset > 0.5) {
                    tempCur = CardState.CARD_STATE_RECENTLY;
                    offset -= 1;
                    break;
                }
                tempCur = cur;
                break;
            case CardState.CARD_STATE_RECENTLY:
                if (offset < -0.5) {
                    tempCur = CardState.CARD_STATE_FAVORITE;
                    offset += 1;
                    break;
                }
                tempCur = cur;
                break;
            case CardState.CARD_STATE_TOOLBOX:
                tempCur = cur;
                break;
            default:
                tempCur = cur;
                break;
        }

        tempCur--;
        float offsetDegree = (tempCur + offset) * mFanDegree;
        mStartDegree = isLeft() ? mFanDegree : -mFanDegree;
        mStartDegree += isLeft() ? -offsetDegree : offsetDegree;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        canvas.rotate(mStartDegree, isLeft() ? 0 : mWidth, mWidth);
        float startDegree = isLeft() ? -DEFAULT_DEGREE - mFanDegree : 180 + DEFAULT_DEGREE;
        canvas.drawArc(mFanRectF, startDegree, mFanDegree, true, mPaint);

        canvas.restore();
    }
}
