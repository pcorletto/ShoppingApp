package com.example.android.shoppingapp2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hernandez on 3/2/2016.
 */
public class ShoppingItem implements Parcelable {

    // Private member variables

    private String mUPC, mProductName, mCategory;
    private int mQuantity;
    private double mItemPrice, mRefreshedPrice, mSubtotal;
    private boolean isSelected;

    // Constructors

    public ShoppingItem(){

    }

    public ShoppingItem(int quantity, String name, double price, String category){

        this.mQuantity = quantity;
        this.mProductName = name;
        this.mItemPrice = price;
        this.mCategory = category;
        this.isSelected = false;

    }

    // Accessor and mutator methods

    public String getUPC() {
        return mUPC;
    }

    public void setUPC(String UPC) {
        mUPC = UPC;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        mProductName = productName;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public double getItemPrice() {
        return mItemPrice;
    }

    public void setItemPrice(double itemPrice) {
        mItemPrice = itemPrice;
    }

    public double getRefreshedPrice() {
        return mRefreshedPrice;
    }

    public void setRefreshedPrice(double refreshedPrice) {
        mRefreshedPrice = refreshedPrice;
    }

    public double getSubtotal() {
        return mSubtotal;
    }

    public void setSubtotal() {
        mSubtotal = this.getQuantity()*this.getItemPrice();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mUPC);
        dest.writeString(mProductName);
        dest.writeString(mCategory);
        dest.writeInt(mQuantity);
        dest.writeDouble(mItemPrice);
        dest.writeDouble(mRefreshedPrice);
        dest.writeDouble(mSubtotal);

    }

    private ShoppingItem(Parcel in){

        mUPC = in.readString();
        mProductName = in.readString();
        mCategory = in.readString();
        mQuantity = in.readInt();
        mItemPrice = in.readDouble();
        mRefreshedPrice = in.readDouble();
        mSubtotal = in.readDouble();

    }

    public static final Creator<ShoppingItem>CREATOR = new Creator<ShoppingItem>() {
        @Override
        public ShoppingItem createFromParcel(Parcel source) {
            return new ShoppingItem(source);
        }


        @Override
        public ShoppingItem[] newArray(int size) {
            return new ShoppingItem[size];
        }
    };
}
