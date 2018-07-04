package cn.manfi.android.project.simple;

import com.facebook.drawee.backends.pipeline.Fresco;

import cn.manfi.android.project.base.BaseApp;
import cn.manfi.android.project.base.BuildConfig;
import cn.manfi.android.project.base.common.Config;
import cn.manfi.android.project.base.common.log.LogConfig;

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
        Config.syncIsDebug(instance);
        Fresco.initialize(this);
    }
}
