package com.jeden.fanmenu.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.bean.AppInfo;
import com.jeden.fanmenu.common.FanMenuViewTools;
import com.jeden.fanmenu.common.model.ContentProvider;
import com.jeden.fanmenu.util.FanLog;
import com.jeden.fanmenu.view.base.CardState;
import com.jeden.fanmenu.view.base.CommonPositionViewGroup;

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
    private int[] mTempP = new int[2];

    private Drawable mCache;
    private boolean mRotating = false;
    private int mBackgroundColor;

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
        mBackgroundColor = rs.getColor(R.color.fan_menu_common_transparent_color);

        ViewConfiguration mConfig = ViewConfiguration.get(context);
        mTouchSlop = mConfig.getScaledTouchSlop();
//        mTouchSlop = FanMenuViewTools.dip2px(context, 5);

        setPersistentDrawingCache(PERSISTENT_ALL_CACHES);
//        setDrawingCacheEnabled(true);
        setDrawingCacheBackgroundColor(0xFF000000);
    }

    public void addAllItemView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        FanMenuItemView itemView;

        List<AppInfo> tempAppInfo = getData();

        for (AppInfo info : tempAppInfo) {
//            itemView = (FanMenuItemView) inflater.inflate(R.layout.fan_menu_item_layout, null);
            itemView = new FanMenuItemView(getContext());
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

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            mTempP[0] = isLeft() ? 0 : getWidth();
            mTempP[1] = getHeight();

            getItemCenterPoint(i, count, mTempP);
            child.layout(mTempP[0] - mItemHalfWidth, mTempP[1] - mItemHalfWidth, mTempP[0] + mItemHalfWidth, mTempP[1] + mItemHalfWidth);
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

    public void getItemCenterPoint(int index, int count, int[] p) {

        int radius = index >= 4 ? mItemOuterRadius : mItemInnerRadius;

        double degree = generateDegreePerItem(index, count);

        int x = (int) (Math.sin(degree) * radius);
        p[1] -= (int) (Math.cos(degree) * radius);
        p[0] += (isLeft() ? x : -x);
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

                    if (mIsEditModel && mDownSelectChild != null && mDownType == DOWN_NULL) {
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
        Rect delete = v.getDeleteRect();
        if (x > delete.left && x < delete.right && y > delete.top && y < delete.bottom) {
            return true;
        }

        return false;
    }

    public void refreshData() {
        removeAllViews();
        addAllItemView();
        requestLayout();
        mCache = null;
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
                ArrayList<int[]> delTo = new ArrayList<>();
                int[] p;
                for (int i = 0; i < count; i++) {
                    View temp = getChildAt(i);
                    if (i != index) {
                        delFrom.add(temp);

                        p = new int[2];
                        p[0] = isLeft() ? 0 : getWidth();
                        p[1] = getHeight();

                        getItemCenterPoint(delTo.size(), count - 1, p);
                        p[0] -= mItemHalfWidth;
                        p[1] -= mItemHalfWidth;

                        p[0] -= temp.getX();
                        p[1] -= temp.getY();
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

    public void deleteViewByAnimator(final ArrayList<View> delFrom, final ArrayList<int[]> delTo, final FanMenuItemView child) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1.0f);
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                for (int i = 0; i < delFrom.size(); i++) {
                    View temp = delFrom.get(i);
                    float x = delTo.get(i)[0] * values;
                    float y = delTo.get(i)[1] * values;

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
            if(mLastSlot[0] == -1) {
                return;
            }
            if (selectIndex == mLastSlot[0]) {
                return;
            }
            FanLog.v(TAG, "addMirrorView getChildByPoint child:" + ((AppInfo) mLastSlotView[selectIndex].getTag()).getAppLabel());
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
            int[] p = new int[2];
            p[0] = isLeft() ? 0 : getWidth();
            p[1] = getHeight();
            getItemCenterPoint(i, count, p);
            r = new Rect();
            r.left = p[0] - mItemHalfWidth;
            r.top = p[1] - mItemHalfWidth;
            r.right = p[0] + mItemHalfWidth;
            r.bottom = p[1] + mItemHalfWidth;
            if (i < mDragIndexPoint.length) {
                mDragIndexPoint[i] = r;
            }
        }
    }

    public int getChildByPoint(float x, float y) {
        for (int i = 0; i < mDragIndexPoint.length; i++) {
            Rect r = mDragIndexPoint[i];
            if (r != null && isPointInRect(r, x, y)) {
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
        FanLog.v(TAG, "startEditModel");
        mIsEditModel = true;
        List<AppInfo> data = getData();
        for (AppInfo appInfo : data) {
            FanMenuItemView child = (FanMenuItemView) this.findViewWithTag(appInfo);
            child.showDelBtn();
        }
    }

    public void endEditModel() {
        FanLog.v(TAG, "endEditModel");
        mIsEditModel = false;
        List<AppInfo> data = getData();
        for (AppInfo appInfo : data) {
            FanMenuItemView child = (FanMenuItemView) this.findViewWithTag(appInfo);
            child.hideDelBtn();
        }

        updateCacheDrawable();
    }

    @Override
    public void setPositionState(int state) {
        super.setPositionState(state);
        mCache = null;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP &&
                mMenuType == CardState.CARD_STATE_RECENTLY) {
            ContentProvider.getInstance().updateRecently(getContext());
            refreshData();
        }
    }

    public void updateCacheDrawable() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        FanLog.v(TAG, "updateCacheDrawable");
        Bitmap cache = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(cache);
        draw(c);
        mCache = new BitmapDrawable(cache);
    }

    public Drawable getCacheDrawable() {
        if (mCache == null) {
            updateCacheDrawable();
        }
        return mCache;
    }

    public void setRotateStart() {
        if (mRotating) {
            return;
        }

        Drawable cache = getCacheDrawable();
        if (cache == null) {
            return;
        }

        setBackgroundDrawable(cache);
        mRotating = true;
        setChildVisible(false);
    }

    public void setRotateEnd() {
        if (!mRotating) {
            return;
        }

        setBackgroundColor(mBackgroundColor);
        mRotating = false;
        setChildVisible(true);
    }

    private void setChildVisible(boolean visible) {
        int count = getChildCount();
        int v = visible ? VISIBLE : GONE;
        for (int i = 0; i < count; i++) {
            getChildAt(i).setVisibility(v);
        }
    }

    private class LongClickRunnable implements Runnable {
        @Override
        public void run() {
            if (mStateChangeable != null && !mDisableTouchEvent) {
                mStateChangeable.longClickStateChange(FanMenuLayout.this, true);
                startEditModel();

                mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {0, 45};
                mVibrator.vibrate(pattern, -1);
            }
        }
    }
}
