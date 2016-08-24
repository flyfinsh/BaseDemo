package com.meilicat.basedemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lizhiming on 2016/3/1.
 */
public class GoodsProductSkuListEntity implements Parcelable {
    private int productId;
    private String sku;
    private String colorName;
    private String sizeName;
    private String colorAbbr;
    private String sizeAbbr;
    private double price;

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getColorAbbr() {
        return colorAbbr;
    }

    public void setColorAbbr(String colorAbbr) {
        this.colorAbbr = colorAbbr;
    }

    public String getSizeAbbr() {
        return sizeAbbr;
    }

    public void setSizeAbbr(String sizeAbbr) {
        this.sizeAbbr = sizeAbbr;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getColorName() {
        return colorName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public double getPrice() {
        return price;
    }

    public GoodsProductSkuListEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.productId);
        dest.writeString(this.sku);
        dest.writeString(this.colorName);
        dest.writeString(this.sizeName);
        dest.writeString(this.colorAbbr);
        dest.writeString(this.sizeAbbr);
        dest.writeDouble(this.price);
    }

    protected GoodsProductSkuListEntity(Parcel in) {
        this.productId = in.readInt();
        this.sku = in.readString();
        this.colorName = in.readString();
        this.sizeName = in.readString();
        this.colorAbbr = in.readString();
        this.sizeAbbr = in.readString();
        this.price = in.readDouble();
    }

    public static final Creator<GoodsProductSkuListEntity> CREATOR = new Creator<GoodsProductSkuListEntity>() {
        public GoodsProductSkuListEntity createFromParcel(Parcel source) {
            return new GoodsProductSkuListEntity(source);
        }

        public GoodsProductSkuListEntity[] newArray(int size) {
            return new GoodsProductSkuListEntity[size];
        }
    };
}
