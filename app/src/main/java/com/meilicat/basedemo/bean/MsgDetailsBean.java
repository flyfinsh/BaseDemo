package com.meilicat.basedemo.bean;

/**
 * Created by user on 2016/2/26.
 */
public class MsgDetailsBean {

    private String msgbox;

    private DataEntity data;
    private int msg;

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public String getMsgbox() {
        return msgbox;
    }

    public DataEntity getData() {
        return data;
    }

    public int getMsg() {
        return msg;
    }

    public static class DataEntity {
        private String subject;
        private int announceType;
        private int announceObject;
        private String content;
        private String approveUserId;
        private String approveTime;
        private String publishTime;
        private String id;
        private String createUserId;
        private String createTime;

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setAnnounceType(int announceType) {
            this.announceType = announceType;
        }

        public void setAnnounceObject(int announceObject) {
            this.announceObject = announceObject;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setApproveUserId(String approveUserId) {
            this.approveUserId = approveUserId;
        }

        public void setApproveTime(String approveTime) {
            this.approveTime = approveTime;
        }

        public void setPublishTime(String publishTime) {
            this.publishTime = publishTime;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setCreateUserId(String createUserId) {
            this.createUserId = createUserId;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getSubject() {
            return subject;
        }

        public int getAnnounceType() {
            return announceType;
        }

        public int getAnnounceObject() {
            return announceObject;
        }

        public String getContent() {
            return content;
        }

        public String getApproveUserId() {
            return approveUserId;
        }

        public String getApproveTime() {
            return approveTime;
        }

        public String getPublishTime() {
            return publishTime;
        }

        public String getId() {
            return id;
        }

        public String getCreateUserId() {
            return createUserId;
        }

        public String getCreateTime() {
            return createTime;
        }
    }
}
