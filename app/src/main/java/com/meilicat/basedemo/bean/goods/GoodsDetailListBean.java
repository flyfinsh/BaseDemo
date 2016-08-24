package com.meilicat.basedemo.bean.goods;

import java.util.List;

/**
 * Created by user on 2016/3/3.
 */
public class GoodsDetailListBean {

    private int msg;
    private String msgbox;

    private DataEntity data;

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public int getMsg() {
        return msg;
    }

    public String getMsgbox() {
        return msgbox;
    }

    public DataEntity getData() {
        return data;
    }

    public class DataEntity {
        private int total;

        private List<RowsEntity> rows;

        public void setTotal(int total) {
            this.total = total;
        }

        public void setRows(List<RowsEntity> rows) {
            this.rows = rows;
        }

        public int getTotal() {
            return total;
        }

        public List<RowsEntity> getRows() {
            return rows;
        }

        public class RowsEntity {
            private int statLogId;
            private int supplierId;
            private String statTime;
            private String orderNo;
            private int productId;
            private String colorName;
            private String sizeName;
            private int qty;
            private double unitPrice;
            private double totalPrice;
            private String payTime;

            private String sizeAbbr;
            private int purchaseNum2;
            private String THDate;
            private String actualTHDate;
            /**
             * opTime : 货品需在3月2日18点前备齐
             */

            private ChildItemEntity processedMap;
            /**
             * opTime : 2016-03-03
             * signName :
             * signMsg : 晚1天签收
             * comment : 测试备注1
             */

            private ChildItemEntity signedMap;
            /**
             * opTime : 2016-03-01
             * totalMoney : 840.0
             */

            private ChildItemEntity statMap;


            public void setStatLogId(int statLogId) {
                this.statLogId = statLogId;
            }

            public void setSupplierId(int supplierId) {
                this.supplierId = supplierId;
            }

            public void setStatTime(String statTime) {
                this.statTime = statTime;
            }

            public void setOrderNo(String orderNo) {
                this.orderNo = orderNo;
            }

            public void setProductId(int productId) {
                this.productId = productId;
            }

            public void setColorName(String colorName) {
                this.colorName = colorName;
            }

            public void setSizeName(String sizeName) {
                this.sizeName = sizeName;
            }

            public void setQty(int qty) {
                this.qty = qty;
            }

            public void setUnitPrice(double unitPrice) {
                this.unitPrice = unitPrice;
            }

            public void setTotalPrice(double totalPrice) {
                this.totalPrice = totalPrice;
            }

            public void setPayTime(String payTime) {
                this.payTime = payTime;
            }

            public String getSizeAbbr() {
                return sizeAbbr;
            }

            public void setSizeAbbr(String sizeAbbr) {
                this.sizeAbbr = sizeAbbr;
            }

            public int getPurchaseNum2() {
                return purchaseNum2;
            }

            public void setPurchaseNum2(int purchaseNum2) {
                this.purchaseNum2 = purchaseNum2;
            }

            public String getTHDate() {
                return THDate;
            }

            public void setTHDate(String THDate) {
                this.THDate = THDate;
            }

            public String getActualTHDate() {
                return actualTHDate;
            }

            public void setActualTHDate(String actualTHDate) {
                this.actualTHDate = actualTHDate;
            }

            public void setProcessedMap(ChildItemEntity processedMap) {
                this.processedMap = processedMap;
            }

            public void setSignedMap(ChildItemEntity signedMap) {
                this.signedMap = signedMap;
            }

            public void setStatMap(ChildItemEntity statMap) {
                this.statMap = statMap;
            }

            public int getStatLogId() {
                return statLogId;
            }

            public int getSupplierId() {
                return supplierId;
            }

            public String getStatTime() {
                return statTime;
            }

            public String getOrderNo() {
                return orderNo;
            }

            public int getProductId() {
                return productId;
            }

            public String getColorName() {
                return colorName;
            }

            public String getSizeName() {
                return sizeName;
            }

            public int getQty() {
                return qty;
            }

            public double getUnitPrice() {
                return unitPrice;
            }

            public double getTotalPrice() {
                return totalPrice;
            }

            public String getPayTime() {
                return payTime;
            }

            public ChildItemEntity getProcessedMap() {
                return processedMap;
            }

            public ChildItemEntity getSignedMap() {
                return signedMap;
            }

            public ChildItemEntity getStatMap() {
                return statMap;
            }

            public class ChildItemEntity {
                private String title;
                private String opTime;
                private String signName;
                private String signMsg;
                private String comment;

                private String totalMoney;
                public boolean isHandled;
                public boolean isMoneyed;

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public void setOpTime(String opTime) {
                    this.opTime = opTime;
                }

                public void setSignName(String signName) {
                    this.signName = signName;
                }

                public void setSignMsg(String signMsg) {
                    this.signMsg = signMsg;
                }

                public void setComment(String comment) {
                    this.comment = comment;
                }

                public String getOpTime() {
                    return opTime;
                }

                public String getSignName() {
                    return signName;
                }

                public String getSignMsg() {
                    return signMsg;
                }

                public String getComment() {
                    return comment;
                }

                public void setTotalMoney(String totalMoney) {
                    this.totalMoney = totalMoney;
                }

                public String getTotalMoney() {
                    return totalMoney;
                }
            }

        }
    }
}
