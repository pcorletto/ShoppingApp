package com.example.android.shoppingapp3.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hernandez on 12/12/2016.
 */
public class PurchaseItem implements Parcelable {

    // Private member variables

    private String mDate;
    private String mSummary;
    private String mStoreLocation;
    private int mPurchaseID;
    private boolean isSelected;

    // Constructors

    public PurchaseItem(){

    }

    public PurchaseItem(String date, String summary, String storeLocation, int purchaseID){

        this.mDate = date;
        this.mSummary = summary;
        this.mStoreLocation = storeLocation;
        this.mPurchaseID = purchaseID;
        this.isSelected = false;

    }

    // Accessor and mutator methods


    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getStoreLocation() {
        return mStoreLocation;
    }

    public void setStoreLocation(String storeLocation) {
        mStoreLocation = storeLocation;
    }

    public int getPurchaseID() {
        return mPurchaseID;
    }

    public void setPurchaseID(int purchaseID) {
        mPurchaseID = purchaseID;
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

        dest.writeString(mDate);
        dest.writeString(mSummary);
        dest.writeString(mStoreLocation);
        dest.writeInt(mPurchaseID);

    }

    private PurchaseItem(Parcel in){

        mDate = in.readString();
        mSummary = in.readString();
        mStoreLocation = in.readString();
        mPurchaseID = in.readInt();

    }

    public static final Creator<PurchaseItem>CREATOR = new Creator<PurchaseItem>() {

        @Override
        public PurchaseItem createFromParcel(Parcel source) {
            return new PurchaseItem(source);
        }

        @Override
        public PurchaseItem[] newArray(int size) {
            return new PurchaseItem[size];
        }
    };



}
