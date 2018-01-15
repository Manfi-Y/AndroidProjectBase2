package cn.manfi.android.project.simple.ui;

import android.app.Activity;
import android.databinding.ObservableField;

import cn.manfi.android.project.base.mvvm.base.BaseViewModel;

/**
 * ~
 * Created by manfi on 2018/1/5.
 */

public class ViewPagerItemViewModel extends BaseViewModel {

    public final ObservableField<Integer> pageNum = new ObservableField<>();

    public ViewPagerItemViewModel(Activity activity, int pageNum) {
        super(activity);
        this.pageNum.set(pageNum);
    }
}
