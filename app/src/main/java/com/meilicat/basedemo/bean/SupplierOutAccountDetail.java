package com.meilicat.basedemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cj on 2016/2/24.
 */
public class SupplierOutAccountDetail {

    public int msg;
    public String msgbox;


    /**
     * supplier : {"TradeTime":"2016-02-25 03:37:46","balance":16480,"bankName":"公司银行","bankNumber":"44444444","moneyDetail":2000,"status":1}
     */

    public DataEntity data;

    public static class DataEntity {
        /**
         * TradeTime : 2016-02-25 03:37:46
         * balance : 16480.0
         * bankName : 公司银行
         * bankNumber : 44444444
         * moneyDetail : 2000.0
         * status : 1
         */

        public SupplierEntity supplier;

        public  static class SupplierEntity implements Parcelable{
            public String TradeTime;
            public double balance;
            public String bankName;
            public String bankNumber;
            public double moneyDetail;
            public int status;

            protected SupplierEntity(Parcel in) {
                TradeTime = in.readString();
                balance = in.readDouble();
                bankName = in.readString();
                bankNumber = in.readString();
                moneyDetail = in.readDouble();
                status = in.readInt();
            }

            public  static final Creator<SupplierEntity> CREATOR = new Creator<SupplierEntity>() {
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
                dest.writeString(TradeTime);
                dest.writeDouble(balance);
                dest.writeString(bankName);
                dest.writeString(bankNumber);
                dest.writeDouble(moneyDetail);
                dest.writeInt(status);
            }
        }
    }
}
