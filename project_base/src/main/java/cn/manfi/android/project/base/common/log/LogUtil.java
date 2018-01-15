package cn.manfi.android.project.base.common.log;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Log 打印工具类
 * Created by Manfi on
 */
public class LogUtil {

    private static HashMap<String, ILog> loggerHashMap = new HashMap<>();
    private static final ILog defaultLogger = new PrintToLogCat();

    public static void addLogger(ILog logger) {
        String loggerName = logger.getClass().getName();
        String defaultLoggerName = defaultLogger.getClass().getName();
        if (!loggerHashMap.containsKey(loggerName)
                && !defaultLoggerName.equalsIgnoreCase(loggerName)) {
            logger.open();
            loggerHashMap.put(loggerName, logger);
        }
    }

    public static void removeLogger(ILog logger) {
        String loggerName = logger.getClass().getName();
        if (loggerHashMap.containsKey(loggerName)) {
            logger.close();
            loggerHashMap.remove(loggerName);
        }
    }

    public static void d(boolean nodeDebug, Object object, String msg) {
        printLog(nodeDebug, Log.DEBUG, object, msg);
    }

    public static void e(boolean nodeDebug, Object object, String msg) {
        printLog(nodeDebug, Log.ERROR, object, msg);
    }

    public static void i(boolean nodeDebug, Object object, String msg) {
        printLog(nodeDebug, Log.INFO, object, msg);
    }

    public static void v(boolean nodeDebug, Object object, String msg) {
        printLog(nodeDebug, Log.VERBOSE, object, msg);
    }

    public static void w(boolean nodeDebug, Object object, String msg) {
        printLog(nodeDebug, Log.WARN, object, msg);
    }

    public static void d(boolean nodeDebug, String tag, String msg) {
        printLog(nodeDebug, Log.DEBUG, tag, msg);
    }

    public static void e(boolean nodeDebug, String tag, String msg) {
        printLog(nodeDebug, Log.ERROR, tag, msg);
    }

    public static void i(boolean nodeDebug, String tag, String msg) {
        printLog(nodeDebug, Log.INFO, tag, msg);
    }

    public static void v(boolean nodeDebug, String tag, String msg) {
        printLog(nodeDebug, Log.VERBOSE, tag, msg);
    }

    public static void w(boolean nodeDebug, String tag, String msg) {
        printLog(nodeDebug, Log.WARN, tag, msg);
    }

    public static void println(boolean nodeDebug, int priority, String tag,
                               String msg) {
        printLog(nodeDebug, priority, tag, msg);
    }

    private static void printLog(boolean nodeDebug, int priority,
                                 Object object, String msg) {
        Class<?> cls = object.getClass();
        String tag = cls.getName();
        String arrays[] = tag.split("\\.");
        tag = arrays[arrays.length - 1];
        printLog(nodeDebug, priority, tag, msg);
    }

    private static void printLog(boolean nodeDebug, int priority, String tag,
                                 String msg) {
        if (LogConfig.DEBUG && nodeDebug) {
            printLog(defaultLogger, priority, tag, msg);
            for (Map.Entry<String, ILog> entry : loggerHashMap.entrySet()) {
                ILog logger = entry.getValue();
                if (logger != null) {
                    printLog(logger, priority, tag, msg);
                }
            }
        }
    }

    private static void printLog(ILog log, int priority, String tag, String msg) {
        switch (priority) {
            case Log.VERBOSE:
                log.v(tag, msg);
                break;
            case Log.DEBUG:
                log.d(tag, msg);
                break;
            case Log.INFO:
                log.i(tag, msg);
                break;
            case Log.WARN:
                log.w(tag, msg);
                break;
            case Log.ERROR:
                log.e(tag, msg);
                break;
            default:
                break;
        }
    }
}
