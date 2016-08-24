package com.meilicat.basedemo.conf;


import com.meilicat.basedemo.utils.LogUtils;

public class Constants {
    /**
     * LogUtils.LEVEL_ALL:显示所有的日志信息-->打开日志开关
     * LogUtils.LEVEL_OFF:隐藏所有的日志信息-->关闭日志开关
     */
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;
    public static final long PROTOCOLTIMEOUT = 60 * 60 * 1000;

    public static final class URLS {

        public static final String TESTBASEURL = "http://192.168.1.112:9100";
        public static final String TESTBASEURL2 = "http://192.168.1.104:9100";

        public static final String BASEURL = "http://h.meilicat.com:8080";
//        public static final String BASEURL = "http://192.168.1.104:9100";
        public static final String BASEURL_RELEASE = "http://h.meilicat.com";


        /*<---------------------- 地址的链接 start ---------------------->*/
        public static final String SUPPLIER_TOTAL = BASEURL + "/supplier/getSupplierDetail";
        public static final String SUPPLIER_MONEY_DETAIL = BASEURL + "/supplier/getMoneyDetail";
        public static final String SUPPLIER_MONEY_OUTACCOUNT = BASEURL + "/supplier/getOutAccountDetail";
        public static final String SUPPLIER_MONEY_INACCOUNT = BASEURL + "/supplier/getInAccountDetail";
        public static final String SUPPLIER_MONEY_WITHDRAWALS = BASEURL + "/supplier/getWithdrawals";
        public static final String SUPPLIER_MONEY_OUTMONEY = BASEURL + "/supplierMoneyLog/getWithdrawalsDetail";

        public static final String SUPPLIER_GET_DETAIL_BY_TIME = BASEURL + "/supplier/getMoneyDetailByTime";


        public static final String SUPPLIER_USERINFO = BASEURL + "/userInfo/getSupplierInfo";
        public static final String MSG_LIST = BASEURL + "/userAnnounce/searchSupp?";
        public static final String MSG_DETAIL = BASEURL + "/userAnnounce/item?";
        public static final String ACCOUTN_MODIFY = BASEURL + "/userInfo/saveSupplierInfo";
        public static final String PSW_MODIFY = BASEURL + "/userInfo/modifyPwd";
        public static final String FEEDBACK = BASEURL + "/userInfo/feedbackSupp";
        public static final String CHECK_VERSION = BASEURL + "/userInfo/SuppAppVersion?";

        public static final String ORDER_MAIN = BASEURL + "/supplierOrder/orderData";

        //商品管理待上架、已上架、下架数量
        public static final String GOODS_MANAGER_COUNT = BASEURL + "/supplierProduct/searchProductCount";
        //待上架
        public static final String GOODS_WAIT_ONLINE = BASEURL + "/supplierProduct/searchWaitOnlineList?";
        //已上架、已下架
        public static final String GOODS_UP_DOWN_ONLINE = BASEURL + "/supplierProduct/searchOnlineList?";
        //查询商品详情
        public static final String GOODS_DETAIL_GET = BASEURL + "/supplierProduct/searchProductDetail?";
        //上传图片
        public static final String GOODS_IMAGE_UPLOAD = BASEURL + "/fileupload/uploadSupplierImage";
        //商品分类列表
        public static final String GOODS_CATEGORY_LIST = BASEURL + "/supplierProduct/categoryList";
        //商品季节列表
        public static final String GOODS_SEASON_LIST = BASEURL + "/supplierProduct/seasonList";
        //商品颜色列表
        public static final String GOODS_COLOR_LIST = BASEURL + "/supplierProduct/colorList";
        //商品尺码列表
        public static final String GOODS_SIZE_LIST = BASEURL + "/supplierProduct/sizeList";
        //供应商添加商品
        public static final String GOODS_ADD = BASEURL + "/supplierProduct/addProduct";
        //供应商编辑商品
        public static final String GOODS_EDIT = BASEURL + "/supplierProduct/editProduct";
        //商品在途中、已卖出列表
        public static final String GOODS_STATU_LIST = BASEURL + "/supplierProduct/searchSalesAndTransitList?";
        //商品删除
        public static final String GOODS_DELETE = BASEURL + "/supplierProduct/deleteProduct";


        public static final String BUYER_BUYERDETAIL = BASEURL + "/purchaser/getPurchaserIndex";//TODO
        public static final String BUYER_UNHANDLER = BASEURL + "/supplierPurchaseDetail/getDTHProductDetail";
        public static final String BUYER_SIGN = BASEURL + "/supplierPurchaseDetail/signSure?";
        public static final String BUYER_HANDLER = BASEURL + "/supplierPurchaseDetail/getYTHProductDetail";
        public static final String BUYER_HANDLER_DETAIL = BASEURL + "/supplierPurchaseDetail/getOrderDetail";
        public static final String BUYER_HANDLER_ORDER = BASEURL + "/purchaseOrder/generatePurchaseOrder";

        public static final String ORDER_UNHANDLER = BASEURL + "/supplierOrder/waitingOrderList";
        public static final String ORDER_WAITREADY = BASEURL + "/supplierOrder/waitingBeReadyList";
        public static final String ORDER_WAITDELIVERY = BASEURL + "/supplierOrder/waitingGoodsOrderList";
        public static final String ORDER_MARKREADY = BASEURL + "/supplierOrder/markReadyTime?";

        public static final String ORDER_RETURN = BASEURL + "/supplierOrder/nowReturnOrderList";
        public static final String ORDER_TODAYADD = BASEURL + "/supplierOrder/nowAddOrderList";
        public static final String ORDER_FINISH = BASEURL + "/supplierOrder/nowStatOrderList";


        public static final String LOGIN = BASEURL + "/loginIndex";//TODO
        public static final String OUTLOGIN = BASEURL + "/loginOut";

        /*<---------------------- 地址的链接  end ---------------------->*/

    }
}
