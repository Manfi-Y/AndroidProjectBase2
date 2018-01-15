package cn.manfi.android.project.simple.bean;

/**
 * RGD新闻
 * Created by manfi on 2017/12/22.
 */

public class RGDNews {

    /**
     * 新闻ID
     */
    private String newsid;
    /**
     * 新闻标题
     */
    private String title;
    /**
     * 节目名称
     */
    private String classname;
    /**
     * 节目ID
     */
    private String classid;
    /**
     * 图片路径
     */
    private String img;
    /**
     * 新闻URI地址
     */
    private String titleurl;
    /**
     * 新闻简述
     */
    private String summary;
    /**
     * 音频地址
     */
    private String audio;
    /**
     * 发布时间
     */
    private long newstime;

    public String getNewsid() {
        return newsid;
    }

    public void setNewsid(String newsid) {
        this.newsid = newsid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitleurl() {
        return titleurl;
    }

    public void setTitleurl(String titleurl) {
        this.titleurl = titleurl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public long getNewstime() {
        return newstime;
    }

    public void setNewstime(long newstime) {
        this.newstime = newstime;
    }
}
