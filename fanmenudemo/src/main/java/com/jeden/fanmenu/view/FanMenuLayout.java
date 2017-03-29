package com.jeden.fanmenu.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.bean.AppInfo;
import com.jeden.fanmenu.common.model.ContentProvider;
import com.jeden.fanmenu.common.FanMenuViewTools;
import com.jeden.fanmenu.view.base.CommonPositionViewGroup;
import com.jeden.fanmenu.view.base.CardState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/12.
 */

public class FanMenuLayout extends CommonPositionViewGroup {

    public static final String TAG = FanMenuLayout.class.getSimpleName();

    private static final String ADD_VIEW_TAG = "MENU_LAYOUT_ITEM_ADD_TAG";
    private int mMenuType = CardState.CARD_STATE_RECENTLY;
    private boolean mLongClickable;

    private int mItemHalfWidth;
    private int mItemInnerRadius;
    private int mItemOuterRadius;

    private long mLastTime;
    private int mTouchSlop;

    private Runnable mLongClickRunnable = new LongClickRunnable();
    protected Vibrator mVibrator;
    protected boolean mIsEditModel;
    private FanMenuItemView mDownSelectChild;

    private boolean mIsDeleting = false;
    private int[] mLastSlot = new int[3];
    private FanMenuItemView[] mLastSlotView = new FanMenuItemView[9];
    private Rect[] mDragIndexPoint = new Rect[9];
    private boolean mIsDragging = false;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public FanMenuLayout(Context context) {
        super(context);
    }

    public FanMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context, attrs);
    }

    public FanMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FanMenu);
        mMenuType = ta.getInteger(R.styleable.FanMenu_menuType, -1);
        mLongClickable = ta.getBoolean(R.styleable.FanMenu_longClickable, false);
        ta.recycle();

        addAllItemView();

        Resources rs = getResources();
        mItemHalfWidth = rs.getDimensionPixelSize(R.dimen.fan_menu_item_half_width);
        mItemInnerRadius = rs.getDimensionPixelSize(R.dimen.fan_menu_item_inner_radius);
        mItemOuterRadius = rs.getDimensionPixelSize(R.dimen.fan_menu_item_outer_radius);

        mTouchSlop = FanMenuViewTools.dip2px(context, 5);
    }

    public void addAllItemView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        FanMenuItemView itemView;

        List<AppInfo> tempAppInfo = getData();

        for (AppInfo info : tempAppInfo) {
            itemView = (FanMenuItemView) inflater.inflate(R.layout.fan_menu_item_layout, null);
            itemView.setToolboxModel(mMenuType == CardState.CARD_STATE_TOOLBOX);
            itemView.setTitle(info.getAppLabel());
            itemView.setItemIcon(info.getAppIcon());
            itemView.setTag(info);
            this.addView(itemView);
            itemView.setOnTouchListener(mMyViewTouchListener);
        }

        addViewIfNeed(inflater);
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
        count = count > 9 ? 9 : count;
        Point p = new Point();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            p.x = isLeft() ? 0 : getWidth();
            p.y = getHeight();

            getItemCenterPoint(i, count, p);
            child.layout(p.x - mItemHalfWidth, p.y - mItemHalfWidth, p.x + mItemHalfWidth, p.y + mItemHalfWidth);
        }
    }

    public void addViewIfNeed(LayoutInflater inflater) {
        if (mMenuType == CardState.CARD_STATE_FAVORITE || mMenuType == CardState.CARD_STATE_TOOLBOX) {
            FrameLayout addView = (FrameLayout) inflater.inflate(R.layout.fan_menu_item_add_layout, null);
            addView.setTag(ADD_VIEW_TAG);
            addView.setOnTouchListener(mMyViewTouchListener);
            this.addView(addView);
        }
    }

    public double generateDegreePerItem(int index, int count) {
        if (count < 0 || count > 9)
            return 0;

        double degree = Math.PI / 2;
        if (count < 5) {
            return degree / (2 * count) * (2 * index + 1);
        } else {
            if (index < 4) {
                return degree / 8 * (2 * index + 1);
            }
            return (degree / (2 * (count - 4))) * (2 * (index - 4) + 1);
        }
    }

    public void getItemCenterPoint(int index, int count, Point p) {

        int radius = index >= 4 ? mItemOuterRadius : mItemInnerRadius;

        double degree = generateDegreePerItem(index, count);

        int x = (int) (Math.sin(degree) * radius);
        p.y -= (int) (Math.cos(degree) * radius);
        p.x += (isLeft() ? x : -x);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public List<AppInfo> getData() {
        List<AppInfo> tempAppInfo = null;
        switch (mMenuType) {
            case CardState.CARD_STATE_FAVORITE:
                tempAppInfo = ContentProvider.getInstance().getFavorite();
                break;
            case CardState.CARD_STATE_RECENTLY:
                tempAppInfo = ContentProvider.getInstance().getRecently();
                break;
            case CardState.CARD_STATE_TOOLBOX:
                tempAppInfo = ContentProvider.getInstance().getToolBox();
                break;
            default:
                break;
        }

        return tempAppInfo;
    }

    public void saveChangeData() {
        switch (mMenuType) {
            case CardState.CARD_STATE_FAVORITE:
                ContentProvider.getInstance().saveFavorite();
                break;
            case CardState.CARD_STATE_RECENTLY:
                ContentProvider.getInstance().saveRecently();
                break;
            case CardState.CARD_STATE_TOOLBOX:
                ContentProvider.getInstance().saveToolbox();
                break;
            default:
                break;
        }
    }

    OnTouchListener mMyViewTouchListener = new View.OnTouchListener() {
        private float mDownX;
        private float mDownY;
        private int mDownType = DOWN_NULL;
        private static final int DOWN_NULL = -1;
        private static final int DOWN_DELETE = 1;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mIsDeleting) {
                return true;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mDownY = event.getY();
                    mLastTime = System.currentTimeMillis();

                    if (v instanceof FanMenuItemView) {
                        if (mLongClickable && !mIsEditModel) {
                            handler.postDelayed(mLongClickRunnable, 600);
                        }

                        mDownSelectChild = (FanMenuItemView) v;
                        if (mIsEditModel && isTouchTheDeleteView(mDownSelectChild, mDownX, mDownY)) {
                            mDownType = DOWN_DELETE;
                        }
                    } else {
                        mDownSelectChild = null;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getX();
                    float moveY = event.getY();
                    if (Math.abs(moveX - mDownX) > mTouchSlop || Math.abs(moveY - mDownY) > mTouchSlop) {
                        mDownType = DOWN_NULL;
                        handler.removeCallbacks(mLongClickRunnable);
                    }

                    if (mIsEditModel && mDownSelectChild != null) {
                        mIsDragging = true;
                        addMirrorViewAndDrag(moveX - mDownX, moveY - mDownY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    float upX = event.getX();
                    float upY = event.getY();
                    long cur = System.currentTimeMillis();

                    if (mIsEditModel && mIsDragging) {
                        restoreDragView(upX - mDownX, upY - mDownY);
                        break;
                    }

                    if (Math.abs(upX - mDownX) > mTouchSlop || Math.abs(upY - mDownY) > mTouchSlop
                            || cur - mLastTime < 300) {
                        if (mDownType == DOWN_DELETE) {
                            deleteItemAndRefresh(mDownSelectChild);
                        } else if (!mIsEditModel) {
                            handler.removeCallbacks(mLongClickRunnable);
                            btnClicked(v.getTag());
                        }
                    }

                    mDownType = DOWN_NULL;
                    break;
                default:
                    handler.removeCallbacks(mLongClickRunnable);
                    mDownType = DOWN_NULL;
                    break;
            }
            return true;
        }
    };

    public void btnClicked(Object obj) {
        if (obj == null || mStateChangeable == null) {
            return;
        }

        if (obj instanceof AppInfo) {
            mStateChangeable.menuItemClicked(this, (AppInfo) obj);
        } else {
            mStateChangeable.addBtnClicked(this, mMenuType);
        }
    }

    public boolean isTouchTheDeleteView(FanMenuItemView v, float x, float y) {
        ImageView delete = v.getDeleteView();
        if (x > delete.getLeft() && x < delete.getRight() && y > delete.getTop() && y < delete.getBottom()) {
            return true;
        }

        return false;
    }

    public void refreshData() {
        removeAllViews();
        addAllItemView();
        requestLayout();
    }

    public void deleteItemAndRefresh(FanMenuItemView child) {
        List<AppInfo> datas = getData();
        AppInfo appInfo = (AppInfo) child.getTag();
        int index = datas.indexOf(appInfo);
        deleteItemAndReorder(child, index);
        datas.remove(appInfo);
        saveChangeData();
    }

    public void deleteItemAndReorder(final FanMenuItemView child, final int index) {
        mIsDeleting = true;

        final int count = getChildCount();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f, 0f);
        valueAnimator.setDuration(150);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                child.setScaleX(values);
                child.setScaleY(values);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ArrayList<View> delFrom = new ArrayList<>();
                ArrayList<Point> delTo = new ArrayList<>();
                Point p;
                for (int i = 0; i < count; i++) {
                    View temp = getChildAt(i);
                    if (i != index) {
                        delFrom.add(temp);

                        p = new Point();
                        p.x = isLeft() ? 0 : getWidth();
                        p.y = getHeight();

                        getItemCenterPoint(delTo.size(), count - 1, p);
                        p.x -= mItemHalfWidth;
                        p.y -= mItemHalfWidth;

                        p.x -= temp.getX();
                        p.y -= temp.getY();
                        delTo.add(p);
                    }
                }

                deleteViewByAnimator(delFrom, delTo, child);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
    }

    public void deleteViewByAnimator(final ArrayList<View> delFrom, final ArrayList<Point> delTo, final FanMenuItemView child) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1.0f);
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                for (int i = 0; i < delFrom.size(); i++) {
                    View temp = delFrom.get(i);
                    float x = delTo.get(i).x * values;
                    float y = delTo.get(i).y * values;

                    temp.setTranslationX(x);
                    temp.setTranslationY(y);
                }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                for (View temp : delFrom) {
                    temp.setTranslationX(0);
                    temp.setTranslationY(0);
                }
                FanMenuLayout.this.removeView(child);

                if (getChildCount() == 1 && !(getChildAt(0) instanceof FanMenuItemView) && mStateChangeable != null) {
                    mStateChangeable.longClickStateChange(null, false);
                    endEditModel();
                }

                mIsDeleting = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mDisableTouchEvent) {
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    public void changeTwoChildPosition(final FanMenuItemView from, final AppInfo toApp) {
        final float fromTranX = from.getTranslationX();
        final float fromTranY = from.getTranslationY();
        final float offsetX = mLastSlot[1] - (from.getLeft() + fromTranX);
        final float offsetY = mLastSlot[2] - (from.getTop() + fromTranY);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1.0f);
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                from.setTranslationX(fromTranX + offsetX * values);
                from.setTranslationY(fromTranY + offsetY * values);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                List<AppInfo> datas = getData();
                AppInfo fromApp = (AppInfo) from.getTag();
                int fromIndex = datas.indexOf(fromApp);
                int toIndex = datas.indexOf(toApp);
                datas.set(toIndex, fromApp);
                datas.set(fromIndex, toApp);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
    }

    public void addMirrorViewAndDrag(float x, float y) {
        if (mDownSelectChild == null || mStateChangeable == null) {
            return;
        }

        x += mDownSelectChild.getTranslationX();
        y += mDownSelectChild.getTranslationY();

        if (mDownSelectChild.getVisibility() != GONE) {
            AppInfo appInfo = (AppInfo) mDownSelectChild.getTag();
            mStateChangeable.dragViewAndRefresh(getLeft() + mDownSelectChild.getLeft() + x,
                    getTop() + mDownSelectChild.getTop() + y, appInfo, false, mMenuType == CardState.CARD_STATE_TOOLBOX);
            mDownSelectChild.setVisibility(GONE);

            mLastSlot[0] = indexOfChild(mDownSelectChild);
            mLastSlot[1] = mDownSelectChild.getLeft();
            mLastSlot[2] = mDownSelectChild.getTop();

            int count = getChildCount();
            for (int i = 0; i < mLastSlotView.length; i++) {
                if (i < count) {
                    View temp = getChildAt(i);
                    if (temp instanceof FanMenuItemView)
                        mLastSlotView[i] = (FanMenuItemView) temp;
                    else
                        mLastSlotView[i] = null;
                } else {
                    mLastSlotView[i] = null;
                }
            }
            initDragIndexRect();
        } else {
            mStateChangeable.dragViewAndRefresh(getLeft() + mDownSelectChild.getLeft() + x,
                    getTop() + mDownSelectChild.getTop() + y, null, false, mMenuType == CardState.CARD_STATE_TOOLBOX);
        }


        int selectIndex = getChildByPoint(mDownSelectChild.getLeft() + mItemHalfWidth + (int) x,
                mDownSelectChild.getTop() + mItemHalfWidth + (int) y);
        if (selectIndex >= 0 && selectIndex < mLastSlotView.length && mLastSlotView[selectIndex] != null) {
            if (selectIndex == mLastSlot[0]) {
                return;
            }
            Log.v(TAG, "addMirrorView getChildByPoint child:" + ((AppInfo) mLastSlotView[selectIndex].getTag()).getAppLabel());
            FanMenuItemView from = mLastSlotView[selectIndex];
            changeTwoChildPosition(from, getData().get(mLastSlot[0]));
            FanMenuItemView temp = mLastSlotView[mLastSlot[0]];
            mLastSlotView[mLastSlot[0]] = from;
            mLastSlotView[selectIndex] = temp;
            mLastSlot[0] = selectIndex;
            mLastSlot[1] = mDragIndexPoint[selectIndex].left;
            mLastSlot[2] = mDragIndexPoint[selectIndex].top;
        }
    }

    public void restoreDragView(final float x, final float y) {
        if (mStateChangeable == null) {
            return;
        }

        final float offsetX = mLastSlot[1] - (mDownSelectChild.getLeft() + x);
        final float offsetY = mLastSlot[2] - (mDownSelectChild.getTop() + y);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1.0f);
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                mStateChangeable.dragViewAndRefresh(getLeft() + mDownSelectChild.getLeft() + offsetX * values + x,
                        getTop() + mDownSelectChild.getTop() + offsetY * values + y, null, false,
                        mMenuType == CardState.CARD_STATE_TOOLBOX);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mStateChangeable.dragViewAndRefresh(getLeft() + mLastSlot[1], getTop() + mLastSlot[2], null, true,
                        mMenuType == CardState.CARD_STATE_TOOLBOX);
                mDownSelectChild.setVisibility(VISIBLE);

                View add = findViewWithTag(ADD_VIEW_TAG);
                removeAllViews();
                for (int i = 0; i < mLastSlotView.length; i++) {
                    View temp = mLastSlotView[i];
                    if (temp != null) {
                        temp.setTranslationX(0);
                        temp.setTranslationY(0);
                        addView(temp);
                    }
                }
                addView(add);
                requestLayout();
                mIsDragging = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.start();
    }

    public void initDragIndexRect() {
        int count = getChildCount();
        count = count > 9 ? 9 : count;

        Rect r;
        for (int i = 0; i < count; i++) {
            Point p = new Point();
            p.x = isLeft() ? 0 : getWidth();
            p.y = getHeight();
            getItemCenterPoint(i, count, p);
            r = new Rect();
            r.left = p.x - mItemHalfWidth;
            r.top = p.y - mItemHalfWidth;
            r.right = p.x + mItemHalfWidth;
            r.bottom = p.y + mItemHalfWidth;
            if (i < mDragIndexPoint.length) {
                mDragIndexPoint[i] = r;
            }
        }
    }

    public int getChildByPoint(float x, float y) {
        for (int i = 0; i < mDragIndexPoint.length; i++) {
            Rect r = mDragIndexPoint[i];
            if (isPointInRect(r, x, y)) {
                return i;
            }
        }

        return -1;
    }

    public boolean isPointInRect(Rect r, float x, float y) {
        return x > r.left && x < r.right && y > r.top && y < r.bottom;
    }

    public void cancelEditModel() {
        endEditModel();
    }

    public void startEditModel() {
        Log.v(TAG, "startEditModel");
        mIsEditModel = true;
        List<AppInfo> data = getData();
        for (AppInfo appInfo : data) {
            FanMenuItemView child = (FanMenuItemView) this.findViewWithTag(appInfo);
            child.showDelBtn();
        }
    }

    public void endEditModel() {
        Log.v(TAG, "endEditModel");
        mIsEditModel = false;
        List<AppInfo> data = getData();
        for (AppInfo appInfo : data) {
            FanMenuItemView child = (FanMenuItemView) this.findViewWithTag(appInfo);
            child.hideDelBtn();
        }
    }

    private class LongClickRunnable implements Runnable {
        @Override
        public void run() {
            if (mStateChangeable != null) {
                mStateChangeable.longClickStateChange(FanMenuLayout.this, true);
                startEditModel();

                mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {0, 45};
                mVibrator.vibrate(pattern, -1);
            }
        }
    }
}
