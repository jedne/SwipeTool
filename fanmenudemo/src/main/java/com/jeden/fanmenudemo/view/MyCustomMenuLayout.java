package com.jeden.fanmenudemo.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.jeden.fanmenudemo.R;
import com.jeden.fanmenudemo.bean.AppInfo;
import com.jeden.fanmenudemo.tools.ContentProvider;
import com.jeden.fanmenudemo.tools.FanMenuViewTools;
import com.jeden.fanmenudemo.view.base.CommonPositionViewGroup;
import com.jeden.fanmenudemo.view.base.SelectCardState;

import java.util.List;

/**
 * Created by Administrator on 2017/3/12.
 */

public class MyCustomMenuLayout extends CommonPositionViewGroup{

    public static final String TAG = MyCustomMenuLayout.class.getSimpleName();
    private int mMenuType = SelectCardState.CARD_STATE_RECENTLY;
    private boolean mLongClickable;

    private int mItemHalfWidth;
    private int mItemInnerRadius;
    private int mItemOuterRadius;
    private int mItemAddWidth;

    private float mInScreenX;
    private float mInScreenY;
    private long mLastTime;
    private int mTouchSlop;

    public MyCustomMenuLayout(Context context) {
        super(context);
    }

    public MyCustomMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context, attrs);
    }

    public MyCustomMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FanMenu);
        mMenuType = ta.getInteger(R.styleable.FanMenu_menuType, -1);
        mLongClickable = ta.getBoolean(R.styleable.FanMenu_longClickable, false);

        LayoutInflater inflater = LayoutInflater.from(context);
        MyCustomMenuItemView itemView;

        List<AppInfo> tempAppInfo = getData();

        for(AppInfo info : tempAppInfo)
        {
            itemView = (MyCustomMenuItemView) inflater.inflate(R.layout.my_custom_fan_item, null);
            itemView.setTitle(info.getAppLabel());
            itemView.setItemIcon(info.getAppIcon());
            itemView.setTag(info);
            this.addView(itemView);
            itemView.setOnTouchListener(new MyViewTouchListener());
        }

        Resources rs = getResources();
        mItemAddWidth = rs.getDimensionPixelSize(R.dimen.fanmenu_menu_item_add_btn_width);
        mItemHalfWidth = rs.getDimensionPixelSize(R.dimen.fanmenu_menu_item_half_width);
        mItemInnerRadius = rs.getDimensionPixelSize(R.dimen.fanmenu_menu_item_inner_radius);
        mItemOuterRadius = rs.getDimensionPixelSize(R.dimen.fanmenu_menu_item_outer_radius);

        mTouchSlop = FanMenuViewTools.dip2px(context, 4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int tempSpec = MeasureSpec.makeMeasureSpec(mItemHalfWidth * 2, MeasureSpec.EXACTLY);
        measureChildren(tempSpec, tempSpec);
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : sizeWidth, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : sizeHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        Point p = new Point();

        for(int i = 0; i < count; i++)
        {
            View child = getChildAt(i);

            p.x = isLeft() ? 0 : getWidth();
            p.y = getHeight();

            getItemCenterPoint(i, count, p);
            child.layout(p.x - mItemHalfWidth, p.y - mItemHalfWidth, p.x + mItemHalfWidth, p.y + mItemHalfWidth);
        }
    }

    public double generateDegreePerItem(int index, int count)
    {
        if(count < 0 || count > 9)
            return 0;

        double degree = Math.PI / 2;
        if(count < 5)
        {
            return degree / (2 * count) * (2 * index + 1);
        }
        else
        {
            if(index < 4)
            {
                return degree / 8 * (2 * index + 1);
            }
            return (degree / (2 * (count - 4))) * (2 * (index - 4) + 1);
        }
    }

    public void getItemCenterPoint(int index, int count, Point p)
    {

        int radius = index >= 4 ? mItemOuterRadius : mItemInnerRadius;

        double degree = generateDegreePerItem(index, count);

        int x = (int)(Math.sin(degree) * radius);
        p.y -= (int)(Math.cos(degree) * radius);
        p.x += (isLeft() ? x : - x);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public List<AppInfo> getData()
    {
        List<AppInfo> tempAppInfo = null;
        switch (mMenuType){
            case SelectCardState.CARD_STATE_FAVORATE:
                tempAppInfo = ContentProvider.getmInstance().getmFavorates();
                break;
            case SelectCardState.CARD_STATE_RECENTLY:
                tempAppInfo = ContentProvider.getmInstance().getmRecentlys();
                break;
            case SelectCardState.CARD_STATE_TOOLBOX:
                tempAppInfo = ContentProvider.getmInstance().getmToolBoxs();
                break;
            default:
                break;
        }

        return tempAppInfo;
    }

    class MyViewTouchListener implements View.OnTouchListener
    {
        private float mDownX;
        private float mDownY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.v(TAG, "onTouch event:" + event.getAction());
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mDownY = event.getY();

                    mLastTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getX();
                    float moveY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float upX = event.getX();
                    float upY = event.getY();
                    long cur = System.currentTimeMillis();

                    if(Math.abs(upX - mInScreenY) > mTouchSlop / 2 || Math.abs(upY - mInScreenY) > mTouchSlop / 2
                            || cur - mLastTime < 300)
                    {
                        AppInfo tag = (AppInfo)v.getTag();
                        if(tag != null)
                        {
                            Toast.makeText(getContext(), "clickItem name:" + tag.getAppLabel(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "onTouchEvent event:" + event.getAction());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInScreenX = event.getX();
                mInScreenY = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                if(Math.abs(moveX - mInScreenY) > mTouchSlop || Math.abs(moveY - mInScreenY) > mTouchSlop)
                {

                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(mDisableTouchEvent)
        {
            return false;
        }
        Log.v(TAG, "dispatchTouchEvent event:" + event.getAction());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInScreenX = event.getX();
                mInScreenY = event.getY();

//                mLastTime = System.currentTimeMillis();
//                if(mLongClickable)
//                {
//                    handler.postDelayed(mLongClickRunnable, 600);
//                }
//                mDownSelectChild = getChildByPoint(mInScreenX, mInScreenY);

                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
//                if(Math.abs(moveX - mInScreenY) > mTouchSlop / 2 || Math.abs(moveY - mInScreenY) > mTouchSlop / 2)
//                {
//                    handler.removeCallbacks(mLongClickRunnable);
//                }
                break;
            case MotionEvent.ACTION_UP:
//                float upX = event.getX();
//                float upY = event.getY();
//                long cur = System.currentTimeMillis();
//                if(Math.abs(upX - mInScreenY) > mTouchSlop / 2 || Math.abs(upY - mInScreenY) > mTouchSlop / 2
//                        || cur - mLastTime < 300)
//                {
//                    handler.removeCallbacks(mLongClickRunnable);
//                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public MyCustomMenuItemView getChildByPoint(float x, float y)
    {
        Point p = new Point();
        p.x = isLeft() ? 0 : getWidth();
        p.y = getHeight();

        List<AppInfo> datas = getData();
        int count = datas.size();
        Rect r = new Rect();
        for(int i = 0; i < count; i++)
        {
            getItemCenterPoint(i, count, p);
            r.left = p.x - mItemHalfWidth;
            r.top = p.y - mItemHalfWidth;
            r.right = p.x + mItemHalfWidth;
            r.bottom = p.y + mItemHalfWidth;
            if(isPointInRect(r, x, y))
            {
                Log.v(TAG, "getChildByPoint index:" + i);
                return (MyCustomMenuItemView) findViewWithTag(datas.get(i));
            }

        }

        return null;
    }

    public boolean isPointInRect(Rect r, float x, float y)
    {
        return x > r.left && x < r.right && y > r.top && y < r.bottom;
    }
}
