package cn.manfi.android.project.base.common;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 格式化工具
 * Created by manfi on 2017/12/22.
 */

public class FormatUtils {

    /**
     * 格式化时间
     *
     * @param timeMillis 时间戳
     * @param pattern    格式（例如：yyyy-MM-dd HH:mm:ss）
     *
     * @return ~
     */
    public static String formatTime(long timeMillis, String pattern) {
        if (TextUtils.isEmpty(pattern)) {
            return "";
        }
        Date date = new Date(timeMillis);
        SimpleDateFormat sdfCurr = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdfCurr.format(date);
    }
}
