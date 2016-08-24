package com.meilicat.basedemo.bean.purchaser;

/**
 * Created by cj on 2016/3/2.
 */
public class PurchaserHanlderDetail {


    public DataEntity data;


    public int msg;
    public String msgbox;

    public  class DataEntity {


        public SupplierPurchaseDetailShowEntity supplierPurchaseDetailShow;

        public  class SupplierPurchaseDetailShowEntity {
            public String THDate;
            public String colorName;
            public String actualTHDate;
            public String payTime;
            public String fullName;
            public String appComment;
            public String mobileImageURL_1;
            public String orderNo;
            public String productName_cn;
            public String productNumber;
            public int purchaseNum;
            public String sizeAbbr;
            public double totalPurchasedPrice;
            public String comment;

        }
    }
}
