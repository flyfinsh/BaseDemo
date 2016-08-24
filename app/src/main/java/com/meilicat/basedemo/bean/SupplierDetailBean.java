package com.meilicat.basedemo.bean;

/**
 * Created by cj on 2016/2/23.
 */
public class SupplierDetailBean {



    public int msg;
    public String msgbox;


    public DataEntity data;

    public static class DataEntity {


        public SupplierEntity supplier;

        public static class SupplierEntity {
            public String description;
            public String address;
            public String faxNum;
            public String telNum;
            public String contractPerson;
            public String webSite;
            public String name;
            public double money;
            public String accountHolder;
            public String mobilePhoneNum;
            public String bankName;
            public String bankNumber;
            public String id;
            public String abbr;
            public int isValid;
            public String createUserId;
            public String updateUserId;
            public String createTime;
            public String updateTime;
        }
    }
}
