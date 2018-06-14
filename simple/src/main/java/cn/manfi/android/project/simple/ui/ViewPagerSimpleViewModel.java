package cn.manfi.android.project.simple.ui;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import cn.manfi.android.project.base.mvvm.base.BaseViewModel;
import cn.manfi.android.project.simple.BR;
import cn.manfi.android.project.simple.R;
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * ~
 * Created by manfi on 2018/1/2.
 */

public class ViewPagerSimpleViewModel extends BaseViewModel<ViewPagerSimpleActivity> {

    public final ObservableList<ViewPagerItemViewModel> itemList = new ObservableArrayList<>();
    public final ItemBinding<ViewPagerItemViewModel> itemBinding = ItemBinding.of(BR.itemViewModel, R.layout.item_pager);

    public final BindingViewPagerAdapter.PageTitles<ViewPagerItemViewModel> pageTitles = (position, item) -> String.valueOf(position);

    public ViewPagerSimpleViewModel(ViewPagerSimpleActivity activity) {
        super(activity);
    }

    public void setUpData() {
        for (int i = 0; i < 10; i++) {
            itemList.add(new ViewPagerItemViewModel(activity, i));
        }
    }
}
