package cn.manfi.android.project.base.common.net;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import cn.manfi.android.project.base.common.NetworkUtil;
import cn.manfi.android.project.base.common.log.LogUtil;
import cn.manfi.android.project.base.mvvm.base.BaseViewModel;
import retrofit2.HttpException;

/**
 * 上传进度观察者
 * Created by manfi on 2018/1/16.
 */

public abstract class UploadSubscriber<T> implements Subscriber<Object> {

    private final String TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private BaseViewModel viewModel;
    protected Subscription subscription;

    private boolean isFinish;

    public UploadSubscriber() {
    }

    protected Subscription mSubscription;

    @Override
    public void onSubscribe(Subscription s) {
        this.mSubscription = s;
        onRequestUpload();
        if (viewModel != null && !NetworkUtil.isNetworkConnected(viewModel.getActivity())) {
            subscription.cancel();
            isFinish = true;
            LogUtil.d(DEBUG, TAG, "没有网络");
            onNoNetwork();
            return;
        }
        mSubscription.request(1);
    }

    @Override
    public void onNext(Object o) {
        if (o instanceof Long) {
            onStartUpload((Long) o);
        } else if (o instanceof Integer) {
            onProgress((Integer) o);
        } else {
            onResult((T) o);
        }

        mSubscription.request(1);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        isFinish = true;
        if (e instanceof ConnectException || e instanceof TimeoutException
                || e instanceof SocketTimeoutException) {
            // 网络连接错误
            LogUtil.w(DEBUG, TAG, "网络连接异常");
            if (viewModel != null) {
                viewModel.showToast("网络连接异常");
            }
        } else if (e instanceof HttpException) {
            // 404等错误
            LogUtil.w(DEBUG, TAG, "服务器开小差");
            if (viewModel != null) {
                viewModel.showToast("服务器开小差");
            }
        } else if (e instanceof IOException) {
            // 文件写入失败
            LogUtil.w(DEBUG, TAG, "文件写入失败");
            if (viewModel != null) {
                viewModel.showToast("文件写入失败");
            }
        }
        e.printStackTrace();
    }

    protected abstract void onRequestUpload();

    protected abstract void onStartUpload(long totalSize);

    protected abstract void onProgress(Integer percent);

    protected abstract void onResult(T result);

    public void onNoNetwork() {
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void cancel() {
        subscription.cancel();
        isFinish = true;
    }
}
