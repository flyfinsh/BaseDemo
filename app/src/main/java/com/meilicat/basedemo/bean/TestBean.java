package com.meilicat.basedemo.bean;

/**
 * Created by cj on 2016/1/26.
 */
public class TestBean {
    /**
     * msg : 1
     * msgbox : 请求成功!
     * data : {"dTHList":[],"headCount":{"notTHInToday":0,"notTHIn30day":0,"notTHAll":0}}
     */

    public int msg;
    public String msgbox;
    /**
     * dTHList : []
     * headCount : {"notTHInToday":0,"notTHIn30day":0,"notTHAll":0}
     */

    public DataEntity data;
    //这是测试的bean
    private int dataId;

    public String orderNum ;
    public boolean isHandler;
    public String payTime;
    public String finishTime;
    public String goodsName;
    public  String goodsNum;
   public  String goodsColor;
   public  String goodsSize;
   public  String goodsSumNum;
   public  String goodsPrice;
   public boolean state;
   public String signState;
   public String signTime;
   public String signName;

   public String signInfo;
   public String acceptInfo;
    public boolean isAccept;//是否签收了
    public boolean isChecked;//是否选择了


    public static class DataEntity {
        /**
         * notTHInToday : 0
         * notTHIn30day : 0
         * notTHAll : 0
         */


    }
}
