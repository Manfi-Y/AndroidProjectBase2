package cn.manfi.android.project.simple.ui;

import android.app.Activity;
import android.databinding.ObservableField;

import cn.manfi.android.project.base.mvvm.base.BaseViewModel;
import cn.manfi.android.project.simple.model.RGDNews;

/**
 * 新闻列表ItemViewModel
 * Created by manfi on 2017/12/22.
 */

public class NewsListItemViewModel extends BaseViewModel {

    public final ObservableField<RGDNews> news = new ObservableField<>();

    public NewsListItemViewModel(Activity activity, RGDNews news) {
        super(activity);
        this.news.set(news);
    }
}
