package cn.manfi.android.project.base.common.net.retrofit;

import okhttp3.Request;

/**
 * 请求更改回调
 * Created by manfi on 2017/9/21.
 */

public interface OnRequestProcess {

    Request onProcessRequest(Request request);
}
