package cn.manfi.android.project.simple.common.net;

import cn.manfi.android.project.base.common.net.ApiManager;
import cn.manfi.android.project.base.common.net.retrofit.RetrofitManager;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

/**
 * Api管理工具
 * Created by manfi on 2017/9/21.
 */

public class AppApiManager extends ApiManager {

    private ApiService apiService;
    private Api2Service api2Service;
    private RGDApiService rgdApiService;

    private static class ApiManagerHolder {

        private static final AppApiManager INSTANCE = new AppApiManager();
    }

    public static final AppApiManager getInstance() {
        return AppApiManager.ApiManagerHolder.INSTANCE;
    }

    private AppApiManager() {
    }

    @Override
    protected OkHttpClient createOkHttpClient() {
        // 重写这里可以根据需求新建OkHttpClient
        return super.createOkHttpClient();
    }

    @Override
    protected Retrofit createRetrofit() {
        // 重写这里可以根据需求新建Retrofit
        return super.createRetrofit();
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
}
