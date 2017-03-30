package com.jeden.fanmenu.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.bean.AppInfo;
import com.jeden.fanmenu.common.FanMenuManager;
import com.jeden.fanmenu.common.model.FanMenuConfig;
import com.jeden.fanmenu.common.FanMenuViewTools;
import com.jeden.fanmenu.common.tools.SwipeTools;
import com.jeden.fanmenu.common.tools.ToolboxHelper;
import com.jeden.fanmenu.view.base.MenuLayoutStateChangeable;
import com.jeden.fanmenu.view.base.PositionState;
import com.jeden.fanmenu.view.base.CardState;

/**
 * Created by jeden on 2017/3/14.
 */

public class FanRootView extends FrameLayout {
    private static final String TAG = FanRootView.class.getSimpleName();

    private static final double MIN_SLID_DEGREE = 20;
    private static final int MIN_SLID_SPEED = 3000;

    private int mPositionState = -1;
    private int mSelectTextIndex = -1;

    private FanBackgroundView mBackgroundView;
    private FanCenterView mCenterView;
    private FanSelectTextLayout mSelectTextView;
    private FanMenuView mMenuView;
    private FanSelectTextIndicator mTextIndicator;

    /**
     * 容器的宽高
     */
    private int mWidth;
    private int mHeight;

    private int mSlideType = SLID_OVER;
    private static final int SLID_OVER = 0;
    private static final int SLIDING = 1;

    private int mEditState = STATE_NORMAL;
    private static final int STATE_NORMAL = 0;
    private static final int STATE_EDIT = 1;
    private FanMenuLayout mEditView;

    private float mDownInScreenX;
    private float mDownInScreenY;
    private int mTouchSlop;
    private long mLastTime;

    private double mDownAngle;

    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity;

    private FanMenuItemView mMirrorView;

    public FanRootView(Context context) {
        super(context);
        initView(context);
    }

    public FanRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FanRootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    public void initView(Context context) {
        ViewConfiguration mConfig = ViewConfiguration.get(context);
        mTouchSlop = mConfig.getScaledTouchSlop();
        mMaximumVelocity = mConfig.getScaledMaximumFlingVelocity();
    }

    public void initFanMenuConfig() {
        FanMenuConfig config = FanMenuConfig.getMenuConfig();
        updateFanMenuPositionState(config.getPositionState());

        updateFanMenuSelectIndex(config.getCardIndex(), 0);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBackgroundView = (FanBackgroundView) findViewById(R.id.fanmenu_background_view_id);
        mCenterView = (FanCenterView) findViewById(R.id.fanmenu_center_view_id);
        mSelectTextView = (FanSelectTextLayout) findViewById(R.id.fanmenu_select_text_layout_id);
        mMenuView = (FanMenuView) findViewById(R.id.fanmenu_menu_view_id);
        mTextIndicator = (FanSelectTextIndicator) findViewById(R.id.fanmenu_select_text_indicator_id);

        mSelectTextView.setMenuStateChangeListener(menuStateChangeListener);
        mMenuView.setMenuStateChangeListener(menuStateChangeListener);

        int halfWidth = getContext().getResources().getDimensionPixelSize(R.dimen.fan_menu_item_half_width);
        mMirrorView = (FanMenuItemView) LayoutInflater.from(getContext()).inflate(R.layout.fan_menu_item_layout, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(halfWidth * 2, halfWidth * 2);
        addView(mMirrorView, lp);

        mMirrorView.setVisibility(GONE);
    }

    public double pointAngle(float x, float y) {
        if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
            x = mWidth - x;
        }

        y = mHeight - y;
        return FanMenuViewTools.getDegreeByPoint(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVeloCityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownInScreenX = event.getX();
                mDownInScreenY = event.getY();
                mLastTime = System.currentTimeMillis();

                mDownAngle = pointAngle(mDownInScreenX, mDownInScreenY);
                break;
            case MotionEvent.ACTION_MOVE:
                float newX = event.getX();
                float newY = event.getY();

                if (newY >= mHeight) {
                    newY = mHeight;
                }

                if (mEditState == STATE_EDIT) {
                    break;
                }

                if ((Math.abs(newX - mDownInScreenX) > mTouchSlop
                        || Math.abs(newY - mDownInScreenY) > mTouchSlop)) {
                    mSlideType = SLIDING;
                }

                if (mSlideType == SLIDING) {
                    slidCardAndMove(newX, newY);
                }

                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();

                if (upY >= mHeight) {
                    upY = mHeight;
                }

                long upTime = System.currentTimeMillis();

                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float vx = mVelocityTracker.getXVelocity();
                float vy = mVelocityTracker.getYVelocity();
                recyleVelocityTracker();
                if (mSlideType == SLIDING) {
                    checkAndMoveToPosition(upX, upY, vx, vy);
                    break;
                }

                if (!isPointInMenuView(upX, upY)) {
                    if (mSlideType == SLID_OVER && upTime - mLastTime < 400 && (Math.abs(upX - mDownInScreenX) < mTouchSlop
                            && Math.abs(upY - mDownInScreenY) < mTouchSlop)) {
                        if (mEditState == STATE_EDIT) {
                            mEditState = STATE_NORMAL;
                            mEditView.cancelEditModel();
                            break;
                        }

                        removeFanMenuView();
                    }
                }
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownInScreenX = ev.getX();
                mDownInScreenY = ev.getY();
                mLastTime = System.currentTimeMillis();
                mDownAngle = pointAngle(mDownInScreenX, mDownInScreenY);

                if (mEditState == STATE_EDIT && !isPointInMenuView(mDownInScreenX, mDownInScreenY)) {
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:

                if (mEditState == STATE_EDIT) {
                    return false;
                }
                if (Math.abs(mDownInScreenX - ev.getX()) > mTouchSlop
                        || Math.abs(mDownInScreenY - ev.getY()) > mTouchSlop) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mEditState == STATE_EDIT && !isPointInMenuView(mDownInScreenX, mDownInScreenY)) {
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void updateFanMenuSelectIndex(int selectState, float offset) {
        if (selectState < CardState.CARD_STATE_RECENTLY) {
            selectState = CardState.CARD_STATE_FAVORITE;
        } else if (selectState > CardState.CARD_STATE_FAVORITE) {
            selectState = CardState.CARD_STATE_RECENTLY;
        }
        mSelectTextIndex = selectState;

        mSelectTextView.setRotateView(this.mSelectTextIndex, offset);
        mTextIndicator.setRotateView(this.mSelectTextIndex, offset);
        mMenuView.setRotateView(this.mSelectTextIndex, offset);

        FanMenuConfig.getMenuConfig().setCardIndex(mSelectTextIndex);
    }

    public void updateFanMenuPositionState(int positionState) {
        mPositionState = positionState;

        mBackgroundView.setPositionState(mPositionState);
        mCenterView.setPositionState(mPositionState);
        mSelectTextView.setPositionState(mPositionState);
        mMenuView.setPositionState(mPositionState);
        mTextIndicator.setPositionState(mPositionState);
        requestLayout();
    }

    public void removeFanMenuView() {
        closeFanMenu();
    }

    MenuLayoutStateChangeable menuStateChangeListener = new MenuLayoutStateChangeable() {
        @Override
        public void selectCardChange(int selectState) {
            if (mEditState == STATE_EDIT) {
                return;
            }
            scrollToState(mSelectTextIndex, selectState, 0);
        }

        @Override
        public void longClickStateChange(View view, boolean isEditModel) {
            mEditState = isEditModel ? STATE_EDIT : STATE_NORMAL;
            if (view != null && view instanceof FanMenuLayout)
                mEditView = (FanMenuLayout) view;
        }

        @Override
        public void dragViewAndRefresh(float x, float y, AppInfo appInfo, boolean hidden, boolean isToolbox) {
            if (hidden) {
                mMirrorView.setVisibility(GONE);
                return;
            }

            if (mMirrorView.getVisibility() == GONE) {
                mMirrorView.setVisibility(View.VISIBLE);
                mMirrorView.setToolboxModel(isToolbox);
                mMirrorView.setItemIcon(appInfo.getAppIcon());
                mMirrorView.setTitle(appInfo.getAppLabel());
                mMirrorView.showDelBtn();
            }

            x += mMenuView.getLeft();
            y += mMenuView.getTop();

            mMirrorView.setTranslationX(x);
            mMirrorView.setTranslationY(y);
        }

        @Override
        public void addBtnClicked(final View view, int selectCard) {
            FanMenuManager.showDialog(getContext(), selectCard, new FanMenuDialog.DialogSubmitListener() {
                @Override
                public void dialogSubmit() {
                    ((FanMenuLayout) view).refreshData();
                }
            });
        }

        @Override
        public void menuItemClicked(View view, AppInfo appInfo) {
            Log.v(TAG, "menuItemClicked appInfo:" + appInfo);
            if (switchOrStartActivity(view, appInfo)) {
                FanMenuManager.closeFanMenu(getContext());
            }
        }
    };

    public boolean switchOrStartActivity(View view, AppInfo appInfo) {
        if (appInfo == null) {
            return false;
        }

        Intent intent = appInfo.getIntent();
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            return true;
        }

        SwipeTools tools = ToolboxHelper.checkSwipeTools(getContext(), appInfo);
        if (tools != null) {
            return tools.changeStateWithResult(getContext());
        }
        return false;
    }

    public void scrollToState(final int fromeState, final int toState, float offset) {
        float temp = toState - fromeState;
        ValueAnimator va = ValueAnimator.ofFloat(offset, temp);
        va.setDuration(250);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (value > 0 && (value > 0.98 && value < 1.02)) {
                    value = 1;
                } else if (value < 0 && (value < -0.98 && value > -1.02)) {
                    value = -1;
                }

                if (value >= 1) {
                    updateFanMenuSelectIndex(fromeState + 1, value - 1);
                } else if (value <= -1) {
                    updateFanMenuSelectIndex(fromeState - 1, value + 1);
                } else {
                    updateFanMenuSelectIndex(fromeState, value);
                }
            }
        });

        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                updateFanMenuSelectIndex(toState, 0.0f);

                if (mSlideType == SLIDING) {
                    mSlideType = SLID_OVER;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        va.start();
    }

    public boolean isPointInMenuView(float x, float y) {
        float upDistance = 0;
        if (mPositionState == PositionState.POSITION_STATE_LEFT) {
            upDistance = FanMenuViewTools.getTwoPointDistance(x, y, 0, mHeight);
        } else if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
            upDistance = FanMenuViewTools.getTwoPointDistance(x, y, mWidth, mHeight);
        }

        int centerViewH = mCenterView.getMeasuredHeight();
        int menuViewH = mMenuView.getMeasuredHeight();
        if (upDistance < centerViewH || upDistance > menuViewH) {
            return false;
        }
        return true;
    }

    public void slidCardAndMove(float x, float y) {
        double newAngle = pointAngle(x, y);
        float offset = (float) ((newAngle - mDownAngle) / 90.0);

        updateFanMenuSelectIndex(mSelectTextIndex, offset);
    }

    public void checkAndMoveToPosition(float x, float y, float vx, float vy) {
        double newAngle = pointAngle(x, y);
        double temp = newAngle - mDownAngle;
        float tempSpeed = mPositionState == PositionState.POSITION_STATE_LEFT ? vy + vx : vy - vx;
        if (temp > MIN_SLID_DEGREE || tempSpeed > MIN_SLID_SPEED) {
            scrollToState(mSelectTextIndex, mSelectTextIndex + 1, (float) (temp / 90.0));
        } else if (temp < -MIN_SLID_DEGREE || tempSpeed < -MIN_SLID_SPEED) {
            scrollToState(mSelectTextIndex, mSelectTextIndex - 1, (float) (temp / 90.0));
        } else {
            scrollToState(mSelectTextIndex, mSelectTextIndex, (float) (temp / 90.0));
        }
    }

    /**
     * 初始化VelocityTracker
     *
     * @param event 滑动时的手势
     */
    private void initVeloCityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker
     */
    private void recyleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void closeFanMenu() {
        ValueAnimator va = ValueAnimator.ofFloat(1.0f, 0f);
        va.setDuration(500);
        va.setInterpolator(new AnticipateOvershootInterpolator(0.9f));
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                scaleChild(value);
            }
        });

        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                FanMenuManager.closeFanMenu(getContext());
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        va.start();
    }

    public void showFanMenu() {
        final ValueAnimator va = ValueAnimator.ofFloat(0f, 1.0f);
        va.setDuration(500);
        va.setInterpolator(new OvershootInterpolator(1.2f));
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                scaleChild(value);
            }
        });
        va.start();
    }

    public void scaleChild(float offset) {
        mBackgroundView.setAlpha(((int) (offset * 10) / 10f));
        mCenterView.setScaleX(offset);
        mSelectTextView.setScaleX(offset);
        mMenuView.setScaleX(offset);
        mCenterView.setScaleY(offset);
        mSelectTextView.setScaleY(offset);
        mMenuView.setScaleY(offset);
    }
}
