package cn.manfi.android.project.base.common.net.retrofit;

import okhttp3.HttpUrl;

/**
 * ~
 * Created by manfi on 2017/9/20.
 */

public interface UrlChangeListener {

    /**
     * 当 Url 的 BaseUrl 被改变时回调
     * 调用时间是在接口请求服务器之前
     *
     * @param newUrl ~
     * @param oldUrl ~
     */
    void onUrlChange(HttpUrl newUrl, HttpUrl oldUrl);
}
