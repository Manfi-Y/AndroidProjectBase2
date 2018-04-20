package cn.manfi.android.project.base.mvvm.binding.adapter;

import android.databinding.BindingAdapter;
import android.support.design.widget.TextInputLayout;

public final class TextInputLayoutBindingAdapter {

    @BindingAdapter("errorText")
    public static void setErrorMessage(TextInputLayout view, String errorMessage) {
        view.setError(errorMessage);
    }
}
