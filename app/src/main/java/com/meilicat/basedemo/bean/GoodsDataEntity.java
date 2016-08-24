package com.meilicat.basedemo.bean;

import java.util.List;

/**
 * Created by user on 2016/3/1.
 */
public class GoodsDataEntity {

    private int total;

    private List<GoodsRowsEntity> rows;

    public void setTotal(int total) {
        this.total = total;
    }

    public void setRows(List<GoodsRowsEntity> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public List<GoodsRowsEntity> getRows() {
        return rows;
    }

}
