package com.jeden.fanmenu.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.bean.AppInfo;
import com.jeden.fanmenu.common.FanMenuViewTools;
import com.jeden.fanmenu.common.tools.SwipeTools;
import com.jeden.fanmenu.common.tools.SwipeView;
import com.jeden.fanmenu.common.tools.ToolboxHelper;
import com.jeden.fanmenu.view.base.CommonPositionView;

/**
 * Created by Administrator on 2017/3/12.
 */

public class FanMenuItemView extends CommonPositionView implements SwipeView{

    private boolean mToolboxModel = false;
    private int mWidth;
    private int mHeight;
    private int mIconW;
    private int mIconH;
    private int mIconL;
    private int mIconT;
    private int mTitleW;
    private int mTitleH;
    private int mTitleT;
    private int mDelW;
    private Drawable mIcon;
    private String mTitle;
    private boolean mDelShow;
    private Paint mTitlePaint;
    private Drawable mDel;

    public FanMenuItemView(Context context) {
        super(context);
        initView(context);
    }

    public FanMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FanMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        Resources rs = context.getResources();
        mWidth = rs.getDimensionPixelSize(R.dimen.fan_menu_item_title_width);
        mIconW = rs.getDimensionPixelSize(R.dimen.fan_menu_item_icon_width);
        mDelW = rs.getDimensionPixelSize(R.dimen.fan_menu_item_close_width);

        mHeight = mWidth;
        mIconH = mIconW;
        mIconT = FanMenuViewTools.dip2px(context, 5);
        mIconL = (mWidth - mIconW) / 2;
        mTitleW = mWidth;
        mTitleT = mIconT + mIconH + FanMenuViewTools.dip2px(context, 1);
        mTitleH = mHeight - mTitleT;

        mTitlePaint = new Paint();
        mTitlePaint.setColor(rs.getColor(R.color.fan_menu_common_white_color));
        mTitlePaint.setTextSize(rs.getDimensionPixelSize(R.dimen.fan_menu_item_title_size));

        mDel = rs.getDrawable(R.drawable.fan_item_close);
        mDel.setBounds(0, 0, mDelW, mDelW);
    }

    public void showDelBtn() {
        mDelShow = true;
    }

    public void hideDelBtn() {
        mDelShow = false;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setItemIcon(Drawable icon) {
        mIcon = icon;
        mIcon.setBounds(mIconL, mIconT, mIconL + mIconW, mIconT + mIconH);
    }

    public Rect getDeleteRect(){
        return new Rect(0, 0, mDelW, mDelW);
    }

    public void setToolboxModel(boolean toolboxModel)
    {
        mToolboxModel = toolboxModel;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : mWidth, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mIcon.draw(canvas);
        canvas.drawText(mTitle, 0, mTitleT, mTitlePaint);
        mDel.draw(canvas);
    }

    @Override
    public void setTag(Object tag) {
        super.setTag(tag);
        if(tag instanceof AppInfo)
        {
            AppInfo appInfo = (AppInfo)tag;
            if(appInfo.getIntent() == null)
            {
                SwipeTools tools = ToolboxHelper.checkSwipeTools(getContext(), appInfo);
                if(tools != null)
                {
                    tools.bindSwipeView(getContext(), this);
                }
            }
        }
    }
}
