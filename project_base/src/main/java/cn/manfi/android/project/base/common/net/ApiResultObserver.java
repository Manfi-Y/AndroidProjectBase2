package cn.manfi.android.project.base.common.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import cn.manfi.android.project.base.common.NetworkUtil;
import cn.manfi.android.project.base.common.log.LogUtil;
import cn.manfi.android.project.base.mvvm.base.BaseViewModel;
import cn.manfi.android.project.base.ui.base.BaseActivity;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Api接口返回Observer
 * Created by manfi on 2017/9/21.
 */
public abstract class ApiResultObserver<T> implements Observer<T> {

    private final String TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

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
            LogUtil.d(DEBUG, TAG, "没有网络");
            onNoNetwork();
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (apiRequestStatus != null) {
            apiRequestStatus.setFinish(true);
        }
        if (e instanceof ConnectException || e instanceof TimeoutException
                || e instanceof SocketTimeoutException) {
            // 网络连接错误
            LogUtil.w(DEBUG, TAG, "网络连接异常");
            /*if (viewModel != null && viewModel.getActivity() instanceof BaseActivity) {
                ((BaseActivity) viewModel.getActivity()).showToast("网络连接异常");
            }*/
            onNormalError(new Throwable("网络连接异常", e));
        } else if (e instanceof HttpException) {
            // 404等错误
            LogUtil.w(DEBUG, TAG, "服务器开小差");
            /*if (viewModel != null && viewModel.getActivity() instanceof BaseActivity) {
                ((BaseActivity) viewModel.getActivity()).showToast("服务器开小差");
            }*/
            onNormalError(new Throwable("服务器开小差", e));
        } else if (e instanceof IOException) {
            // Json数据解析错误
            LogUtil.w(DEBUG, TAG, "Json数据解析错误");
            /*if (viewModel != null && viewModel.getActivity() instanceof BaseActivity) {
                ((BaseActivity) viewModel.getActivity()).showToast("Json数据解析错误");
            }*/
            onNormalError(new Throwable("Json数据解析错误", e));
        }
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        if (apiRequestStatus != null) {
            apiRequestStatus.setFinish(true);
        }
    }

    public void onNormalError(@NonNull Throwable e) {

    }

    public void onNoNetwork() {
    }

    public ApiRequestStatus getApiRequestStatus() {
        return apiRequestStatus;
    }
}