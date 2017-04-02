package com.jeden.fanmenu.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.view.base.CardState;
import com.jeden.fanmenu.view.base.CommonPositionViewGroup;
import com.jeden.fanmenu.view.base.MenuLayoutStateChangeable;

/**
 * Created by Administrator on 2017/3/12.
 */

public class FanMenuView extends CommonPositionViewGroup {
    private static final String TAG = FanMenuView.class.getSimpleName();
    private static final float CUR_DEGREE = 90;
    private Paint mPaint;
    private Paint mAlphaPaint;
    private Paint mInnerPaint;
    private int mOuterRadius;
    private int mInnerRadius;
    private int[] mColors;

    private float mOffsetDegree;
    private int mCurrentPage;

    private FanMenuLayout mFavorite;
    private FanMenuLayout mToolBox;
    private FanMenuLayout mRecently;

    public FanMenuView(Context context) {
        super(context);
        initView(context);
    }

    public FanMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FanMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mFavorite = (FanMenuLayout) findViewById(R.id.fanmenu_menu_layout_id_favorite);
        mToolBox = (FanMenuLayout) findViewById(R.id.fanmenu_menu_layout_id_toolbox);
        mRecently = (FanMenuLayout) findViewById(R.id.fanmenu_menu_layout_id_recently);
    }

    public void initView(Context context) {
        Resources rs = context.getResources();
        mOuterRadius = rs.getDimensionPixelSize(R.dimen.fan_menu_layout_outer_radius);
        mInnerRadius = rs.getDimensionPixelSize(R.dimen.fan_menu_layout_inner_radius);
        int color1 = rs.getColor(R.color.fan_menu_item_menu_bg_color1);
        int color2 = rs.getColor(R.color.fan_menu_item_menu_bg_color2);
        int color3 = rs.getColor(R.color.fan_menu_item_menu_bg_color3);

        mColors = new int[4];
        mColors[0] = color1;
        mColors[1] = color2;
        mColors[2] = color3;
        mColors[3] = color3;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mInnerPaint = new Paint();
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setColor(rs.getColor(R.color.fan_menu_background_color));

        mAlphaPaint = new Paint();
        mAlphaPaint.setStyle(Paint.Style.FILL);
        mAlphaPaint.setAlpha(255);
        mAlphaPaint.setColor(Color.RED);
        mAlphaPaint.setAntiAlias(true);
        mAlphaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    @Override
    public void setPositionState(int state) {
        super.setPositionState(state);

        setPivotX(isLeft() ? 0 : getWidth());
        setPivotY(getHeight());

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof FanMenuLayout) {
                ((FanMenuLayout) child).setPositionState(state);
            }
        }
    }

    protected void afterOnMeasure() {

        LinearGradient lg = new LinearGradient(isLeft() ? mOuterRadius : getMeasuredWidth() - mOuterRadius, 0,
                isLeft() ? 0 : getMeasuredWidth(), getMeasuredHeight(), mColors, new float[]{0, 0.3f, 0.6f, 1}, Shader.TileMode.REPEAT);
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

        int tempSpec = MeasureSpec.makeMeasureSpec(mOuterRadius, MeasureSpec.EXACTLY);
        measureChildren(tempSpec, tempSpec);
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : sizeWidth, (heightMode == MeasureSpec.EXACTLY) ? sizeWidth
                : sizeWidth);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (!(child instanceof FanMenuLayout)) {
                break;
            }

            if (isLeft()) {
                child.layout(0, getHeight() - mOuterRadius, mOuterRadius, getHeight());
            } else {
                child.layout(getWidth() - mOuterRadius, getHeight() - mOuterRadius, getWidth(), getHeight());
            }
        }

        afterOnMeasure();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {

        int index = indexOfChild(child);

//        float rotateDegree = index * CUR_DEGREE + mOffsetDegree;
        float rotateDegree = getCurrentDegreeByChildIndex(index) + mOffsetDegree;
        rotateDegree = isLeft() ? rotateDegree : -rotateDegree;

        canvas.save();
        canvas.rotate(rotateDegree, isLeft() ? 0 : getWidth(), getHeight());
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return result;
    }

    public float getCurrentDegreeByChildIndex(int index) {
        int temp = mCurrentPage - index - 1;
        if (temp == -2) {
            temp = 1;
        } else if (temp == 2) {
            temp = -1;
        }

        return temp * CUR_DEGREE;
    }

    /**
     * 旋转指示器指示的位置
     *
     * @param cur    当前指示的是哪个位置，（0,1,2）
     * @param offset 偏移（-1 -0 - 1）
     */
    public void setRotateView(int cur, float offset) {
        mCurrentPage = cur;
        mOffsetDegree = offset * CUR_DEGREE;

        mFavorite.setDisableTouchEvent(true);
        mRecently.setDisableTouchEvent(true);
        mToolBox.setDisableTouchEvent(true);
        switch (mCurrentPage) {
            case CardState.CARD_STATE_FAVORITE:
                mFavorite.setDisableTouchEvent(false);
                break;
            case CardState.CARD_STATE_RECENTLY:
                mRecently.setDisableTouchEvent(false);
                break;
            case CardState.CARD_STATE_TOOLBOX:
                mToolBox.setDisableTouchEvent(false);
                break;
            default:
                break;
        }
//
//        mOffsetDegree = -mOffsetDegree;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(isLeft() ? 0 : getWidth(), getHeight(), mOuterRadius, mPaint);

        canvas.drawCircle(isLeft() ? 0 : getWidth(), getHeight(), mInnerRadius, mAlphaPaint);

        canvas.drawCircle(isLeft() ? 0 : getWidth(), getHeight(), mInnerRadius, mInnerPaint);
    }

    @Override
    public void setMenuStateChangeListener(MenuLayoutStateChangeable changeable) {
        mFavorite.setMenuStateChangeListener(changeable);
        mRecently.setMenuStateChangeListener(changeable);
        mToolBox.setMenuStateChangeListener(changeable);
    }
}
