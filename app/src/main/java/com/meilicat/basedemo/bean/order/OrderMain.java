package com.meilicat.basedemo.bean.order;

/**
 * Created by cj on 2016/2/29.
 */
public class OrderMain {



    public DataEntity data;

    public int msg;
    public String msgbox;

    public  class DataEntity {
        public int nowAddOrderCount;
        public int nowReturnOrderCount;
        public int nowStatOrderCount;
        public int waitBeReadyCount;
        public int waitGoodsCount;
        public int waitOrderCount;
    }
}
