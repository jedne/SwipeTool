package com.jeden.fanmenu.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.common.ColorShades;
import com.jeden.fanmenu.common.FanMenuViewTools;
import com.jeden.fanmenu.view.base.CommonPositionViewGroup;
import com.jeden.fanmenu.view.base.CardState;

/**
 * Created by Administrator on 2017/3/12.
 */

public class FanSelectTextLayout extends CommonPositionViewGroup {

    public static final String TAG = FanSelectTextLayout.class.getSimpleName();

    private Paint mPaint;
    private int mRadius;
    private int[] mColors;
    private int mTvPadding;
    private int mSelectColor;
    private int mNormalColor;

    private TextView mRecently;
    private TextView mToolBox;
    private TextView mFavorite;

    private float mInScreenX;
    private float mInScreenY;
    private long mLastTime;

    private int mWidth;
    private int mHeight;

    private int mTouchSlop;

    private ColorShades mColorShades = new ColorShades();

    public FanSelectTextLayout(Context context) {
        super(context);
        initView(context);
    }

    public FanSelectTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FanSelectTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mRecently = (TextView) findViewById(R.id.fanmenu_select_text_view_recently);
        mToolBox = (TextView) findViewById(R.id.fanmenu_select_text_view_toolbox);
        mFavorite = (TextView) findViewById(R.id.fanmenu_select_text_view_favorite);
    }

    private void initView(Context context) {

        Resources rs = context.getResources();
        int bgc1 = rs.getColor(R.color.fan_menu_select_text_bg_color1);
        int bgc2 = rs.getColor(R.color.fan_menu_select_text_bg_color2);
        int bgc3 = rs.getColor(R.color.fan_menu_select_text_bg_color3);
        mSelectColor = rs.getColor(R.color.fan_menu_common_white_color);
        mNormalColor = rs.getColor(R.color.fan_menu_select_text_color2);

        mTouchSlop = FanMenuViewTools.dip2px(context, 4);

        mRadius = rs.getDimensionPixelSize(R.dimen.fan_menu_select_text_bg_radius);
        mTvPadding = rs.getDimensionPixelSize(R.dimen.fan_menu_select_text_padding_center);

        mColors = new int[3];
        mColors[0] = bgc1;
        mColors[1] = bgc2;
        mColors[2] = bgc3;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    protected void afterOnMeasure() {

        LinearGradient lg = new LinearGradient(isLeft() ? 2 * mRadius : getMeasuredWidth() - 2 * mRadius, 0,
                isLeft() ? 0 : getMeasuredWidth(), getMeasuredHeight(), mColors, new float[]{0, 0.8f, 1}, Shader.TileMode.REPEAT);
        mPaint.setShader(lg);

        setPivotX(isLeft() ? 0 : getWidth());
        setPivotY(getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : sizeWidth, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : sizeWidth);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (isLeft()) {
                if (child instanceof FanSelectTextIndicator) {
                    child.layout(l, b - mRadius, l + mRadius, b);
                } else {
                    child.layout(mTvPadding, b - child.getMeasuredHeight() / 2, mTvPadding + child.getMeasuredWidth(),
                            b + child.getMeasuredHeight() / 2);
                }

            } else {
                if (child instanceof FanSelectTextIndicator) {
                    child.layout(r - mRadius, b - mRadius, r, b);
                } else {
                    child.layout(r - mTvPadding - child.getMeasuredWidth(), b - child.getMeasuredHeight() / 2, r - mTvPadding,
                            b + child.getMeasuredHeight() / 2);
                }
            }
        }

        afterOnMeasure();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {

        if (child instanceof FanSelectTextIndicator) {
            return super.drawChild(canvas, child, drawingTime);
        }

        int count = getChildCount();
        int index = indexOfChild(child);
        int degree = 90 / (count);
        degree = isLeft() ? -degree : degree;
        canvas.save();
        canvas.rotate(degree * (index), isLeft() ? 0 : getWidth(), getHeight());
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(isLeft() ? 0 : getWidth(), getHeight(), mRadius, mPaint);
    }

    @Override
    public void setPositionState(int state) {
        super.setPositionState(state);

        setPivotX(isLeft() ? 0 : getWidth());
        setPivotY(getHeight());
    }

    /**
     * 旋转指示器指示的位置
     * @param cur 当前指示的是哪个位置，（0,1,2）
     * @param offset 偏移（-1 -0 - 1）
     */
    public void setRotateView(int cur, float offset){

        switch (cur){
            case CardState.CARD_STATE_FAVORITE:

                if(offset == 0)
                {
                    mFavorite.setTextColor(mSelectColor);
                    mToolBox.setTextColor(mNormalColor);
                    mRecently.setTextColor(mNormalColor);
                }
                else
                {
                    int color1 = mColorShades.setShade(Math.abs(offset))
                            .setFromColor(mSelectColor)
                            .setToColor(mNormalColor).generate();

                    mFavorite.setTextColor(color1);

                    int color2 = mColorShades.setShade(Math.abs(offset))
                            .setFromColor(mNormalColor)
                            .setToColor(mSelectColor).generate();
                    if(offset > 0)
                    {
                        mRecently.setTextColor(color2);
                        mToolBox.setTextColor(mNormalColor);
                    }
                    else {
                        mToolBox.setTextColor(color2);
                        mRecently.setTextColor(mNormalColor);
                    }
                }
                break;
            case CardState.CARD_STATE_RECENTLY:
                if(offset == 0)
                {
                    mRecently.setTextColor(mSelectColor);
                    mToolBox.setTextColor(mNormalColor);
                    mFavorite.setTextColor(mNormalColor);
                }
                else
                {
                    int color1 = mColorShades.setShade(Math.abs(offset))
                            .setFromColor(mSelectColor)
                            .setToColor(mNormalColor).generate();

                    mRecently.setTextColor(color1);

                    int color2 = mColorShades.setShade(Math.abs(offset))
                            .setFromColor(mNormalColor)
                            .setToColor(mSelectColor).generate();
                    if(offset > 0)
                    {
                        mToolBox.setTextColor(color2);
                        mFavorite.setTextColor(mNormalColor);
                    }
                    else {
                        mFavorite.setTextColor(color2);
                        mToolBox.setTextColor(mNormalColor);
                    }
                }
                break;
            case CardState.CARD_STATE_TOOLBOX:
                if(offset == 0)
                {
                    mToolBox.setTextColor(mSelectColor);
                    mFavorite.setTextColor(mNormalColor);
                    mRecently.setTextColor(mNormalColor);
                }
                else
                {
                    int color1 = mColorShades.setShade(Math.abs(offset))
                            .setFromColor(mSelectColor)
                            .setToColor(mNormalColor).generate();

                    mToolBox.setTextColor(color1);

                    int color2 = mColorShades.setShade(Math.abs(offset))
                            .setFromColor(mNormalColor)
                            .setToColor(mSelectColor).generate();
                    if(offset > 0)
                    {
                        mFavorite.setTextColor(color2);
                        mRecently.setTextColor(mNormalColor);
                    }
                    else {
                        mRecently.setTextColor(color2);
                        mFavorite.setTextColor(mNormalColor);
                    }
                }
                break;
            default:
                break;
        }
//        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInScreenX = event.getX();
                mInScreenY = event.getY();
                mLastTime = System.currentTimeMillis();
                if(isInTheView(event.getX(), event.getY()))
                {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                }
            break;
            case MotionEvent.ACTION_MOVE:
            break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();
                long curTime = System.currentTimeMillis();
                if(FanMenuViewTools.getTwoPointDistance(upX, upY, mInScreenX, mInScreenY) < mTouchSlop && curTime - mLastTime < 400)
                {
                    selectTextAndRefresh();
                }
            break;
            default:
            break;
        }
        return super.onTouchEvent(event);
    }

    public void selectTextAndRefresh()
    {
        int index;
        double degree;
        if(isLeft())
        {
            degree = FanMenuViewTools.getDegreeByPoint(mInScreenX, mHeight - mInScreenY);
        }
        else
        {
            degree = FanMenuViewTools.getDegreeByPoint(mWidth - mInScreenX, mHeight - mInScreenY);
        }
        if(degree < 30)
        {
            index = CardState.CARD_STATE_FAVORITE;
        }
        else if(degree > 30 && degree < 60)
        {
            index = CardState.CARD_STATE_TOOLBOX;
        }
        else
        {
            index = CardState.CARD_STATE_RECENTLY;
        }

        if(mStateChangeable != null)
        {
            mStateChangeable.selectCardChange(index);
        }
    }

    public boolean isInTheView(float x, float y)
    {
        float distance = FanMenuViewTools.getTwoPointDistance(x, y, (isLeft() ? 0 : getWidth()), getHeight());
        return distance <= mRadius && distance >= mTvPadding;
    }
}
