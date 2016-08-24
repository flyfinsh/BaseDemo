package com.meilicat.basedemo.bean.goods;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/3/2.
 */
public class GoodsCategoryData implements Parcelable {
    private String id;
    private String cn;
    private String parentCategoryId;
    private String abbr;
    private String en;
    private int isValid;

    private ArrayList<GoodsCategoryData> children;

    public void setId(String id) {
        this.id = id;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public void setChildren(ArrayList<GoodsCategoryData> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public String getCn() {
        return cn;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public String getAbbr() {
        return abbr;
    }

    public String getEn() {
        return en;
    }

    public int getIsValid() {
        return isValid;
    }

    public ArrayList<GoodsCategoryData> getChildren() {
        return children;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.cn);
        dest.writeString(this.parentCategoryId);
        dest.writeString(this.abbr);
        dest.writeString(this.en);
        dest.writeInt(this.isValid);
        dest.writeTypedList(this.children);
    }

    public GoodsCategoryData() {
    }

    protected GoodsCategoryData(Parcel in) {
        this.id = in.readString();
        this.cn = in.readString();
        this.parentCategoryId = in.readString();
        this.abbr = in.readString();
        this.en = in.readString();
        this.isValid = in.readInt();
        this.children = in.createTypedArrayList(GoodsCategoryData.CREATOR);
    }

    public static final Parcelable.Creator<GoodsCategoryData> CREATOR = new Parcelable.Creator<GoodsCategoryData>() {
        public GoodsCategoryData createFromParcel(Parcel source) {
            return new GoodsCategoryData(source);
        }

        public GoodsCategoryData[] newArray(int size) {
            return new GoodsCategoryData[size];
        }
    };
}
