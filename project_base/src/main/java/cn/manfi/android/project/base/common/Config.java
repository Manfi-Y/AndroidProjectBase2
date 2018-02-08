package cn.manfi.android.project.base.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * 配置
 * Created by manfi on 2018/2/8.
 */

public class Config {

    private static Boolean isDebug = null;

    public static boolean isDebug() {
        return isDebug != null && isDebug;
    }

    /**
     * Sync lib debug with app's debug value. Should be called in module Application
     *
     * @param context ~
     */
    public static void syncIsDebug(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null &&
                    (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }
}
