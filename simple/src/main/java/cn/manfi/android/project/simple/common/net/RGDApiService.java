package cn.manfi.android.project.simple.common.net;

import java.util.List;

import cn.manfi.android.project.simple.model.RGDNews;
import cn.manfi.android.project.simple.model.response.RGDApiResult;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import static cn.manfi.android.project.base.common.net.retrofit.RetrofitManager.DOMAIN_NAME_HEADER;

/**
 * 新闻接口
 * Created by manfi on 2017/12/22.
 */

public interface RGDApiService {

    String DOMAIN_NAME = "NewsApi";
    String API_URL = "http://eapp.rgd.com.cn";
    String API_PATH = "/api/app_new.php";

    @Headers({DOMAIN_NAME_HEADER + DOMAIN_NAME})
    @GET(API_PATH + "?method=getProgramNews")
    Observable<RGDApiResult<List<RGDNews>>> requestNews(
            @Query("id") String programId,
            @Query("start") int start,
            @Query("count") int count);
}
