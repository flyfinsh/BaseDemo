package com.meilicat.basedemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by cj on 2016/2/24.
 */
public class SupplierMoneyDetail {


    public DataEntity data;


    public int msg;
    public String msgbox;

    public static class DataEntity {


        public SupplierCountMoneyEntity supplierCountMoney;

        public List<SupplierListEntity> supplierList;

        public static class SupplierCountMoneyEntity {
            public String countInMoney;
            public String countOutMoney;
        }

        public static class SupplierListEntity implements Parcelable{
            public String TradeTime;
            public String colorName;
            public String createTime1;
            public double moneyDetail;
            public String moneyLogId;
            public String productName_cn;
            public String productNumber;
            public String sizeAbbr;
            public int type;
            public String money;

            protected SupplierListEntity(Parcel in) {
                TradeTime = in.readString();
                colorName = in.readString();
                createTime1 = in.readString();
                moneyDetail = in.readDouble();
                moneyLogId = in.readString();
                productName_cn = in.readString();
                productNumber = in.readString();
                sizeAbbr = in.readString();
                type = in.readInt();
                money = in.readString();
            }

            public static final Creator<SupplierListEntity> CREATOR = new Creator<SupplierListEntity>() {
                @Override
                public SupplierListEntity createFromParcel(Parcel in) {
                    return new SupplierListEntity(in);
                }

                @Override
                public SupplierListEntity[] newArray(int size) {
                    return new SupplierListEntity[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(TradeTime);
                dest.writeString(colorName);
                dest.writeString(createTime1);
                dest.writeDouble(moneyDetail);
                dest.writeString(moneyLogId);
                dest.writeString(productName_cn);
                dest.writeString(productNumber);
                dest.writeString(sizeAbbr);
                dest.writeInt(type);
                dest.writeString(money);
            }
        }
    }
}
