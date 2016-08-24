package com.meilicat.basedemo.bean.goods;

import java.util.List;

/**
 * Created by lizhiming on 2016/3/2.
 */
public class GoodsCategoryBean {

    private int msg;
    private String msgbox;

    private List<GoodsCategoryData> data;

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public void setData(List<GoodsCategoryData> data) {
        this.data = data;
    }

    public int getMsg() {
        return msg;
    }

    public String getMsgbox() {
        return msgbox;
    }

    public List<GoodsCategoryData> getData() {
        return data;
    }

}
