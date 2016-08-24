package com.meilicat.basedemo.bean.money;

import java.util.List;

/**
 * Created by cj on 2016/2/29.
 */
public class OutMoneyBean {
    public DataEntity data;
    public int msg;
    public String msgbox;

    public  class DataEntity {


        public List<SupplierMoneyLogListEntity> supplierMoneyLogList;
        /**
         * msg : 1
         * msgbox : 请求成功!
         * data :
         */
        public  class SupplierMoneyLogListEntity {
            public double balance;
            public String comment;
            public double money;
            public String tradeTime;
            public int type;
            public String id;
        }
    }




}
