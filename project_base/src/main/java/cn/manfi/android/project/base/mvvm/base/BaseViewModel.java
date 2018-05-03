package cn.manfi.android.project.base.mvvm.base;

import android.app.Activity;

import cn.manfi.android.project.base.ui.base.BaseActivity;

/**
 * 基础ViewModel
 * Created by manfi on 2017/10/25.
 */

public class BaseViewModel implements ViewModel {

    protected BaseActivity activity;

    public BaseViewModel(BaseActivity activity) {
        this.activity = activity;
    }

    public BaseActivity getActivity() {
        return activity;
    }
}
