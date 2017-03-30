package com.jeden.fanmenu.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.services.FanMenuSDK;

public class FanMenuSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fan_menu_setting_layout);

        initView();
    }

    private void initView()
    {
        ImageView back = (ImageView) findViewById(R.id.fan_setting_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Switch switchMenu = (Switch) findViewById(R.id.fan_setting_switch);
        switchMenu.setChecked(FanMenuSDK.isFlowingShow());
        switchMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    FanMenuSDK.showFlowing();
                }
                else
                {
                    FanMenuSDK.hideFlowing();
                }
            }
        });
    }
}
