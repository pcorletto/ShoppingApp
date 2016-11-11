package com.example.android.shoppingapp3.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hernandez on 3/2/2016.
 */
public class ShoppingItem implements Parcelable {

    // Private member variables

    private String mUPC, mProductName, mCategory;
    private int mPriority;
    private int mQuantity;
    private int mLastQuantity;
    private String mLastDatePurchased;
    private double mItemPrice, mSubtotal;
    private boolean isSelected;

    // Constructors

    public ShoppingItem(){

    }

    public ShoppingItem(String upc, int quantity, int lastQuantity, String lastDatePurchased,
                        String name, int priority, double price, String category,
                        double subtotal){

        this.mUPC = upc;
        this.mQuantity = quantity;
        this.mLastQuantity = lastQuantity;
        this.mLastDatePurchased = lastDatePurchased;
        this.mProductName = name;
        this.mPriority = priority;
        this.mItemPrice = price;
        this.mCategory = category;
        this.mSubtotal = subtotal;
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

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        mPriority = priority;
    }

    public int getLastQuantity() {
        return mLastQuantity;
    }

    public void setLastQuantity(int lastQuantity) {
        mLastQuantity = lastQuantity;
    }

    public String getLastDatePurchased() {
        return mLastDatePurchased;
    }

    public void setLastDatePurchased(String lastDatePurchased) {
        mLastDatePurchased = lastDatePurchased;
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

    public double getSubtotal() {
        return mSubtotal;
    }

    public void setSubtotal(double subtotal) {

        mSubtotal = subtotal;
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
        dest.writeInt(mQuantity);
        dest.writeInt(mLastQuantity);
        dest.writeString(mLastDatePurchased);
        dest.writeString(mProductName);
        dest.writeInt(mPriority);
        dest.writeDouble(mItemPrice);
        dest.writeString(mCategory);
        dest.writeDouble(mSubtotal);

    }

    private ShoppingItem(Parcel in){

        mUPC = in.readString();
        mQuantity = in.readInt();
        mLastQuantity = in.readInt();
        mLastDatePurchased = in.readString();
        mProductName = in.readString();
        mPriority = in.readInt();
        mItemPrice = in.readDouble();
        mCategory = in.readString();
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
