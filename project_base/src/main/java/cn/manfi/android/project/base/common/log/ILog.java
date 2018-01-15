package cn.manfi.android.project.base.common.log;

/**
 * Log 接口
 * Created by Manfi on
 */
public interface ILog {

    void v(String tag, String msg);

    void d(String tag, String msg);

    void i(String tag, String msg);

    void w(String tag, String msg);

    void e(String tag, String msg);

    void open();

    void close();

    void println(int priority, String tag, String msg);
}
