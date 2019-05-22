package cn.manfi.android.project.base.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 检测手机网络连接工具类
 * <p>
 * Created by Manfi
 */
public class NetworkUtil {

    /**
     * 获取手机网络连接信息
     *
     * @param context     上下文
     * @param networkType 网络类型（如：ConnectivityManager.TYPE_xxx）<br>0:可用网络</br>
     *
     * @return ~
     */
    public static NetworkInfo getNetWorkInfo(Context context, int networkType) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info;
        if (networkType == 0) {
            info = connMgr != null ? connMgr.getActiveNetworkInfo() : null;
        } else {
            info = connMgr != null ? connMgr.getNetworkInfo(networkType) : null;
        }
        return info;
    }

    /**
     * 手机是否有网络
     *
     * @param context ~
     *
     * @return ~
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo info = getNetWorkInfo(context, 0);
        return info != null && info.isConnected();
    }

    /**
     * 手机WiFi是否连接
     *
     * @param context ~
     *
     * @return ~
     */
    public static boolean isWifiConnected(Context context) {
        NetworkInfo info = getNetWorkInfo(context, ConnectivityManager.TYPE_WIFI);
        return info != null && info.isConnected();
    }
}