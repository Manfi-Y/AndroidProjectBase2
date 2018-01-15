package cn.manfi.android.project.base.common.net;

import java.io.File;
import java.io.IOException;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 下载进度被观察者
 * Created by manfi on 2018/1/12.
 */

public class DownLoadOnSubscribe implements FlowableOnSubscribe<Object> {

    private FlowableEmitter<Object> flowableEmitter;

    // 写入文件路径
    private String filePath;
    // 写入文件名
    private String fileName;

    // 已下载大小
    private long downloadedSize = 0l;
    //    总长度
    private long totalSize = 0l;
    private int percent = 0;

    private Source source;
    private Source progressSource;
    private BufferedSink sink;

    public DownLoadOnSubscribe(ResponseBody responseBody, String filePath, String fileName) throws IOException {
        this.filePath = filePath;
        this.fileName = fileName;
        init(responseBody);
    }

    private void init(ResponseBody responseBody) throws IOException {
        totalSize = responseBody.contentLength();
        source = responseBody.source();
        progressSource = getProgressSource(source);
        // 写入文件
        sink = Okio.buffer(Okio.sink(new File(filePath + fileName)));
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<Object> e) {
        this.flowableEmitter = e;
        try {
            flowableEmitter.onNext(totalSize);
            sink.writeAll(Okio.buffer(progressSource));
            sink.close();
            flowableEmitter.onComplete();
        } catch (Exception exception) {
            flowableEmitter.onError(exception);
        }
    }

    public void onRead(long read) {
        downloadedSize += read == -1 ? 0 : read;
        if (totalSize <= 0) {
            onProgress(-1);
        } else {
            onProgress((int) (100 * downloadedSize / totalSize));
        }
    }

    private void onProgress(int percent) {
        if (flowableEmitter == null) return;
        if (percent == this.percent) return;
        this.percent = percent;
        if (percent >= 100) {
            percent = 100;
            flowableEmitter.onNext(percent);
            return;
        }
        flowableEmitter.onNext(percent);
    }

    private ForwardingSource getProgressSource(Source source) {
        return new ForwardingSource(source) {

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long read = super.read(sink, byteCount);
                onRead(read);
                return read;
            }
        };
    }
}
