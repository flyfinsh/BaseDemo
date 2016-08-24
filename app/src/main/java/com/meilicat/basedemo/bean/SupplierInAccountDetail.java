package com.meilicat.basedemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cj on 2016/2/24.
 */
public class SupplierInAccountDetail {

    public int msg;
    public String msgbox;


    public DataEntity data;

    public static class DataEntity {
        /**
         * balance : 220.0
         * colorName : 黑色
         * createTime : 2015-12-15 08:31:33
         * moneyDetail : 220.0
         * productNumber : A002DWL019022
         * sizeAbbr : M
         */

        public SupplierEntity supplier;

        public static class SupplierEntity implements Parcelable {
            public double balance;
            public String colorName;
            public String createTime;
            public String tradeTime;
            public double moneyDetail;
            public String productNumber;
            public String sizeAbbr;
            public int qty;
            public double price;
            public String moneyLogId;
            public String productName_cn;
            public int type;


            protected SupplierEntity(Parcel in) {
                balance = in.readDouble();
                colorName = in.readString();
                createTime = in.readString();
                tradeTime = in.readString();
                moneyDetail = in.readDouble();
                productNumber = in.readString();
                sizeAbbr = in.readString();
                qty = in.readInt();
                price = in.readDouble();
                moneyLogId = in.readString();
                productName_cn = in.readString();
                type = in.readInt();
            }

            public static final Creator<SupplierEntity> CREATOR = new Creator<SupplierEntity>() {
                @Override
                public SupplierEntity createFromParcel(Parcel in) {
                    return new SupplierEntity(in);
                }

                @Override
                public SupplierEntity[] newArray(int size) {
                    return new SupplierEntity[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeDouble(balance);
                dest.writeString(colorName);
                dest.writeString(createTime);
                dest.writeString(tradeTime);
                dest.writeDouble(moneyDetail);
                dest.writeString(productNumber);
                dest.writeString(sizeAbbr);
                dest.writeInt(qty);
                dest.writeDouble(price);
                dest.writeString(moneyLogId);
                dest.writeString(productName_cn);
                dest.writeInt(type);
            }
        }
    }
}
