package cn.manfi.android.project.simple.bean.request;

/**
 * Created by manfi on 2017/9/22.
 */

public class NewsRequest {

    private String code;
    private int pageNo;
    private int pageSize;

    public NewsRequest(String code, int pageNo, int pageSize) {
        this.code = code;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
