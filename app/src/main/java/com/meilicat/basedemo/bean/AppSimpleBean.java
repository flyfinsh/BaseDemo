package com.meilicat.basedemo.bean;

/**
 * Created by user on 2016/2/27.
 */
public class AppSimpleBean {

    /**
     * msg : 1
     * msgbox : 请求成功!
     * data :
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
