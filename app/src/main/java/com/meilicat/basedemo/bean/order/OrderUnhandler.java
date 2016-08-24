package com.meilicat.basedemo.bean.order;

import java.util.List;

/**
 * Created by cj on 2016/3/4.
 */
public class OrderUnhandler {

    public int msg;
    public String msgbox;


    public DataEntity data;

    public  class DataEntity {
        public int total;


        public List<RowsEntity> rows;

        public  class RowsEntity {
            public String supplierId;
            public String orderNo;
            public int productId;
            public String colorName;
            public String sizeAbbr;
            public int purchaseNum2;
            public double totalMoney;
            public double purchasedPrice;
            public String imageURL_1;
            public String productName_cn;
            public String productNumber;
            public String payTime;
            public String id;
            public int stateNum ;
            public int flowCount;//流程流转步数

            public boolean isHandler;
            public String finishTime;

            public ProcessedMap processedMap;
            public class ProcessedMap{
                public String opTime;
            }
            public SignedMap signedMap;
            public class SignedMap{
              public String  opTime;
              public String  signName;
              public String  signMsg;
              public String  comment;
            }

            public GoodsShippedMap goodsShippedMap;
            public class GoodsShippedMap {
                public String opTime;
            }

            public StatMap statMap;
            public class StatMap{
                public String opTime;
                public String totalMoney;
            }

            public List<ReturnEntity> returnOrderMapList;

            public class ReturnEntity{
             public String returnTime;
             public String returnQty;
             public String returnMoney;
             public String returnReason;

            }

        }
    }


}
