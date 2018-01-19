package cn.manfi.android.project.simple.model.response;

import android.text.TextUtils;

/**
 * ~
 * Created by manfi on 2017/12/22.
 */

public class RGDApiResultState {

    public static final String STATE_SUCCESS = "000";

    private String resCode;
    private String resMsg;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public boolean isSuccess() {
        return TextUtils.equals(STATE_SUCCESS, resCode);
    }
}
