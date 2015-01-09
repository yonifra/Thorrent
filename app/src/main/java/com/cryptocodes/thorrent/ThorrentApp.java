package com.cryptocodes.thorrent;

import android.app.Application;
import android.content.Context;

/**
 * Created by jonathanf on 17/11/2014.
 */
public class ThorrentApp extends Application {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
