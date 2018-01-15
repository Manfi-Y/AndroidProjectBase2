package cn.manfi.android.project.simple.common.net;

import java.util.List;

import cn.manfi.android.project.simple.bean.News;
import cn.manfi.android.project.simple.bean.request.NewsRequest;
import cn.manfi.android.project.simple.bean.response.Api2Result;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static cn.manfi.android.project.base.common.net.retrofit.RetrofitManager.DOMAIN_NAME_HEADER;

/**
 * ~
 * Created by manfi on 2017/9/22.
 */

public interface Api2Service {

    String DOMAIN_NAME = "Api2";
    String API_URL = "http://www.nngjj.com:8877/";
    String API_PATH = "chs-app-nn";

    @Headers({DOMAIN_NAME_HEADER + DOMAIN_NAME})
    @POST(API_PATH + "/open/zxdtlb.html")
    Observable<Api2Result<List<News>>> requestNewsList(@Body NewsRequest newsRequest);
}
