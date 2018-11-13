package cn.manfi.android.project.base.common.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import cn.manfi.android.project.base.common.Config;
import cn.manfi.android.project.base.common.NetworkUtil;
import cn.manfi.android.project.base.common.log.LogUtil;
import cn.manfi.android.project.base.mvvm.base.BaseViewModel;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Api接口返回Observer Created by manfi on 2017/9/21.
 */
public abstract class ApiResultObserver<T> implements Observer<T> {

    private final String TAG = getClass().getSimpleName();

    private BaseViewModel viewModel;
    private ApiRequestStatus apiRequestStatus;

    public ApiResultObserver() {
    }

    public ApiResultObserver(ApiRequestStatus apiRequestStatus) {
        this.apiRequestStatus = apiRequestStatus;
    }

    public ApiResultObserver(BaseViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public ApiResultObserver(BaseViewModel viewModel, ApiRequestStatus apiRequestStatus) {
        this.viewModel = viewModel;
        this.apiRequestStatus = apiRequestStatus;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (apiRequestStatus != null) {
            apiRequestStatus.setStart(d);
        }
        if (viewModel != null && !NetworkUtil.isNetworkConnected(viewModel.getActivity())) {
            d.dispose();
            if (apiRequestStatus != null) {
                apiRequestStatus.setFinish(true);
            }
            LogUtil.d(Config.isDebug(), TAG, "没有网络");
            onNoNetwork();
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (apiRequestStatus != null) {
            apiRequestStatus.setFinish(true);
        }
        if (e instanceof ConnectException || e instanceof TimeoutException || e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
            // 网络连接错误
            LogUtil.w(Config.isDebug(), TAG, "网络连接异常");
            onNormalError(e, "网络连接异常");
            return;
        } else if (e instanceof HttpException) {
            // 404等错误
            LogUtil.w(Config.isDebug(), TAG, "服务器开小差");
            onNormalError(e, "服务器开小差");
            return;
        } else if (e instanceof IOException) {
            // Json数据解析错误
            LogUtil.w(Config.isDebug(), TAG, "数据解析错误");
            onNormalError(e, "数据解析错误");
            return;
        }
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        if (apiRequestStatus != null) {
            apiRequestStatus.setFinish(true);
        }
    }

    public void onNormalError(@NonNull Throwable e, @NonNull String uiMsg) {
        e.printStackTrace();
    }

    public void onNoNetwork() {
        if (viewModel != null) {
            viewModel.onNetworkUnavaliable();
        }
    }

    public ApiRequestStatus getApiRequestStatus() {
        return apiRequestStatus;
    }
}