package cn.manfi.android.project.base.mvvm.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import cn.manfi.android.project.base.ui.base.BaseActivity;
import cn.manfi.android.project.base.ui.base.BaseUI;

/**
 * 基础ViewModel
 * Created by manfi on 2017/10/25.
 */

public class BaseViewModel<T extends Activity> implements ViewModel {

    protected final String TAG = getClass().getSimpleName();
    protected final boolean DEBUG = true;

    protected T activity;

    private BaseUI baseUI;

    public BaseViewModel(T activity) {
        this.activity = activity;
        baseUI = getCustomBaseUI();
    }

    public Activity getActivity() {
        return activity;
    }

    /**
     * 可自行重写BaseUI
     */
    protected BaseUI getCustomBaseUI() {
        if (activity instanceof BaseActivity) {
            return ((BaseActivity) activity).getBaseUI();
        } else {
            return new BaseUI(activity);
        }
    }

    public void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public void showToast(String msg, int duration) {
        baseUI.showToast(msg, duration);
    }

    public void showLoading(@NonNull String msg) {
        showLoading(msg, false, false, null);
    }

    /**
     * 显示Loading
     *
     * @param msg Loading title
     */
    public void showLoading(@NonNull String msg, boolean cancelTouchOutside, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        baseUI.showLoading(msg, cancelTouchOutside, cancelable, cancelListener);
    }

    /**
     * 取消Loading对话框
     */
    public void dismissLoading() {
        baseUI.dismissLoading();
    }

    /**
     * 弹出键盘
     *
     * @param view 通常是EditText
     */
    public void showSoftKeyboard(View view) {
        baseUI.showSoftKeyboard(view);
    }

    /**
     * 隐藏键盘
     *
     * @param view 通常是EditText
     */
    public void hideSoftKeyboard(View view) {
        baseUI.hideSoftKeyboard(view);
    }

    /**
     * 没有网络回调
     */
    public void onNetworkUnavaliable() {
        baseUI.onNetworkUnavaliable();
    }
}
