package com.jeden.fanmenu.common.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeCalculator extends SwipeTools {
    private volatile static SwipeCalculator mInstance;

    private SwipeCalculator(Context context) {
    }

    public static SwipeCalculator getInstance(Context context) {
        if (mInstance == null) {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context) {
        mInstance = new SwipeCalculator(context);
    }

    @Override
    public void viewBind(Context context) {

    }

    @Override
    public void changeState(Context context) {
        tryStartCalculator(context);
        mCloseAfterChange = true;
    }

    private boolean tryStartCalculator(Context context) {
        try {
            PackageManager e = context.getPackageManager();
            List packageInfos = e.getInstalledPackages(0);
            if (packageInfos == null) {
                return false;
            } else {
                Iterator var3 = packageInfos.iterator();

                while (var3.hasNext()) {
                    PackageInfo pi = (PackageInfo) var3.next();
                    if (pi != null && pi.packageName != null && pi.packageName.toLowerCase(Locale.US).contains("calcul")) {
                        Intent i = e.getLaunchIntentForPackage(pi.packageName);
                        if (i != null) {
                            i.setFlags(268435456);
                            context.startActivity(i);
                            break;
                        }
                    }
                }

                return true;
            }
        } catch (Exception var6) {
            var6.printStackTrace();
            return false;
        }
    }
}
