package cn.manfi.android.project.base.common.net;

import android.support.annotation.NonNull;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * 上传进度被观察者
 * <p>
 * Created by manfi on 2018/1/16.
 */

public class UploadOnSubscribe implements FlowableOnSubscribe<Object> {

    private FlowableEmitter<Object> flowableEmitter;
    private long totalSize = 0l;
    private long uploadedSize = 0l;

    private int mPercent = 0;

    public UploadOnSubscribe(long sumLength) {
        this.totalSize = sumLength;
    }

    public void onRead(long read) {
        uploadedSize += read;
        if (totalSize <= 0) {
            onProgress(-1);
        } else {
            onProgress((int) (100 * uploadedSize / totalSize));
        }
    }

    private void onProgress(int percent) {
        if (flowableEmitter == null) return;
        if (percent == mPercent) return;
        mPercent = percent;
        if (percent >= 100) {
            percent = 100;
            flowableEmitter.onNext(percent);
            flowableEmitter.onComplete();
            return;
        }
        flowableEmitter.onNext(percent);
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<Object> e) throws Exception {
        this.flowableEmitter = e;
        flowableEmitter.onNext(totalSize);
    }
}
