package cn.manfi.android.project.base.mvvm.base;

import android.app.Activity;
import android.support.annotation.Nullable;
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

    public void dismissLoading() {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).dismissLoading();
            return;
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
