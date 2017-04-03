package fanmenu.jeden.com.fanmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jeden.fanmenu.services.FanMenuSDK;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    public void initView() {
        Button btn1 = (Button) findViewById(R.id.myfan_show_button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new FanMenuManager(MainActivity.this, getWindowManager()).showFanMenu();
                FanMenuSDK.showFlowing();
            }
        });
        Button btn2 = (Button) findViewById(R.id.myfan_hide_button);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new FanMenuManager(MainActivity.this, getWindowManager()).showFanMenu();
                FanMenuSDK.hideFlowing();
            }
        });
    }
}
