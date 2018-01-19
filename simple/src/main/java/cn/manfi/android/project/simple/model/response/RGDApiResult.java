package cn.manfi.android.project.simple.model.response;

/**
 * ~
 * Created by manfi on 2017/12/22.
 */

public class RGDApiResult<T> {

    private RGDApiResultState result;
    private T datalist;

    public RGDApiResultState getResult() {
        return result;
    }

    public void setResult(RGDApiResultState result) {
        this.result = result;
    }

    public T getDataList() {
        return datalist;
    }

    public void setDataList(T datalist) {
        this.datalist = datalist;
    }
}
