package fanmenu.jeden.com.fanmenu;

import android.app.Application;
import android.content.Context;

import com.jeden.fanmenu.services.FanMenuSDK;

/**
 * Created by jeden on 2017/3/22.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FanMenuSDK.initSDK(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
