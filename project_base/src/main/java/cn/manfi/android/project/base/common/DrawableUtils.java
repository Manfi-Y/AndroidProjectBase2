package cn.manfi.android.project.base.common;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * 图片处理工具
 * Created by manfi on 2017/11/30.
 */

public class DrawableUtils {

    public static Drawable tintDrawable(@NonNull Drawable drawable, int color) {
        return tintDrawable(drawable, ColorStateList.valueOf(color));
    }

    public static Drawable tintDrawable(@NonNull Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }
}
