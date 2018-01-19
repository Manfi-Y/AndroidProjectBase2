package cn.manfi.android.project.simple.common.net;

import cn.manfi.android.project.simple.model.response.RGDApiResult;
import io.reactivex.functions.Function;

/**
 * RGD接口返回数据解析成Json
 * Created by manfi on 2017/12/25.
 */

public class RGDApiResultParser<T> implements Function<RGDApiResult<T>, T> {

    @Override
    public T apply(RGDApiResult<T> trgdApiResult) throws Exception {
        if (!trgdApiResult.getResult().isSuccess()) {
            processApiResult(trgdApiResult);
        }
        return trgdApiResult.getDataList();
    }

    protected void processApiResult(RGDApiResult<T> rgdApiResult) {

    }
}
