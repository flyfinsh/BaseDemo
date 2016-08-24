package com.meilicat.basedemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2016/2/25.
 */
public class UserInfoSupplierEntity implements Parcelable {

    /**
     * contractPerson : dsfs
     * name : Richy
     * accountHolder : dsfsd
     * mobilePhoneNum : sdfsdf
     * bankName : sfs
     * bankNumber : 23523
     * supAccount : SZNY2345
     * industryBeltId : 61
     * industryBeltName_cn : 深圳产业带
     * marketName_cn : 南油
     * marketId : 48
     * isSign : 1
     * id : 185
     * abbr : 2345
     * isValid : 1
     * createUserId : 166
     * updateUserId : 166
     * createTime : 2016-02-25 11:26:40
     * updateTime : 2016-02-25 11:27:46
     */

    private String avatarImageURL;
    private String address;
    private String contractPerson;
    private String name;
    private String accountHolder;
    private String mobilePhoneNum;
    private String bankName;
    private String bankNumber;
    private String supAccount;
    private String industryBeltId;
    private String industryBeltName_cn;
    private String marketName_cn;
    private String marketId;
    private int isSign;
    private String id;
    private String abbr;
    private int isValid;
    private String createUserId;
    private String updateUserId;
    private String createTime;
    private String updateTime;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatarImageURL() {
        return avatarImageURL;
    }

    public void setAvatarImageURL(String avatarImageURL) {
        this.avatarImageURL = avatarImageURL;
    }

    public void setContractPerson(String contractPerson) {
        this.contractPerson = contractPerson;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public void setMobilePhoneNum(String mobilePhoneNum) {
        this.mobilePhoneNum = mobilePhoneNum;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public void setSupAccount(String supAccount) {
        this.supAccount = supAccount;
    }

    public void setIndustryBeltId(String industryBeltId) {
        this.industryBeltId = industryBeltId;
    }

    public void setIndustryBeltName_cn(String industryBeltName_cn) {
        this.industryBeltName_cn = industryBeltName_cn;
    }

    public void setMarketName_cn(String marketName_cn) {
        this.marketName_cn = marketName_cn;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public void setIsSign(int isSign) {
        this.isSign = isSign;
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

    public String getContractPerson() {
        return contractPerson;
    }

    public String getName() {
        return name;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public String getMobilePhoneNum() {
        return mobilePhoneNum;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public String getSupAccount() {
        return supAccount;
    }

    public String getIndustryBeltId() {
        return industryBeltId;
    }

    public String getIndustryBeltName_cn() {
        return industryBeltName_cn;
    }

    public String getMarketName_cn() {
        return marketName_cn;
    }

    public String getMarketId() {
        return marketId;
    }

    public int getIsSign() {
        return isSign;
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

    public UserInfoSupplierEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatarImageURL);
        dest.writeString(this.address);
        dest.writeString(this.contractPerson);
        dest.writeString(this.name);
        dest.writeString(this.accountHolder);
        dest.writeString(this.mobilePhoneNum);
        dest.writeString(this.bankName);
        dest.writeString(this.bankNumber);
        dest.writeString(this.supAccount);
        dest.writeString(this.industryBeltId);
        dest.writeString(this.industryBeltName_cn);
        dest.writeString(this.marketName_cn);
        dest.writeString(this.marketId);
        dest.writeInt(this.isSign);
        dest.writeString(this.id);
        dest.writeString(this.abbr);
        dest.writeInt(this.isValid);
        dest.writeString(this.createUserId);
        dest.writeString(this.updateUserId);
        dest.writeString(this.createTime);
        dest.writeString(this.updateTime);
    }

    protected UserInfoSupplierEntity(Parcel in) {
        this.avatarImageURL = in.readString();
        this.address = in.readString();
        this.contractPerson = in.readString();
        this.name = in.readString();
        this.accountHolder = in.readString();
        this.mobilePhoneNum = in.readString();
        this.bankName = in.readString();
        this.bankNumber = in.readString();
        this.supAccount = in.readString();
        this.industryBeltId = in.readString();
        this.industryBeltName_cn = in.readString();
        this.marketName_cn = in.readString();
        this.marketId = in.readString();
        this.isSign = in.readInt();
        this.id = in.readString();
        this.abbr = in.readString();
        this.isValid = in.readInt();
        this.createUserId = in.readString();
        this.updateUserId = in.readString();
        this.createTime = in.readString();
        this.updateTime = in.readString();
    }

    public static final Creator<UserInfoSupplierEntity> CREATOR = new Creator<UserInfoSupplierEntity>() {
        public UserInfoSupplierEntity createFromParcel(Parcel source) {
            return new UserInfoSupplierEntity(source);
        }

        public UserInfoSupplierEntity[] newArray(int size) {
            return new UserInfoSupplierEntity[size];
        }
    };
}
