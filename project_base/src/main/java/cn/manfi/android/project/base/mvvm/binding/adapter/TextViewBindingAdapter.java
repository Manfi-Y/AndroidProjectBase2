package cn.manfi.android.project.base.mvvm.binding.adapter;

import androidx.databinding.BindingAdapter;
import android.text.Spanned;
import android.widget.TextView;

/**
 * ~
 * Created by manfi on 2017/9/29.
 */

public final class TextViewBindingAdapter {

    @BindingAdapter({"text"})
    public static void setText(TextView view, CharSequence text) {
        final CharSequence oldText = view.getText();
        if (text == oldText || (text == null && oldText.length() == 0)) {
            return;
        }
        if (text instanceof Spanned) {
            if (text.equals(oldText)) {
                return; // No change in the spans, so don't set anything.
            }
        } else if (!haveContentsChanged(text, oldText)) {
            return; // No content changes, so don't set anything.
        }
        view.setText(String.format("DataBinding：%s", text));
    }

    private static boolean haveContentsChanged(CharSequence str1, CharSequence str2) {
        if ((str1 == null) != (str2 == null)) {
            return true;
        } else if (str1 == null) {
            return false;
        }
        final int length = str1.length();
        if (length != str2.length()) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    @BindingAdapter({"changeText"})
    public static void changeText(TextView view, CharSequence text) {
        view.setText(String.format("ChangeText：%s", text));
    }
}
