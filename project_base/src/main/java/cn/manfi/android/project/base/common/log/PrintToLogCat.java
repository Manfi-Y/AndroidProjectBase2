package cn.manfi.android.project.base.common.log;

import android.util.Log;

/**
 * 输出 Log 到 LogCat
 * Created by Manfi on 15/4/21.
 */
public class PrintToLogCat implements ILog {

    @Override
    public void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    @Override
    public void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    @Override
    public void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    @Override
    public void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    @Override
    public void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

    @Override
    public void println(int priority, String tag, String msg) {
        Log.println(priority, tag, msg);
    }
}
