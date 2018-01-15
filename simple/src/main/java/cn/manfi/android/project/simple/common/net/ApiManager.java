package cn.manfi.android.project.simple.common.net;

import java.util.concurrent.TimeUnit;

import cn.manfi.android.project.base.common.net.retrofit.CustomGsonConverterFactory;
import cn.manfi.android.project.base.common.net.retrofit.RetrofitManager;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Api管理工具
 * Created by manfi on 2017/9/21.
 */

public class ApiManager {

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;

    private ApiService apiService;
    private Api2Service api2Service;
    private RGDApiService rgdApiService;
    private DownloadService downloadService;

    private static class ApiManagerHolder {

        private static final ApiManager INSTANCE = new ApiManager();
    }

    public static final ApiManager getInstance() {
        return ApiManagerHolder.INSTANCE;
    }

    private ApiManager() {
        HttpLoggingInterceptor interceptorLog = new HttpLoggingInterceptor();
        /*
        不能使用BODY来输出API返回结果，不然下载进度的ResponseBody会被消费掉
         */
        interceptorLog.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        okHttpClient = RetrofitManager.getInstance().with(new OkHttpClient.Builder()) // RetrofitManager 初始化
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptorLog)
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://127.0.0.1")   // 随便填一个或空，会用拦截器动态改变url
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // 使用rxjava
                .addConverterFactory(CustomGsonConverterFactory.create())   // 使用自定义Gson处理返回结果log输出
                .build();
    }

    public synchronized ApiService getApiService() {
        if (apiService == null) {
            apiService = retrofit.create(ApiService.class);
            RetrofitManager.getInstance().putDomain(ApiService.DOMAIN_NAME, ApiService.API_URL);
            HttpUrl url = HttpUrl.parse(ApiService.API_URL);
            if (url != null) {
                RetrofitManager.getInstance().putRequestProcess(url.host(), request -> {
                    Request.Builder newRequest = request.newBuilder();

                    // 添加公共参数
                    if (request.method().equals("GET")) {
                        HttpUrl newUrl = request.url().newBuilder()
                                .addQueryParameter("appkey", "Yv9cL8wTwZgr")
                                .build();
                        return newRequest.url(newUrl).build();
                    }
                    if (request.method().equals("POST")) {
                        if (request.body() instanceof FormBody) {
                            FormBody.Builder bodyBuilder = new FormBody.Builder();
                            FormBody formBody = (FormBody) request.body();
                            if (formBody != null) {
                                //把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的）
                                for (int i = 0; i < formBody.size(); i++) {
                                    bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                                }

                                formBody = bodyBuilder
                                        .addEncoded("appkey", "Yv9cL8wTwZgr")
                                        .build();
                                request = newRequest.post(formBody).build();
                            }
                        }
                    }
                    return request;
                });
            }
        }
        return apiService;
    }

    public synchronized Api2Service getApi2Service() {
        if (api2Service == null) {
            api2Service = retrofit.create(Api2Service.class);
            RetrofitManager.getInstance().putDomain(Api2Service.DOMAIN_NAME, Api2Service.API_URL);
        }
        return api2Service;
    }

    public synchronized RGDApiService getRGDApiService() {
        if (rgdApiService == null) {
            rgdApiService = retrofit.create(RGDApiService.class);
            RetrofitManager.getInstance().putDomain(RGDApiService.DOMAIN_NAME, RGDApiService.API_URL + "/");
        }
        return rgdApiService;
    }

    public synchronized DownloadService getDownloadService() {
        if (downloadService == null) {
            downloadService = retrofit.create(DownloadService.class);
        }
        return downloadService;
    }
}
