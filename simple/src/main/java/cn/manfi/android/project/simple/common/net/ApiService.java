package cn.manfi.android.project.simple.common.net;

import java.util.List;

import cn.manfi.android.project.simple.bean.LineType;
import cn.manfi.android.project.simple.bean.OfflineDataInfo;
import cn.manfi.android.project.simple.bean.response.ApiResult;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

import static cn.manfi.android.project.base.common.net.retrofit.RetrofitManager.DOMAIN_NAME_HEADER;

/**
 * ApiService
 * Created by manfi on 2017/9/21.
 */

public interface ApiService {

    String DOMAIN_NAME = "Api";
    String API_URL = "http://api.8684.cn/";
    String API_PATH = "bus_api_v1.php";

    String CHECK_OFFLINE_DATE_URL = "http://update1.8684.cn/checkupdate5.php";

    /**
     * 所有线路
     *
     * @param k     all_lines
     * @param eCity 城市拼音
     *
     * @return ~
     */
    @Headers({DOMAIN_NAME_HEADER + DOMAIN_NAME})
    @FormUrlEncoded()
    @POST(API_PATH)
    Observable<ApiResult<List<LineType>>> requestAllLine(@Field("k") String k, @Field("ecity") String eCity);

    @GET()
    Observable<OfflineDataInfo> checkOfflineDataUpdate(@Url String url
            , @Query("city") String eCity
            , @Query("dbver") String dbver
            , @Query("sid") String sid
            , @Query("ime") String ime
            , @Query("sjmodel") String phoneModel
            , @Query("appvercode") int appVerCode);
}
