package cn.manfi.android.project.base.common.net;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.manfi.android.project.base.common.net.retrofit.CustomGsonConverterFactory;
import cn.manfi.android.project.base.common.net.retrofit.RetrofitManager;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Api管理工具 Created by manfi on 2018/1/18.
 */

public class ApiManager {

    protected OkHttpClient okHttpClient;
    protected Retrofit retrofit;

    protected DownloadService downloadService;

    public ApiManager() {
        okHttpClient = createOkHttpClient();
        retrofit = createRetrofit();
    }

    protected OkHttpClient createOkHttpClient() {
        HttpLoggingInterceptor interceptorLog = new HttpLoggingInterceptor();
        /*
        不能使用BODY来输出API返回结果，不然下载进度的ResponseBody会被消费掉
         */
        interceptorLog.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return RetrofitManager.getInstance().with(new OkHttpClient.Builder()) // RetrofitManager 初始化
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptorLog)
                .build();
    }

    protected Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://127.0.0.1")   // 随便填一个或空，会用拦截器动态改变url
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // 使用rxjava
                .addConverterFactory(CustomGsonConverterFactory.create())   // 使用自定义Gson处理返回结果log输出
                .build();
    }

    public synchronized Flowable<Object> download(@NonNull String url, @NonNull String filePath, @NonNull String fileName, long startPoint) {
        if (downloadService == null) {
            downloadService = retrofit.create(DownloadService.class);
        }

        return Flowable.just(url)
                .subscribeOn(Schedulers.io())
                .flatMap(downloadUrl -> downloadService.download("bytes=" + startPoint + "-", downloadUrl))
                .compose(new DownLoadTransformer(filePath, fileName))
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 上传文件
     *
     * @param url                API地址
     * @param uploadServiceClass 上传API
     * @param uploadFucntionName 上传API方法名
     * @param uploadParamsName   上传文件参数名
     * @param file               上传文件
     * @param paramMap           上传参数
     * @param <T>                API Service类型
     *
     * @return ~
     */
    public synchronized <T> Flowable<Object> upload(@NonNull String url, @NonNull Class<T> uploadServiceClass, @NonNull String uploadFucntionName, @NonNull String uploadParamsName, @NonNull File file, Map<String, Object> paramMap) {
        try {
            T service = retrofit.create(uploadServiceClass);
            // 获得上传方法
            Method uploadMethod = uploadServiceClass.getMethod(uploadFucntionName, String.class, MultipartBody.Part.class, Map.class);

            // 进度Observable
            UploadOnSubscribe uploadOnSubscribe = new UploadOnSubscribe(file.length());
            Flowable<Object> progressObservale = Flowable.create(uploadOnSubscribe, BackpressureStrategy.BUFFER);

            UploadRequestBody uploadRequestBody = new UploadRequestBody(file);

            // 进度监听
            uploadRequestBody.setUploadOnSubscribe(uploadOnSubscribe);

            // 上传文件参数
            MultipartBody.Part filePart = MultipartBody.Part.createFormData(uploadParamsName, file.getName(), uploadRequestBody);
            Map<String, RequestBody> partMap = null;

            if (paramMap != null) {
                partMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                    partMap.put(entry.getKey(), RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(entry.getValue())));
                }
            }

            // 运行上传方法
            Object o = uploadMethod.invoke(service, url, filePart, partMap);
            if (o instanceof Flowable) {
                Flowable<Object> uploadFlowable = (Flowable) o;

                // 合并Observable
                return Flowable.merge(progressObservale, uploadFlowable)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Flowable.error(e);
        }
        return Flowable.error(new Exception("没有找到上传方法"));
    }
}
