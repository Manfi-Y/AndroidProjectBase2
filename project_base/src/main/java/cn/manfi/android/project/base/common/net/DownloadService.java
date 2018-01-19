package cn.manfi.android.project.base.common.net;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 下载Service
 * Created by manfi on 2017/10/27.
 */

public interface DownloadService {

    @GET
    @Streaming
    Flowable<ResponseBody> download(@Url String url);
}
