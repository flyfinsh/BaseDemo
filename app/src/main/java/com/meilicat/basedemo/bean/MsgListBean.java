package com.meilicat.basedemo.bean;

import java.util.List;

/**
 * Created by user on 2016/2/26.
 */
public class MsgListBean {

    private String msgbox;
    private int msg;
    /**
     * subject : 美丽猫猴年开市公告
     * announceType : 0
     * announceObject : 0
     * content : &nbsp; &nbsp;&nbsp;&nbsp;美丽猫恭祝大家猴年大发，开市大
     * approveUserId : 148
     * approveTime : 2016-02-24 09:53:27
     * publishTime : 2016-02-24 09:53:02
     * id : 16
     * createUserId : 148
     * createTime : 2016-02-24 09:53:27
     */

    private List<DataEntity> data;

    public void setMsgbox(String msgbox) {
        this.msgbox = msgbox;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public String getMsgbox() {
        return msgbox;
    }

    public int getMsg() {
        return msg;
    }

    public List<DataEntity> getData() {
        return data;
    }

    public class DataEntity {
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
        private String status;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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
