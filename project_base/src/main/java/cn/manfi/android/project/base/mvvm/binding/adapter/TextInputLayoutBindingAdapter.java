package cn.manfi.android.project.base.mvvm.binding.adapter;

import androidx.databinding.BindingAdapter;
import com.google.android.material.textfield.TextInputLayout;

public final class TextInputLayoutBindingAdapter {

    @BindingAdapter("errorText")
    public static void setErrorMessage(TextInputLayout view, String errorMessage) {
        view.setError(errorMessage);
    }
}
