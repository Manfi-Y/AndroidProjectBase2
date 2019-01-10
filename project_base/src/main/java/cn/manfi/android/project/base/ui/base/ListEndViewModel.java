package cn.manfi.android.project.base.ui.base;

import android.app.Activity;
import android.databinding.ObservableField;
import android.support.annotation.ColorRes;
import android.text.TextUtils;

import cn.manfi.android.project.base.mvvm.base.BaseViewModel;

/**
 * 列表底部加载更多/没有更多数据/加载失败ViewModel
 * layout_list_end.xml
 * Created by manfi on 2017/12/26.
 */

public class ListEndViewModel extends BaseViewModel {

    public final ObservableField<Boolean> isProgressVisable = new ObservableField<>();
    public final ObservableField<String> endText = new ObservableField<>();

    public ListEndViewModel(Activity activity) {
        super(activity);
        setLoading(true, "正在加载更多");
    }

    public void setLoading(boolean isLoading) {
        isProgressVisable.set(isLoading);
    }

    public void setLoadingText(String text) {
        endText.set(text);
    }

    public void setLoading(boolean isLoading, String text) {
        setLoading(isLoading);
        setLoadingText(text);
    }
}
