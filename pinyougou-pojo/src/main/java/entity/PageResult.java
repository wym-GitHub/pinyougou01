package entity;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {
        //定义一个总记录数
    private long total;
    private List rows;//查询当前页的结果集

    public PageResult() {
    }

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
