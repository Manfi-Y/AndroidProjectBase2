package cn.manfi.android.project.simple;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import cn.manfi.android.project.base.BuildConfig;
import cn.manfi.android.project.base.common.log.LogConfig;

/**
 * Application
 * Created by manfi on 2017/9/20.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogConfig.DEBUG = BuildConfig.DEBUG;
        Fresco.initialize(this);
    }
}
