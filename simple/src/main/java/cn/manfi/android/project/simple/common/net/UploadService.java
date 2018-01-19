package cn.manfi.android.project.simple.common.net;

import java.util.Map;

import cn.manfi.android.project.simple.model.UploadPhotoResult;
import cn.manfi.android.project.simple.model.response.UserApiResult;
import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

/**
 * 上传API
 * <p>
 * 请保持上传接口参数顺序为
 * String url、MultipartBody.Part file、OtherParams...
 * </p>
 * Created by manfi on 2018/1/16.
 */

public interface UploadService {

    @Multipart
    @POST
    Flowable<UserApiResult<UploadPhotoResult>> upload(@Url String url, @Part MultipartBody.Part file, @PartMap Map<String, RequestBody> partMap);
}
