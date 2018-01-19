package cn.manfi.android.project.simple.model.response;

/**
 * Created by manfi on 2018/1/16.
 */

public class UserApiResult<T> {

    public static final int CODE_SUCCESS = 0;
    // 登录超时
    public static final int CODE_LOGIN_TIMEOUT = 31;
    // SID 超时
    public static final int CODE_SID_TIMEOUT = 200;

    /**
     * 错误代码
     * <p>
     * 0:成功
     * </p>
     */
    private int error_code;
    /**
     * 错误信息
     */
    private String error_message;
    /**
     * 接口返回数据
     * <p>
     * JsonArray格式
     * 里面主要放一条详细信息
     * </p>
     */
    private T data;
    /**
     * 接口返回数据
     * <p>
     * JsonArray格式
     * 里面主要放列表
     * </p>
     */
    private T list;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
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

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }

    public boolean isSuccess() {
        return error_code == CODE_SUCCESS;
    }
}
