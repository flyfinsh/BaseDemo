package com.meilicat.basedemo.bean.goods;

import java.util.List;

/**
 * Created by user on 2016/3/2.
 */
public class GoodsSizeBean {

    private int msg;
    private String msgbox;

    private List<DataEntity> data;

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public int getMsg() {
        return msg;
    }

    public String getMsgbox() {
        return msgbox;
    }

    public List<DataEntity> getData() {
        return data;
    }

    public class DataEntity {
        private String name;
        private String id;
        private String abbr;
        private int isValid;
        private String createUserId;
        private String updateUserId;
        private String createTime;
        private String updateTime;
        public boolean isSelected;

        public void setName(String name) {
            this.name = name;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setAbbr(String abbr) {
            this.abbr = abbr;
        }

        public void setIsValid(int isValid) {
            this.isValid = isValid;
        }

        public void setCreateUserId(String createUserId) {
            this.createUserId = createUserId;
        }

        public void setUpdateUserId(String updateUserId) {
            this.updateUserId = updateUserId;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getAbbr() {
            return abbr;
        }

        public int getIsValid() {
            return isValid;
        }

        public String getCreateUserId() {
            return createUserId;
        }

        public String getUpdateUserId() {
            return updateUserId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }
    }
}
