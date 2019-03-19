package cn.manfi.android.project.simple.ui;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.manfi.android.project.base.common.FileUtils;
import cn.manfi.android.project.base.common.log.LogUtil;
import cn.manfi.android.project.base.common.net.ApiResultObserver;
import cn.manfi.android.project.base.common.net.DownLoadSubscriber;
import cn.manfi.android.project.base.common.net.UploadSubscriber;
import cn.manfi.android.project.base.common.permission.PermissionUtils;
import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.common.net.ApiResultParser;
import cn.manfi.android.project.simple.common.net.ApiService;
import cn.manfi.android.project.simple.common.net.AppApiManager;
import cn.manfi.android.project.simple.common.net.UploadService;
import cn.manfi.android.project.simple.databinding.ActivityRetrofitSimpleBinding;
import cn.manfi.android.project.simple.model.LineType;
import cn.manfi.android.project.simple.model.News;
import cn.manfi.android.project.simple.model.OfflineDataInfo;
import cn.manfi.android.project.simple.model.UploadPhotoResult;
import cn.manfi.android.project.simple.model.request.NewsRequest;
import cn.manfi.android.project.simple.model.response.Api2Result;
import cn.manfi.android.project.simple.model.response.ApiResult;
import cn.manfi.android.project.simple.model.response.UserApiResult;
import cn.manfi.android.project.simple.ui.base.SwipeBackAppActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Retrofit Simple Created by manfi on 2017/9/21.
 */

public class RetrofitSimpleActivity extends SwipeBackAppActivity implements View.OnClickListener {

    private ActivityRetrofitSimpleBinding binding;

    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_retrofit_simple);
        rxPermissions = new RxPermissions(activity);
    }

    @Override
    protected void initView() {
        binding.btnStart1.setOnClickListener(this);
        binding.btnStart2.setOnClickListener(this);
        binding.btnStart3.setOnClickListener(this);
        binding.btnStart4.setOnClickListener(this);
        binding.btnStart5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Start1:
                requestAllLine();
                break;
            case R.id.btn_Start2:
                requestNewsList();
                break;
            case R.id.btn_Start3:
                checkOfflineDataUpdate();
                break;
            case R.id.btn_Start4:
                download(offlineDataInfo);
                break;
            case R.id.btn_Start5:
                upload();
                break;
        }
    }

    private void requestAllLine() {
        AppApiManager.getInstance().getApiService().requestAllLine("all_lines", "guangzhou")
                .delay(3, TimeUnit.SECONDS)
                .map(new ApiResultParser<List<LineType>>() {

                    @Override
                    protected void processApiResult(ApiResult<List<LineType>> apiResult) {
                        super.processApiResult(apiResult);
                        System.out.println("RetrofitSimpleActivity.processApiResult");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiResultObserver<List<LineType>>() {

                    @Override
                    public void onNext(@NonNull List<LineType> lineTypes) {
                        if (lineTypes != null) {
                            System.out.println("RetrofitSimpleActivity.onNext:" + lineTypes.size());
                        }
                    }
                });
    }

    private void requestNewsList() {
        AppApiManager.getInstance().getApi2Service().requestNewsList(new NewsRequest("0-0", 1, 20))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiResultObserver<Api2Result<List<News>>>() {

                    @Override
                    public void onNext(@NonNull Api2Result<List<News>> listApi2Result) {

                    }
                });
    }

    private OfflineDataInfo offlineDataInfo;

    public void setOfflineDataInfo(OfflineDataInfo offlineDataInfo) {
        this.offlineDataInfo = offlineDataInfo;
    }

    private void checkOfflineDataUpdate() {
        String city = "beijing";
        String dbver = "1450011152019608";
        String sid = "3b845e0a2ce49ebb9a31b64a4ad2068d";
        String ime = "000000000000000";
        String phoneModel = "Google Nexus 5 - 5.1.0 - API 22 - 1080x1920";
        int appVerCode = 1400;
        AppApiManager.getInstance().getApiService().checkOfflineDataUpdate(ApiService.CHECK_OFFLINE_DATE_URL
                , city, dbver, sid, ime, phoneModel, appVerCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiResultObserver<OfflineDataInfo>() {

                    @Override
                    public void onNext(OfflineDataInfo offlineDataInfo) {
                        setOfflineDataInfo(offlineDataInfo);
                        binding.btnStart4.setEnabled(true);
                    }
                });
    }

    private DownLoadSubscriber downLoadSubscriber;

    private void download(OfflineDataInfo offlineDataInfo) {
        String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        String filePath = FileUtils.getSDCardPath();
        String fileName = offlineDataInfo.getUrl().substring(offlineDataInfo.getUrl().lastIndexOf("/") + 1);

        downLoadSubscriber = new DownLoadSubscriber() {

            @Override
            protected void onRequestDownload() {
                LogUtil.i(DEBUG, TAG, "onRequestDownload");
                binding.pbProgress.setIndeterminate(true);
                binding.tvProgress.setText("请求下载");
            }

            @Override
            protected void onStartDownload(long totalSize) {
                LogUtil.i(DEBUG, TAG, "onStartDownload");
                binding.pbProgress.setIndeterminate(false);
                binding.pbProgress.setMax(100);
                binding.tvProgress.setText("准备下载");
            }

            @Override
            protected void onProgress(Integer percent) {
                LogUtil.i(DEBUG, TAG, "onProgress:" + percent);
                binding.pbProgress.setProgress(percent);
                binding.tvProgress.setText(String.format(Locale.getDefault(), "%d%%", percent));
            }

            @Override
            public void onComplete() {
                super.onComplete();
                LogUtil.i(DEBUG, TAG, "onDownloadFinish");
                binding.pbProgress.setIndeterminate(false);
                binding.tvProgress.setText("下载完成");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LogUtil.i(DEBUG, TAG, "onError");
                binding.pbProgress.setIndeterminate(false);
                binding.pbProgress.setProgress(0);
                binding.tvProgress.setText("下载失败");
            }
        };
        rxPermissions.request(perms)
                .toFlowable(BackpressureStrategy.LATEST)
                .concatMap(granted -> {
                    if (granted) {
                        File file = new File(filePath, fileName);
                        return AppApiManager.getInstance().download(offlineDataInfo.getUrl(), filePath, fileName, file.length()).delaySubscription(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread());
                    } else if (!PermissionUtils.somePermissionsNeedAskAgain(activity, perms)) {
                        askPermanentlyDeniedPermission(PermissionUtils.checkPermissions(activity, perms));
                    }
                    return Flowable.error(new Exception("没有权限"));
                })
                .subscribe(downLoadSubscriber);
    }

    private void upload() {
        UploadSubscriber<UserApiResult<UploadPhotoResult>> uploadSubscriber = new UploadSubscriber<UserApiResult<UploadPhotoResult>>() {

            @Override
            protected void onRequestUpload() {
                LogUtil.i(DEBUG, TAG, "onRequestUpload");
            }

            @Override
            protected void onStartUpload(long totalSize) {
                LogUtil.i(DEBUG, TAG, "onStartUpload:" + totalSize);
            }

            @Override
            protected void onProgress(Integer percent) {
                LogUtil.i(DEBUG, TAG, "onProgress");
            }

            @Override
            protected void onResult(UserApiResult<UploadPhotoResult> result) {
                LogUtil.i(DEBUG, TAG, "onResult:" + result.getData().getInfo());
            }

            @Override
            public void onComplete() {
                super.onComplete();
                LogUtil.i(DEBUG, TAG, "onComplete");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }


        };
        File photoFile = new File(FileUtils.getSDCardPath() + "img-6393a94691b9b46aeeaf8a17f9515ad4.jpg");
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("appkey", "Yv9cL8wTwZgr");
        paramsMap.put("ecity", "guangzhou");
        paramsMap.put("type", 1);
        paramsMap.put("name", "1路");
        paramsMap.put("code", "322e21c5");
        paramsMap.put("userid", "50456890");
        paramsMap.put("username", "XPHFDQCP");
        AppApiManager.getInstance().upload("https://api.8684.cn/v1/bus/img/add", UploadService.class, "upload", "imgfile", photoFile, paramsMap)
                .safeSubscribe(uploadSubscriber);
    }
}
