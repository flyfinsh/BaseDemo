package com.meilicat.basedemo.bean;

/**
 * Created by cj on 2016/2/26.
 */
public class MoneyChangeBean {
    /**
     * moneyLogId : 678
     */

    public DataEntity data;
    /**
     * data : {"moneyLogId":"678"}
     * msg : 1
     * msgbox : 请求成功!
     */

    public int msg;
    public String msgbox;
    public  class DataEntity {
        public String moneyLogId;
    }
}
