package com.example.android.shoppingapp3.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hernandez on 10/29/2016.
 */
public class ReloadPurchasesFromDB {

    // Data structures

    private PurchaseItem mPurchaseItem;
    private int mRowNumber;
    private PurchaseList mPurchaseList = new PurchaseList();

    PurchaseDbHelper purchaseDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    public ReloadPurchasesFromDB(){

    }

    public PurchaseList reloadPurchasesFromDB(String selector, String searchItem, Context context){

        // Initialize purchase list item
        mPurchaseItem = new PurchaseItem();

        // Initialize purchaseDbHelper and SQLiteDB
        purchaseDbHelper = new PurchaseDbHelper(context);
        sqLiteDatabase = purchaseDbHelper.getReadableDatabase();

        if (selector.equals("search")) {

            cursor = purchaseDbHelper.searchPurchaseItems(searchItem, sqLiteDatabase);

        }

        else if (selector.equals("get")){

            cursor = purchaseDbHelper.getPurchaseItem(sqLiteDatabase);
        }
        else if (selector.equals("sort")){

            cursor = purchaseDbHelper.sortPurchaseItems(sqLiteDatabase);
        }

        // Initialize the row number

        mRowNumber = 0;

        if(cursor.moveToFirst()){

            do{
                int purchaseID;
                String date, store_location, summary;

                purchaseID = cursor.getInt(0);
                date = cursor.getString(1);
                store_location = cursor.getString(2);
                summary = cursor.getString(3);

                mPurchaseItem = new PurchaseItem(date, summary, store_location, purchaseID);

                mPurchaseList.addPurchaseItem(mPurchaseItem, mRowNumber);

                mRowNumber++;

            }

            while(cursor.moveToNext());
        }

        return mPurchaseList;
    }

    public int getListSize(){

        // This method is used to count the number of items in the reloaded list

        // Initialize the row number

        mRowNumber = 0;

        if(cursor.moveToFirst()){

            do{

                mRowNumber++;

            }

            while(cursor.moveToNext());
        }
        return mRowNumber;
    }
}
