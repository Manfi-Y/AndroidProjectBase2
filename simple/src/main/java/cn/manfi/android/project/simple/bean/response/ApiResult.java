package cn.manfi.android.project.simple.bean.response;

import android.text.TextUtils;

/**
 * Api 结果
 * Created by manfi on 2017/9/21.
 */

public class ApiResult<T> {

    public static final String CODE_SUCCESS = "0";

    /**
     * 错误信息
     */
    private String error_message;
    /**
     * 获取到的数据
     */
    private T data;

    public boolean isSuccess() {
        return TextUtils.equals(error_message, CODE_SUCCESS);
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
