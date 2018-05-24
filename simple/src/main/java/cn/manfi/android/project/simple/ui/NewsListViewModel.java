package cn.manfi.android.project.simple.ui;

import android.app.Activity;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.manfi.android.project.base.common.net.ApiRequestStatus;
import cn.manfi.android.project.base.common.net.ApiResultObserver;
import cn.manfi.android.project.base.mvvm.base.BaseViewModel;
import cn.manfi.android.project.base.mvvm.command.ReplyCommand;
import cn.manfi.android.project.base.ui.base.ListEndViewModel;
import cn.manfi.android.project.base.ui.base.ListLoadingViewModel;
import cn.manfi.android.project.simple.BR;
import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.model.RGDNews;
import cn.manfi.android.project.simple.model.response.RGDApiResult;
import cn.manfi.android.project.simple.common.net.AppApiManager;
import cn.manfi.android.project.simple.common.net.RGDApiResultParser;
import cn.manfi.android.project.simple.ui.base.SwipeBackAppActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;

/**
 * 新闻列表ViewModel
 * Created by manfi on 2017/12/22.
 */

public class NewsListViewModel extends BaseViewModel<NewsListSimpleActivity> {

    private static final int PAGE_SIZE = 20;

    private String programId = "5";
    private int pageNum = -1;

    private ApiRequestStatus arsRequestNews;

    public final ObservableField<Boolean> isRefreshing = new ObservableField<>(false);
    // 数据ViewModel
    public final ObservableList<NewsListItemViewModel> newsListItemList = new ObservableArrayList<>();
    // 数据+footer ViewModel
    public final MergeObservableList<Object> loadMoreItemList = new MergeObservableList<>();
    // 单种ViewModel Binding
    public final ItemBinding<NewsListItemViewModel> itemBinding = ItemBinding.of(BR.itemViewModel, R.layout.item_list_news);
    // 多种ViewModel Binding
    public final OnItemBindClass<Object> loadMoreItemBindingList = new OnItemBindClass<>()
            .map(ListLoadingViewModel.class, BR.viewModel, R.layout.layout_list_loading)
            .map(ListEndViewModel.class, BR.viewModel, R.layout.layout_list_end)
            .map(NewsListItemViewModel.class, BR.itemViewModel, R.layout.item_list_news);

    private ListLoadingViewModel listLoadingViewModel;
    private ListEndViewModel listEndViewModel;

    // 刷新命令
    public final ReplyCommand<Void> onRefreshCmd = new ReplyCommand<>(() -> {
        isRefreshing.set(true);
        pageNum = -1;
        requestNews(programId);
    });

    // 底部加载更多命令
    public final ReplyCommand<Integer> onLoadMoreCmd = new ReplyCommand<>(itemConnt -> {
        if (arsRequestNews != null && arsRequestNews.isFinish()) {
            requestNews(programId);
        }
    });

    public NewsListViewModel(NewsListSimpleActivity activity) {
        super(activity);
        listLoadingViewModel = new ListLoadingViewModel(activity);
        listEndViewModel = new ListEndViewModel(activity);
    }

    /**
     * 请求新闻列表
     *
     * @param programId 节目id
     */
    public void requestNews(String programId) {
        if (arsRequestNews != null) {
            arsRequestNews.cancel();
        }
        this.programId = programId;
        int _pageNum;
        if (pageNum == -1) {
            _pageNum = 0;
        } else {
            _pageNum = pageNum++;
        }
        AppApiManager.getInstance().getRGDApiService().requestNews(programId, _pageNum, PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .compose(((SwipeBackAppActivity) activity).bindToLifecycle())
                .delay(pageNum == -1 ? 1 : 0, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new RGDApiResultParser<List<RGDNews>>() {

                    @Override
                    protected void processApiResult(RGDApiResult<List<RGDNews>> rgdApiResult) {
                        super.processApiResult(rgdApiResult);
                    }
                })
                .flatMap(rgdNews -> {
                    if (pageNum == -1) {
                        newsListItemList.clear();
                    }
                    return Observable.fromIterable(rgdNews);
                })
                .doOnDispose(() -> {
                    listLoadingViewModel.setLoading(false, "下拉重新获取");
                    listEndViewModel.setLoading(false, "滚动重新加载");
                })
                .doFinally(() -> {
                    /*
                     如果列表空需要从MergeObservableList移除，不然removeAll会因为判断子list.size都是0
                     而不执行清空操作
                     */
                    if (newsListItemList.isEmpty()) {
                        loadMoreItemList.removeList(newsListItemList);
                    } else {
                        loadMoreItemList.removeItem(listLoadingViewModel);
                    }
                    isRefreshing.set(false);
                })
                .subscribe(new ApiResultObserver<RGDNews>(this, arsRequestNews = new ApiRequestStatus()) {

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        if (!d.isDisposed()) {
                            if (pageNum == -1) {
                                if (newsListItemList.isEmpty()) {
                                    if (isRefreshing.get()) {
                                        loadMoreItemList.removeItem(listLoadingViewModel);
                                    } else {
                                        listLoadingViewModel.setLoading(true, "正在加载");
                                        loadMoreItemList.insertItem(listLoadingViewModel);
                                    }
                                    loadMoreItemList.insertList(newsListItemList);
                                    loadMoreItemList.remove(listEndViewModel);
                                } else {
                                    isRefreshing.set(true);
                                }
                            } else {
                                listEndViewModel.setLoading(true, "正在加载更多");
                            }
                        }
                    }

                    @Override
                    public void onNext(RGDNews rgdNews) {
                        newsListItemList.add(new NewsListItemViewModel(activity, rgdNews));
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        addPageNum();
                        loadMoreItemList.removeItem(listEndViewModel);
                        if (!newsListItemList.isEmpty() && newsListItemList.size() % PAGE_SIZE == 0) {
                            listEndViewModel.setLoading(true, "正在加载更多");
                            loadMoreItemList.insertItem(listEndViewModel);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        listLoadingViewModel.setLoading(false, "下拉重新获取");
                        listEndViewModel.setLoading(false, "滚动重新加载");
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                        if (newsListItemList.isEmpty()) {
                            listLoadingViewModel.setLoading(false, "下拉重新获取");
                            if (!loadMoreItemList.contains(listLoadingViewModel)) {
                                loadMoreItemList.insertItem(listLoadingViewModel);
                            }
                        } else {
                            listEndViewModel.setLoading(false, "滚动重新加载");
                        }
                    }
                });
    }

    private void addPageNum() {
        pageNum++;
    }
}
