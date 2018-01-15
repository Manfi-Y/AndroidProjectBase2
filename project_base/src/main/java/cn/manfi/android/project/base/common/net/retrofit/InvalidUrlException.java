package cn.manfi.android.project.base.common.net.retrofit;

import android.text.TextUtils;

/**
 * ~
 * Created by manfi on 2017/9/20.
 */

public class InvalidUrlException extends RuntimeException {

    public InvalidUrlException(String url) {
        super("You've configured an invalid url : " + (TextUtils.isEmpty(url) ? "EMPTY_OR_NULL_URL" : url));
    }
}
