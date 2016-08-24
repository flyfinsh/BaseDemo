package com.meilicat.basedemo.bean;

/**
 * Created by user on 2016/2/25.
 */
public class UserInfoBean {

    private int msg;
    private String msgbox;

    private UserInfoDataEntity data;

    public UserInfoBean() {
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public void setData(UserInfoDataEntity data) {
        this.data = data;
    }

    public int getMsg() {
        return msg;
    }

    public String getMsgbox() {
        return msgbox;
    }

    public UserInfoDataEntity getData() {
        return data;
    }

}
