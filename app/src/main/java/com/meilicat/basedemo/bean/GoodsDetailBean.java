package com.meilicat.basedemo.bean;

/**
 * Created by user on 2016/3/1.
 */
public class GoodsDetailBean {

    private int msg;
    private String msgbox;

    private GoodsRowsEntity data;

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public void setData(GoodsRowsEntity data) {
        this.data = data;
    }

    public int getMsg() {
        return msg;
    }

    public String getMsgbox() {
        return msgbox;
    }

    public GoodsRowsEntity getData() {
        return data;
    }

}
