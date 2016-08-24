package com.meilicat.basedemo.bean;

/**
 * Created by user on 2016/3/1.
 */
public class UploadImageBean {


    /**
     * msg : 1
     * msgbox : 请求成功!
     * data : http://h.meilicat.com/upload/supplierProduct/2016-03-01/2016030119065920160301_070637.jpg
     */

    private int msg;
    private String msgbox;
    private String data;

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getMsg() {
        return msg;
    }

    public String getMsgbox() {
        return msgbox;
    }

    public String getData() {
        return data;
    }
}
