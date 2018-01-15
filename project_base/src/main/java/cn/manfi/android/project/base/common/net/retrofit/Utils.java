package cn.manfi.android.project.base.common.net.retrofit;

import okhttp3.HttpUrl;

/**
 * ~
 * Created by manfi on 2017/9/20.
 */

public class Utils {

    private Utils() {
        throw new IllegalStateException("do not instantiation me");
    }

    static HttpUrl checkUrl(String url) {
        HttpUrl parseUrl = HttpUrl.parse(url);
        if (null == parseUrl) {
            throw new InvalidUrlException(url);
        } else {
            return parseUrl;
        }
    }
}
