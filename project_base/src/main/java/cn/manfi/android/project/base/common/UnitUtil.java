package cn.manfi.android.project.base.common;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.Locale;

/**
 * 单位转换工具类
 * Created by Manfi
 */
public class UnitUtil {

    /**
     * dp转px
     *
     * @param context ~
     * @param dpValue ~
     * @return ~
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param context ~
     * @param pxValue ~
     * @return ~
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 计算TextView能显示多少行
     *
     * @param text 文字
     * @param tv   TextView
     * @return ~
     */
    public static int calTextLineInTextView(String text, TextView tv) {
        if (TextUtils.isEmpty(text)) {
            return 1;
        }
        TextPaint paint = tv.getPaint();
        float textWidth = paint.measureText(text);
        int tvContentWidth = tv.getMeasuredWidth() - tv.getPaddingLeft() - tv.getPaddingRight();
        if (tvContentWidth == 0) {
            return 1;
        }
        int lines = 0;
        do {
            lines++;
            textWidth -= tvContentWidth;
        } while (textWidth > 0);
        return lines;
    }

    /**
     * 转换byte成带单位字符串
     *
     * @param bytes ~
     * @param si    ~
     * @return ~
     */
    public static String humanReadableByte(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * 转换byte成不带单位
     *
     * @param bytes ~
     * @param si    ~
     * @return ~
     */
    public static long humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes;
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return (long) (bytes / Math.pow(unit, exp));
    }

    /**
     * 转换byte成对应的单位
     *
     * @param bytes ~
     * @param si    ~
     * @return ~
     */
    public static String humanReadableByteUnit(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        return (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
    }
}
