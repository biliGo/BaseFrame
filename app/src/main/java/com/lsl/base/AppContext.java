package com.lsl.base;

import android.app.Application;

import com.lsl.base.common.AppConfig;

/**
 * Created by Forrest
 * on 2017/7/3 10:10
 */

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.init(this);
    }
}
