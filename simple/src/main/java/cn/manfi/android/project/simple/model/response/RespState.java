package cn.manfi.android.project.simple.model.response;

/**
 * Created by manfi on 2017/9/22.
 */

public class RespState {

    private String capinfo_session;
    private String code;
    private String msg;
    private String serverTime;
    private String token;

    public String getCapinfo_session() {
        return capinfo_session;
    }

    public void setCapinfo_session(String capinfo_session) {
        this.capinfo_session = capinfo_session;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
