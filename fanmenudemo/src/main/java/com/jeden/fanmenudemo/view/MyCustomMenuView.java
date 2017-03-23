package com.jeden.fanmenudemo.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.jeden.fanmenudemo.R;
import com.jeden.fanmenudemo.view.base.CommonPositionViewGroup;
import com.jeden.fanmenudemo.view.base.MenuLayoutStateChangeable;
import com.jeden.fanmenudemo.view.base.SelectCardState;

/**
 * Created by Administrator on 2017/3/12.
 */

public class MyCustomMenuView extends CommonPositionViewGroup{

    private static final float CUR_DEGREE = 90;
    private Paint mPaint;
    private Paint mAlphaPaint;
    private int mOuterRadius;
    private int mInnerRadius;
    private int[] mColors;

    private float mOffsetDegree;
    private int mCurrentPage;

    private MyCustomMenuLayout mFavorate;
    private MyCustomMenuLayout mToolBox;
    private MyCustomMenuLayout mRecently;

    public MyCustomMenuView(Context context) {
        super(context);
        initView(context);
    }

    public MyCustomMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyCustomMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mFavorate = (MyCustomMenuLayout) findViewById(R.id.fanmenu_menu_layout_id_favorite);
        mToolBox = (MyCustomMenuLayout) findViewById(R.id.fanmenu_menu_layout_id_toolbox);
        mRecently = (MyCustomMenuLayout) findViewById(R.id.fanmenu_menu_layout_id_recently);
    }

    public void initView(Context context){
        Resources rs = context.getResources();
        mOuterRadius = rs.getDimensionPixelSize(R.dimen.fanmenu_menu_layout_outer_radius);
        mInnerRadius = rs.getDimensionPixelSize(R.dimen.fanmenu_menu_layout_inner_radius);
        int color1 = rs.getColor(R.color.fanmenu_item_menu_bg_color1);
        int color2 = rs.getColor(R.color.fanmenu_item_menu_bg_color2);
        int color3 = rs.getColor(R.color.fanmenu_item_menu_bg_color3);

        mColors = new int[4];
        mColors[0] = color1;
        mColors[1] = color2;
        mColors[2] = color3;
        mColors[3] = color3;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);

        mAlphaPaint = new Paint();
        mAlphaPaint.setStyle(Paint.Style.FILL);
        mAlphaPaint.setAlpha(69);
        mAlphaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    @Override
    public void setPositionState(int state) {
        super.setPositionState(state);
        int count = getChildCount();
        for(int i = 0; i < count; i++)
        {
            View child = getChildAt(i);
            if(child instanceof MyCustomMenuLayout){
                ((MyCustomMenuLayout) child).setPositionState(state);
            }
        }
    }

    protected void afterOnMeasure() {

        LinearGradient lg = new LinearGradient(isLeft() ? mOuterRadius : getMeasuredWidth() - mOuterRadius, 0,
                isLeft() ? 0 : getMeasuredWidth(), getMeasuredHeight(), mColors, new float[]{0, 0.3f, 0.6f, 1}, Shader.TileMode.REPEAT);
        mPaint.setShader(lg);
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

            if(!(child instanceof MyCustomMenuLayout)){
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

        if(!(child instanceof MyCustomMenuLayout)){
            return super.drawChild(canvas, child, drawingTime);
        }

        int index = indexOfChild(child);

//        float rotateDegree = index * CUR_DEGREE + mOffsetDegree;
        float rotateDegree = getCurrentDegreeByChildIndex(index) + mOffsetDegree;
        rotateDegree = isLeft() ? rotateDegree : - rotateDegree;

        canvas.save();
        canvas.rotate(rotateDegree, isLeft() ? 0 : getWidth(), getHeight());
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return result;
    }

    public float getCurrentDegreeByChildIndex(int index)
    {
        int temp = mCurrentPage - index - 1;
        if(temp == -2)
        {
            temp = 1;
        }
        else if(temp == 2)
        {
            temp = -1;
        }

        return temp * CUR_DEGREE;
    }

    /**
     * 旋转指示器指示的位置
     * @param cur 当前指示的是哪个位置，（0,1,2）
     * @param offset 偏移（-1 -0 - 1）
     */
    public void setRotateView(int cur, float offset){
        mCurrentPage = cur;
        mOffsetDegree = offset * CUR_DEGREE;

        mFavorate.setDisableTouchEvent(true);
        mRecently.setDisableTouchEvent(true);
        mToolBox.setDisableTouchEvent(true);
        switch (mCurrentPage)
        {
            case SelectCardState.CARD_STATE_FAVORATE:
                mFavorate.setDisableTouchEvent(false);
                break;
            case SelectCardState.CARD_STATE_RECENTLY:
                mRecently.setDisableTouchEvent(false);
                break;
            case SelectCardState.CARD_STATE_TOOLBOX:
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
    }

    @Override
    public void setMenuStateChangeListener(MenuLayoutStateChangeable changeable) {
        mFavorate.setMenuStateChangeListener(changeable);
        mRecently.setMenuStateChangeListener(changeable);
        mToolBox.setMenuStateChangeListener(changeable);
    }
}