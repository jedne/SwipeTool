package com.jeden.fanmenudemo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeden.fanmenudemo.R;

/**
 * Created by Administrator on 2017/3/12.
 */

public class MyCustomMenuItemView extends RelativeLayout {

    private ImageView mDelIcon;
    private ImageView mIcon;
    private TextView mTitle;

    public MyCustomMenuItemView(Context context) {
        super(context);
    }

    public MyCustomMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCustomMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDelIcon = (ImageView) findViewById(R.id.fan_item_delete);
        mIcon = (ImageView) findViewById(R.id.fan_item_icon);
        mTitle = (TextView) findViewById(R.id.fan_item_title);
    }

    public void showDelBtn() {
        mDelIcon.setVisibility(View.VISIBLE);
    }

    public void hideDelBtn() {
        mDelIcon.setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public String getTitle() {
        return mTitle.getText().toString();
    }

    public void setItemIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
