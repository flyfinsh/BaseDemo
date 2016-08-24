package com.meilicat.basedemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2016/2/19.
 */
public class NormSelItemData implements Parcelable {

    public String filterName;

    public String colorName;
    public String colorAbbrName;

    public String sizeName;
    public String sizeAbbrName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filterName);
        dest.writeString(this.colorName);
        dest.writeString(this.colorAbbrName);
        dest.writeString(this.sizeName);
        dest.writeString(this.sizeAbbrName);
    }

    public NormSelItemData() {
    }

    protected NormSelItemData(Parcel in) {
        this.filterName = in.readString();
        this.colorName = in.readString();
        this.colorAbbrName = in.readString();
        this.sizeName = in.readString();
        this.sizeAbbrName = in.readString();
    }

    public static final Parcelable.Creator<NormSelItemData> CREATOR = new Parcelable.Creator<NormSelItemData>() {
        public NormSelItemData createFromParcel(Parcel source) {
            return new NormSelItemData(source);
        }

        public NormSelItemData[] newArray(int size) {
            return new NormSelItemData[size];
        }
    };
}
