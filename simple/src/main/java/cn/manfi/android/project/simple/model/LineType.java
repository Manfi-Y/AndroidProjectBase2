package cn.manfi.android.project.simple.model;

import java.util.List;

/**
 * ~
 * Created by manfi on 2017/9/21.
 */

public class LineType {

    private String type;
    private List<Line> lines;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }
}
