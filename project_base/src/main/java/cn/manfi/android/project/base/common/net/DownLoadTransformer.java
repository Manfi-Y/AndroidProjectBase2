package cn.manfi.android.project.base.common.net;

import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;

/**
 * 下载进度观察转换器
 * <p>
 * 与下载Api Observable进行组合操作，通过不断发送时间来观察ResponseBody来更新进度和写入文件
 * </p>
 * Created by manfi on 2018/1/12.
 */

public class DownLoadTransformer implements FlowableTransformer<ResponseBody, Object> {

    /**
     * 写入文件地址
     */
    private String filePath;
    /**
     * 写入文件名
     */
    private String fileName;

    public DownLoadTransformer(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    public Publisher<Object> apply(@NonNull Flowable<ResponseBody> upstream) {
        return upstream.flatMap(responseBody -> {
            DownLoadOnSubscribe downLoadOnSubscribe = new DownLoadOnSubscribe(responseBody, filePath, fileName);
            return Flowable.create(downLoadOnSubscribe, BackpressureStrategy.BUFFER);
        });
    }

}
