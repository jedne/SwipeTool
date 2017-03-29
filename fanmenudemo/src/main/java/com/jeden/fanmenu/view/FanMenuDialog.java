package com.jeden.fanmenu.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.bean.AppInfo;
import com.jeden.fanmenu.common.FanMenuManager;
import com.jeden.fanmenu.common.model.ContentProvider;
import com.jeden.fanmenu.view.base.CardState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeden on 2017/3/22.
 */

public class FanMenuDialog extends RelativeLayout implements View.OnClickListener{

    private static final String TAG = FanMenuDialog.class.getSimpleName();

    private RelativeLayout mBackground;
    private LinearLayout mDialogLayout;
    private TextView mTitle;
    private TextView mSelectNum;
    private TextView mCancel;
    private TextView mSubmit;
    private GridView mContentView;

    private List<AppInfo> mListItems;
    private List<AppInfo> mSelectItems;
    private List<AppInfo> mTempSelect = new ArrayList<>();

    private boolean mToolboxModel = false;

    private DialogSubmitListener mSubmitListener;

    public FanMenuDialog(Context context) {
        super(context);
    }

    public FanMenuDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FanMenuDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBackground = (RelativeLayout) findViewById(R.id.fanmenu_dialog_bg);
        mDialogLayout = (LinearLayout) findViewById(R.id.fanmenu_dialog_layout);
        mTitle = (TextView) findViewById(R.id.fanmenu_dialog_title);
        mSelectNum = (TextView) findViewById(R.id.fanmenu_dialog_select_nun);
        mCancel = (TextView) findViewById(R.id.fanmenu_dialog_cancel);
        mSubmit = (TextView) findViewById(R.id.fanmenu_dialog_submit);
        mContentView = (GridView) findViewById(R.id.fanmenu_dialog_content);

        mCancel.setOnClickListener(this);
        mSubmit.setOnClickListener(this);
    }

    public void initView(int dialogtype, DialogSubmitListener listener)
    {
        mSubmitListener = listener;
        Resources rs = getResources();
        if(dialogtype == CardState.CARD_STATE_TOOLBOX)
        {
            mToolboxModel = true;
            mTitle.setText(rs.getString(R.string.fan_menu_dialog_toolbox_title));
            mListItems = ContentProvider.getInstance().getAllToolBox();
            mSelectItems = ContentProvider.getInstance().getToolBox();
        }
        else
        {
            mToolboxModel = false;
            mTitle.setText(rs.getString(R.string.fan_menu_dialog_favorite_title));
            mListItems = ContentProvider.getInstance().getAllApps();
            mSelectItems = ContentProvider.getInstance().getFavorite();
        }

        mTempSelect.clear();
        for(AppInfo appInfo : mSelectItems)
        {
            mTempSelect.add(appInfo);
            mListItems.remove(appInfo);
        }

        for(AppInfo appInfo : mSelectItems)
        {
            mListItems.add(0, appInfo);
        }

        mSelectNum.setText("(" + mTempSelect.size() + "/9)");

        mContentView.setAdapter(new GridViewAdapter(getContext()));
    }

    private class GridViewAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        GridViewAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (mListItems != null) {
                return mListItems.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            if (mListItems != null) {
                return mListItems.get(i);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            AppInfo appInfo = mListItems.get(i);
            FanMenuDialogItemView item;
            if (view == null) {
                item = (FanMenuDialogItemView) mInflater.inflate(R.layout.fan_dialog_item_layout, null);
                item.setToolboxModel(mToolboxModel);
            } else {
                item = (FanMenuDialogItemView) view;
            }

            item.setIcon(appInfo.getAppIcon());
            item.setTitle(appInfo.getAppLabel());
            item.setSelected(mTempSelect.contains(appInfo));

            item.setTag(appInfo);

            item.setSelectChangeListener(new FanMenuDialogItemView.DialogItemSelectChange() {
                @Override
                public boolean selectChangeable(View view, boolean isSelect) {
                    if(isSelect && mTempSelect.size() >= 9)
                    {
//                        Toast.makeText(getContext(), "最多只能选9个", Toast.LENGTH_SHORT).show();
                        FanMenuManager.showToast(getContext(), "最多只能选9个");
                        return false;
                    }
                    AppInfo appInfo = (AppInfo) view.getTag();
                    if(isSelect && !mTempSelect.contains(appInfo))
                    {
                        mTempSelect.add(appInfo);
                    }
                    else if(!isSelect && mTempSelect.contains(appInfo))
                    {
                        mTempSelect.remove(appInfo);
                    }

                    mSelectNum.setText("(" + mTempSelect.size() + "/9)");
                    return true;
                }
            });
            return item;
        }
    }

    public void closeDialog()
    {
        ValueAnimator va = ValueAnimator.ofFloat(1.0f, 0f);
        va.setDuration(500);
        va.setInterpolator(new AnticipateOvershootInterpolator(0.9f));
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                mBackground.setAlpha(((int) (value * 10) / 10f));
                mDialogLayout.setScaleX(value);
                mDialogLayout.setScaleY(value);
            }
        });

        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                FanMenuManager.closeDialog(getContext());
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

    public void showDialog(){
        final ValueAnimator va = ValueAnimator.ofFloat(0f, 1.0f);
        va.setDuration(500);
        va.setInterpolator(new OvershootInterpolator(1.2f));
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBackground.setAlpha(((int) (value * 10) / 10f));
                mDialogLayout.setScaleX(value);
                mDialogLayout.setScaleY(value);
            }
        });
        va.start();
    }

    @Override
    public void onClick(View v) {
        if(v == mCancel)
        {
            closeDialog();
        }
        else if(v == mSubmit)
        {
            closeDialog();
            mSelectItems.clear();
            for(AppInfo appInfo : mTempSelect)
            {
                mSelectItems.add(appInfo);
            }
            if(mToolboxModel)
            {
                ContentProvider.getInstance().saveToolbox();
            }
            else
            {
                ContentProvider.getInstance().saveFavorite();
            }

            if(mSubmitListener != null)
            {
                mSubmitListener.dialogSubmit();
            }
        }
    }

    public interface DialogSubmitListener{
        void dialogSubmit();
    }
}
