package com.meilicat.basedemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by user on 2016/3/1.
 */
public class GoodsRowsEntity implements Parcelable {
    private int source;
    private int categoryId;
    private String categoryIdStr;
    private int seasonId;
    private String seasonName;
    private String categoryName;
    private String recommendTime;
    private int approveType;
    private int approveUserId;
    private String approveTime;
    private String supplierName;

    private int supplierId;
    private String productNumber;
    private String imageURL_1;
    private int status;
    private double price;

    private int transitCount;
    private int statCount;

    /**
     * 其他详情信息
     */
    private String imageURL_2;
    private String imageURL_3;
    private String imageURL_4;
    private String imageURL_5;
    private String imageURL_6;
    private String refuseReason;

    private String id;
    private String cn;

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryIdStr() {
        return categoryIdStr;
    }

    public void setCategoryIdStr(String categoryIdStr) {
        this.categoryIdStr = categoryIdStr;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getRecommendTime() {
        return recommendTime;
    }

    public void setRecommendTime(String recommendTime) {
        this.recommendTime = recommendTime;
    }

    public int getApproveType() {
        return approveType;
    }

    public void setApproveType(int approveType) {
        this.approveType = approveType;
    }

    public int getApproveUserId() {
        return approveUserId;
    }

    public void setApproveUserId(int approveUserId) {
        this.approveUserId = approveUserId;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * productId : 433
     * sku : SZBHCwaitaoFRY5487C0M
     * colorName : C
     * sizeName : 0M
     * price : 10.0
     */



    private List<GoodsProductSkuListEntity> supplierProductSkuList;

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public void setImageURL_1(String imageURL_1) {
        this.imageURL_1 = imageURL_1;
    }

    public String getImageURL_2() {
        return imageURL_2;
    }

    public void setImageURL_2(String imageURL_2) {
        this.imageURL_2 = imageURL_2;
    }

    public String getImageURL_3() {
        return imageURL_3;
    }

    public void setImageURL_3(String imageURL_3) {
        this.imageURL_3 = imageURL_3;
    }

    public String getImageURL_4() {
        return imageURL_4;
    }

    public void setImageURL_4(String imageURL_4) {
        this.imageURL_4 = imageURL_4;
    }

    public String getImageURL_5() {
        return imageURL_5;
    }

    public void setImageURL_5(String imageURL_5) {
        this.imageURL_5 = imageURL_5;
    }

    public String getImageURL_6() {
        return imageURL_6;
    }

    public void setImageURL_6(String imageURL_6) {
        this.imageURL_6 = imageURL_6;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public int getTransitCount() {
        return transitCount;
    }

    public void setTransitCount(int transitCount) {
        this.transitCount = transitCount;
    }

    public int getStatCount() {
        return statCount;
    }

    public void setStatCount(int statCount) {
        this.statCount = statCount;
    }

    public void setSupplierProductSkuList(List<GoodsProductSkuListEntity> supplierProductSkuList) {
        this.supplierProductSkuList = supplierProductSkuList;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public String getImageURL_1() {
        return imageURL_1;
    }

    public int getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    public String getCn() {
        return cn;
    }

    public List<GoodsProductSkuListEntity> getSupplierProductSkuList() {
        return supplierProductSkuList;
    }

    public GoodsRowsEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.source);
        dest.writeInt(this.categoryId);
        dest.writeString(this.categoryIdStr);
        dest.writeInt(this.seasonId);
        dest.writeString(this.seasonName);
        dest.writeString(this.categoryName);
        dest.writeString(this.recommendTime);
        dest.writeInt(this.approveType);
        dest.writeInt(this.approveUserId);
        dest.writeString(this.approveTime);
        dest.writeString(this.supplierName);
        dest.writeInt(this.supplierId);
        dest.writeString(this.productNumber);
        dest.writeString(this.imageURL_1);
        dest.writeInt(this.status);
        dest.writeDouble(this.price);
        dest.writeInt(this.transitCount);
        dest.writeInt(this.statCount);
        dest.writeString(this.imageURL_2);
        dest.writeString(this.imageURL_3);
        dest.writeString(this.imageURL_4);
        dest.writeString(this.imageURL_5);
        dest.writeString(this.imageURL_6);
        dest.writeString(this.refuseReason);
        dest.writeString(this.id);
        dest.writeString(this.cn);
        dest.writeTypedList(supplierProductSkuList);
    }

    protected GoodsRowsEntity(Parcel in) {
        this.source = in.readInt();
        this.categoryId = in.readInt();
        this.categoryIdStr = in.readString();
        this.seasonId = in.readInt();
        this.seasonName = in.readString();
        this.categoryName = in.readString();
        this.recommendTime = in.readString();
        this.approveType = in.readInt();
        this.approveUserId = in.readInt();
        this.approveTime = in.readString();
        this.supplierName = in.readString();
        this.supplierId = in.readInt();
        this.productNumber = in.readString();
        this.imageURL_1 = in.readString();
        this.status = in.readInt();
        this.price = in.readDouble();
        this.transitCount = in.readInt();
        this.statCount = in.readInt();
        this.imageURL_2 = in.readString();
        this.imageURL_3 = in.readString();
        this.imageURL_4 = in.readString();
        this.imageURL_5 = in.readString();
        this.imageURL_6 = in.readString();
        this.refuseReason = in.readString();
        this.id = in.readString();
        this.cn = in.readString();
        this.supplierProductSkuList = in.createTypedArrayList(GoodsProductSkuListEntity.CREATOR);
    }

    public static final Creator<GoodsRowsEntity> CREATOR = new Creator<GoodsRowsEntity>() {
        public GoodsRowsEntity createFromParcel(Parcel source) {
            return new GoodsRowsEntity(source);
        }

        public GoodsRowsEntity[] newArray(int size) {
            return new GoodsRowsEntity[size];
        }
    };
}
