package com.jeden.fanmenudemo.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jeden.fanmenudemo.R;
import com.jeden.fanmenudemo.services.MyService;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    public void initView(){
        Button btn = (Button)findViewById(R.id.myfan_show_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new MyCustomMenuManager(MainActivity.this, getWindowManager()).showFanMenu();
                Intent intent = new Intent(MainActivity.this, MyService.class);
                startService(intent);
            }
        });
    }
}
