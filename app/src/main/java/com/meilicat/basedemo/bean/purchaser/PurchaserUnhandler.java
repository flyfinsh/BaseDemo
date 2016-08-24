package com.meilicat.basedemo.bean.purchaser;

import java.util.List;

/**
 * Created by cj on 2016/2/27.
 * 待提货的bean
 */
public class PurchaserUnhandler {


    public int msg;
    public String msgbox;
    public DataEntity data;

    public  class DataEntity {

        public HeadCountEntity headCount;
        public List<DTHListEntity> dTHList;

        public  class HeadCountEntity {
            public int notTHInToday;
            public int notTHIn30day;
            public int notTHAll;
        }

        public  class DTHListEntity {
            public boolean isAccept;
            public String remark;
            public int isSign;
            public String supplierId;
            public int orderId;
            public String orderNo;
            public int productId;
            public String colorName;
            public String sizeAbbr;
            public int purchaseNum;
            public String THDate;
            public String supplierName;
            public String address;
            public String contractPerson;
            public String mobilePhoneNum;
            public double totalMoneyOne;
            public double purchasedPrice;
            public String productName_cn;
            public String productNumber;
            public String avatarImageURL;
            public String mobileImageURL_1;
            public String appComment;
            public String fullName;
            public String payTime;
            public int con;
            public int id;
            public String createTime;
        }
    }


}
