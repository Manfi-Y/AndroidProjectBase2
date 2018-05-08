package cn.manfi.android.project.base.mvvm.base;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import cn.manfi.android.project.base.ui.base.BaseActivity;

/**
 * 基础ViewModel
 * Created by manfi on 2017/10/25.
 */

public class BaseViewModel implements ViewModel {

    protected Activity activity;

    private Toast toast;
    private MaterialDialog loadingDialog;

    public BaseViewModel(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public void showToast(String msg, int duration) {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).showToast(msg, duration);
            return;
        }
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(activity, msg, duration);
        toast.show();
    }

    /**
     * 显示Loading
     *
     * @param msg Loading title
     */
    public void showLoading(@Nullable String msg) {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).showLoading(msg);
            return;
        }
        if (loadingDialog == null) {
            loadingDialog = new MaterialDialog.Builder(activity)
                    .progress(true, 0)
                    .content(msg)
                    .build();
        }
        loadingDialog.show();
    }

    /**
     * 取消Loading对话框
     */
    public void dismissLoading() {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).dismissLoading();
            return;
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 隐藏键盘
     */
    public void hideSoftKeyboard() {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).hideSoftKeyboard();
            return;
        }
        if (activity.getCurrentFocus() == null || activity.getCurrentFocus().getWindowToken() == null)
            return;

        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏键盘
     *
     * @param view ~
     */
    public void hideSoftKeyboard(View view) {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).hideSoftKeyboard(view);
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
