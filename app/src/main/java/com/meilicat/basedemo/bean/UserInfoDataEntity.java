package com.meilicat.basedemo.bean;

/**
 * Created by user on 2016/2/25.
 */
public class UserInfoDataEntity {
    public UserInfoDataEntity() {
    }

    private int announceCount;

    public int getAnnounceCount() {
        return announceCount;
    }

    public void setAnnounceCount(int announceCount) {
        this.announceCount = announceCount;
    }

    private UserInfoSupplierEntity supplier;
    public void setSupplier(UserInfoSupplierEntity supplier) {
        this.supplier = supplier;
    }

    public UserInfoSupplierEntity getSupplier() {
        return supplier;
    }
}
