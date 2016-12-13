package com.example.android.shoppingapp3.model;

/**
 * Created by hernandez on 12/12/2016.
 */
public class PurchaseList {

    // The class PurchaseList is only going to hold the last 1,000 purchases
    // stored on the PurchaseListDB, which is an
    // SQLite database of purchases you store. However, PurchaseList is an array of PurchaseItem
    // objects that only holds the 1,000 purchases.

    public PurchaseItem[] mPurchaseItem = new PurchaseItem[1000];

    public void addPurchaseItem(PurchaseItem purchaseItem, int rowNumber){

        mPurchaseItem[rowNumber] = purchaseItem;

    }

    public String getPurchaseItem(int i){
        return mPurchaseItem[i].getDate() +
                "\n\n" + mPurchaseItem[i].getStoreLocation() +
                "\n\n" + mPurchaseItem[i].getSummary();
    }

}
