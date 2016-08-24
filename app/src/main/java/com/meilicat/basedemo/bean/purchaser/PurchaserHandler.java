package com.meilicat.basedemo.bean.purchaser;

import java.util.List;

/**
 * Created by cj on 2016/3/1.
 */
public class PurchaserHandler {



    public int msg;
    public String msgbox;


    public DataEntity data;

    public  class DataEntity {


        public HeadCountEntity headCount;

        public List<YTHListEntity> yTHList;

        public  class HeadCountEntity {
            public int yTHInToday;
            public int yTH1WeekAgo;
            public int yTH30daysAgo;
            public int yTHAll;
        }

        public  class YTHListEntity {
            public String supplierId;
            public int orderId;
            public String orderNo;
            public int productId;
            public String colorName;
            public String sizeAbbr;
            public int purchaseNum;
            public String THDate;
            public String actualTHDate;
            public String supplierName;
            public String address;
            public String contractPerson;
            public String mobilePhoneNum;
            public double totalMoneyOne;
            public double purchasedPrice;
            public String productName_cn;
            public String productNumber;
            public String fullName;
            public int con;
            public String mobileImageURL_1;
            public String avatarImageURL;
            public int isSign;//是否签约
            public String id;
            public String createTime;
        }
    }
}
