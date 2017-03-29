package com.jeden.fanmenu.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeden.fanmenu.R;

/**
 * Created by jeden on 2017/3/22.
 */

public class FanMenuDialogItemView extends RelativeLayout implements View.OnClickListener{
    private ImageView mIcon;
    private ImageView mCircle;
    private TextView mTitle;
    private boolean mIsSelected = false;
    private boolean mToolboxModel = false;
    private DialogItemSelectChange mChangeListener;

    public FanMenuDialogItemView(Context context) {
        super(context);
    }

    public FanMenuDialogItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FanMenuDialogItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mIcon = (ImageView) findViewById(R.id.fan_dialog_item_icon);
        mCircle = (ImageView) findViewById(R.id.fan_dialog_item_circle);
        mTitle = (TextView) findViewById(R.id.fan_dialog_item_title);
        setOnClickListener(this);
    }

    public void setSelected(boolean selected)
    {
        mIsSelected = selected;
        if(mIsSelected)
        {
            mCircle.setImageResource(R.drawable.fan_dialog_item_circle);
        }
        else
        {
            mCircle.setImageDrawable(null);
        }
    }

    public void setIcon(Drawable drawable)
    {
        mIcon.setImageDrawable(drawable);
    }

    public void setTitle(String title)
    {
        mTitle.setText(title);
    }

    public void setToolboxModel(boolean toolboxModel)
    {
        mToolboxModel = toolboxModel;
        if(mToolboxModel)
        {
            mIcon.setBackgroundResource(R.drawable.fan_item_icon_bg);
            mIcon.setScaleType(ImageView.ScaleType.CENTER);
        }
        else
        {
            mIcon.setBackgroundDrawable(null);
            mIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    public void setSelectChangeListener(DialogItemSelectChange listener)
    {
        mChangeListener = listener;
    }

    @Override
    public void onClick(View v) {
        boolean canChange = true;
        if(mChangeListener != null)
        {
            canChange = mChangeListener.selectChangeable(this, !mIsSelected);
        }
        if(canChange)
        setSelected(!mIsSelected);
    }

    public interface DialogItemSelectChange{
        boolean selectChangeable(View view, boolean isSelect);
    }
}
