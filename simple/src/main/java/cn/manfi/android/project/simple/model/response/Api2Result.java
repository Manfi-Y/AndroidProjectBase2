package cn.manfi.android.project.simple.model.response;

/**
 * Created by manfi on 2017/9/22.
 */

public class Api2Result<T> {

    private RespState respState;
    private T datalist;

    public RespState getRespState() {
        return respState;
    }

    public void setRespState(RespState respState) {
        this.respState = respState;
    }

    public T getDatalist() {
        return datalist;
    }

    public void setDatalist(T datalist) {
        this.datalist = datalist;
    }
}
