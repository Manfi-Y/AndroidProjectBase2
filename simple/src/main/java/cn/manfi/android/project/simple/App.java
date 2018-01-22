package cn.manfi.android.project.simple;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import cn.manfi.android.project.base.BaseApp;
import cn.manfi.android.project.base.BuildConfig;
import cn.manfi.android.project.base.common.log.LogConfig;
import cn.manfi.android.project.base.ui.base.BaseActivity;

/**
 * Application
 * Created by manfi on 2017/9/20.
 */

public class App extends BaseApp {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LogConfig.DEBUG = BuildConfig.DEBUG;
        Fresco.initialize(this);
    }
}
