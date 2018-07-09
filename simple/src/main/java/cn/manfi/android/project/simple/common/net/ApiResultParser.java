package cn.manfi.android.project.simple.common.net;

import cn.manfi.android.project.simple.model.response.ApiResult;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 接口返回数据解析成Json
 * Created by manfi on 2017/9/21.
 */

public class ApiResultParser<T> implements Function<ApiResult<T>, T> {

    @Override
    public T apply(@NonNull ApiResult<T> tApiResult) {
        if (!tApiResult.isSuccess()) {
            processApiResult(tApiResult);
        }
        return tApiResult.getData();
    }

    /**
     * 处理其它业务返回值
     *
     * @param apiResult ~
     */
    protected void processApiResult(ApiResult<T> apiResult) {

    }
}