package cn.manfi.android.project.base.common.net;

import io.reactivex.disposables.Disposable;

/**
 * 网络请求状态
 * Created by manfi on 2017/12/29.
 */

public class ApiRequestStatus {

    private Disposable disposable;
    private boolean isFinish;

    public ApiRequestStatus() {
    }

    public boolean isStart() {
        return disposable != null;
    }

    public void setStart(Disposable disposable) {
        this.disposable = disposable;
    }

    public boolean cancel() {
        if (disposable != null) {
            disposable.dispose();
            setFinish(true);
            return true;
        }
        return false;
    }

    public boolean isCancel() {
        if (disposable != null) {
            return disposable.isDisposed();
        }
        return false;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }
}
