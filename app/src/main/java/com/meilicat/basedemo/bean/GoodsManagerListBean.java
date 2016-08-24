package com.meilicat.basedemo.bean;

/**
 * Created by user on 2016/2/29.
 */
public class GoodsManagerListBean {

    private int msg;
    private String msgbox;

    private GoodsDataEntity data;

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public void setData(GoodsDataEntity data) {
        this.data = data;
    }

    public int getMsg() {
        return msg;
    }

    public String getMsgbox() {
        return msgbox;
    }

    public GoodsDataEntity getData() {
        return data;
    }
}
