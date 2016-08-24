package com.meilicat.basedemo.bean.purchaser;

/**
 * Created by cj on 2016/2/27.
 */
public class PurchaserDetail {



    public DataEntity data;


    public int msg;
    public String msgbox;

    public  class DataEntity {


        public PurchaserEntity purchaser;

        public  class PurchaserEntity {
            public String account;
            public int dBHProductNum;
            public String fullName;
            public int yTHProductNum;
        }
    }
}
