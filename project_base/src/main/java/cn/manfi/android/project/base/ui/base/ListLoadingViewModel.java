package cn.manfi.android.project.base.ui.base;

import android.app.Activity;
import androidx.databinding.ObservableField;

import cn.manfi.android.project.base.mvvm.base.BaseViewModel;

/**
 * 列表加载状态ViewModel
 * layout_list_loading.xml
 * Created by manfi on 2017/12/25.
 */

public class ListLoadingViewModel extends BaseViewModel {

    public final ObservableField<Boolean> isProgressVisable = new ObservableField<>();
    public final ObservableField<String> loadingText = new ObservableField<>();

    public ListLoadingViewModel(Activity activity) {
        super(activity);
        setLoading(true, "正在加载");
    }

    public void setLoading(boolean isLoading) {
        isProgressVisable.set(isLoading);
    }

    public void setLoadingText(String text) {
        loadingText.set(text);
    }

    public void setLoading(boolean isLoading, String text) {
        setLoading(isLoading);
        setLoadingText(text);
    }
}
