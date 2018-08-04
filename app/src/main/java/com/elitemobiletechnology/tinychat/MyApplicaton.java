package com.elitemobiletechnology.tinychat;

import android.app.Application;
import android.content.Context;

/**
 * Created by SteveYang on 17/1/22.
 */

public class MyApplicaton extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this.getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
