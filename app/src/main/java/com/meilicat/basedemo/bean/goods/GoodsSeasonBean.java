package com.meilicat.basedemo.bean.goods;

import java.util.List;

/**
 * Created by user on 2016/3/2.
 */
public class GoodsSeasonBean {

    private int msg;
    private String msgbox;

    private List<GoodsSeasonData> data;

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public void setData(List<GoodsSeasonData> data) {
        this.data = data;
    }

    public int getMsg() {
        return msg;
    }

    public String getMsgbox() {
        return msgbox;
    }

    public List<GoodsSeasonData> getData() {
        return data;
    }

}
