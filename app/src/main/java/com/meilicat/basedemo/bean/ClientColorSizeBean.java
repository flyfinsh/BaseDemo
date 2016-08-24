package com.meilicat.basedemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lizhiming on 2016/3/10.
 */
public class ClientColorSizeBean implements Parcelable {
    public String colorName;
    public String sizeName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.colorName);
        dest.writeString(this.sizeName);
    }

    public ClientColorSizeBean() {
    }

    protected ClientColorSizeBean(Parcel in) {
        this.colorName = in.readString();
        this.sizeName = in.readString();
    }

    public static final Parcelable.Creator<ClientColorSizeBean> CREATOR = new Parcelable.Creator<ClientColorSizeBean>() {
        public ClientColorSizeBean createFromParcel(Parcel source) {
            return new ClientColorSizeBean(source);
        }

        public ClientColorSizeBean[] newArray(int size) {
            return new ClientColorSizeBean[size];
        }
    };
}
