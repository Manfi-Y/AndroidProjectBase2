package cn.manfi.android.project.simple.ui;

import android.app.Activity;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.manfi.android.project.base.common.net.ApiResultObserver;
import cn.manfi.android.project.base.mvvm.base.BaseViewModel;
import cn.manfi.android.project.base.mvvm.command.ReplyCommand;
import cn.manfi.android.project.simple.BR;
import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.model.LineType;
import cn.manfi.android.project.simple.model.response.ApiResult;
import cn.manfi.android.project.simple.common.net.AppApiManager;
import cn.manfi.android.project.simple.common.net.ApiResultParser;
import cn.manfi.android.project.simple.ui.base.SwipeBackAppActivity;
import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * ~
 * Created by manfi on 2017/10/9.
 */

public class MVVMSimpleViewModel extends BaseViewModel {

    public static final String TOKEN_REFRESH_LIST = "refresh_list";

    public final ObservableField<String> linesCount = new ObservableField<>();
    public final ObservableList<MVVMItemViewModel> itemViewModels = new ObservableArrayList<>();
    public final ItemBinding<MVVMItemViewModel> itemBinding = ItemBinding.of(BR.itemViewModel, R.layout.item_list_mvvm_simple);

    public final ReplyCommand<Void> requestAllLineCmd = new ReplyCommand<>(this::requestAllLine);

    public final ReplyCommand<Integer> onLoadMoreCommand = new ReplyCommand<>(integer -> {
        showToast("onLoadMoreCommand");
    });

    public MVVMSimpleViewModel(Activity activity) {
        super(activity);
    }

    private void requestAllLine() {
        System.out.println("线程1：" + Thread.currentThread().getId());
        Observable<Notification<ApiResult<List<LineType>>>> ob = AppApiManager.getInstance().getApiService().requestAllLine("all_lines", "guangzhou")
                .subscribeOn(Schedulers.io())
                .compose(((SwipeBackAppActivity) activity).bindToLifecycle())
                .materialize()
                .share();

        // 延迟处理结果（让Loading UI展示时间长一点，如果Activity没有提前结束就继续执行）
        ob.delay(1, TimeUnit.SECONDS)
                .filter(Notification::isOnNext)
                .map(Notification::getValue)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ApiResultParser<List<LineType>>() {

                    @Override
                    protected void processApiResult(ApiResult<List<LineType>> apiResult) {  // 处理ApiResult结果，如果业务代码不成功则进行处理
                        super.processApiResult(apiResult);
                        showToast(apiResult.getError_message());
                    }
                })
                .doOnNext(
                        lineTypes -> {
                            System.out.println("线程2：" + Thread.currentThread().getId());
                            linesCount.set("线路类型总数：" + lineTypes.size());
                        })
                .flatMap(
                        lineTypes -> {
                            System.out.println("线程3：" + Thread.currentThread().getId());
                            return Observable.fromIterable(lineTypes);
                        })
                .subscribe(new ApiResultObserver<LineType>(this) {

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        if (!d.isDisposed()) {
                            showLoading("正在加载");
                        }
                    }

                    @Override
                    public void onNext(LineType lineType) {
                        System.out.println("线程4：" + Thread.currentThread().getId());
                        itemViewModels.add(new MVVMItemViewModel(activity, lineType.getType()));
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissLoading();
                    }
                });
    }
}
