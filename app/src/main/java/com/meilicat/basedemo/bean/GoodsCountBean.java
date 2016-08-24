package com.meilicat.basedemo.bean;

/**
 * Created by user on 2016/2/29.
 */
public class GoodsCountBean {


    /**
     * msg : 1
     * msgbox : 请求成功!
     * data : {"upOnlineCount":0,"downOnlineCount":0,"waitOnlineCount":0}
     */

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
        private int upOnlineCount;
        private int downOnlineCount;
        private int waitOnlineCount;

        public void setUpOnlineCount(int upOnlineCount) {
            this.upOnlineCount = upOnlineCount;
        }

        public void setDownOnlineCount(int downOnlineCount) {
            this.downOnlineCount = downOnlineCount;
        }

        public void setWaitOnlineCount(int waitOnlineCount) {
            this.waitOnlineCount = waitOnlineCount;
        }

        public int getUpOnlineCount() {
            return upOnlineCount;
        }

        public int getDownOnlineCount() {
            return downOnlineCount;
        }

        public int getWaitOnlineCount() {
            return waitOnlineCount;
        }
    }
}
