package com.example.android.shoppingapp3.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hernandez on 12/12/2016.
 */
public class PurchaseDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PURCHASELIST.DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY = "CREATE TABLE " + PurchaseListDB.NewPurchaseItem.TABLE_NAME +
            "(" + PurchaseListDB.NewPurchaseItem.PURCHASE_ID + " INTEGER," +
            PurchaseListDB.NewPurchaseItem.DATE + " TEXT," +
            PurchaseListDB.NewPurchaseItem.STORE_LOCATION + " TEXT," +
            PurchaseListDB.NewPurchaseItem.SUMMARY + " TEXT);";

    // Default Constructor:

    public PurchaseDbHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DATABASE OPERATIONS", "Database created / opened ...");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_QUERY);
        Log.e("DATABASE OPERATIONS", "Table created ...");

    }

    // Insert the item next. Method for inserting the purchase item.

    public void addItem(int id, String date, String store_location,
                        String summary, SQLiteDatabase db){

        // Map key-values

        ContentValues contentValues = new ContentValues();
        contentValues.put(PurchaseListDB.NewPurchaseItem.PURCHASE_ID, id);
        contentValues.put(PurchaseListDB.NewPurchaseItem.DATE, date);
        contentValues.put(PurchaseListDB.NewPurchaseItem.STORE_LOCATION, store_location);
        contentValues.put(PurchaseListDB.NewPurchaseItem.SUMMARY, summary);

        // Save all these into the database

        db.insert(PurchaseListDB.NewPurchaseItem.TABLE_NAME, null, contentValues);

        Log.e("DATABASE OPERATIONS", "One row is inserted ...");

    }

    public Cursor getPurchaseItem(SQLiteDatabase db){

        // The return type of Object is "Cursor"
        Cursor cursor;

        // Create projections, or the needed column names
        String[] projections = {PurchaseListDB.NewPurchaseItem.PURCHASE_ID,
                PurchaseListDB.NewPurchaseItem.DATE,
                PurchaseListDB.NewPurchaseItem.STORE_LOCATION,
                PurchaseListDB.NewPurchaseItem.SUMMARY};

        // We only need the table name and projection parameters. No conditions will be specified,
        // so, we will pass in null for the last five parameters.

        cursor = db.query(PurchaseListDB.NewPurchaseItem.TABLE_NAME, projections, null, null, null, null, null);

        return cursor;

    }

    public Cursor searchPurchaseItems(String searchItem, SQLiteDatabase db){

        // The return type of Object is "Cursor"
        Cursor cursor;

        cursor = db.rawQuery("SELECT * FROM " +
                PurchaseListDB.NewPurchaseItem.TABLE_NAME + " where " + PurchaseListDB.NewPurchaseItem.DATE+ " like '"
                +searchItem+"%'" + " ORDER BY " + PurchaseListDB.NewPurchaseItem.SUMMARY + " COLLATE NOCASE ASC", null);

        return cursor;
    }

    public Cursor sortPurchaseItems(SQLiteDatabase db){

        Cursor cursor;

        // Modified this method so that the results are sorted in alphabetical ascending order,
        // by the word. It worked!!!

        cursor = db.rawQuery("SELECT * FROM " + PurchaseListDB.NewPurchaseItem.TABLE_NAME +
                " ORDER BY " + PurchaseListDB.NewPurchaseItem.DATE + " COLLATE NOCASE ASC", null);

        // Another valid way to sort it, case insensitive, in SQL is as follows:
        // cursor = db.rawQuery("SELECT * FROM " + EntryListDB.NewEntryItem.TABLE_NAME +
        //        " ORDER BY LOWER(" + EntryListDB.NewEntryItem.WORD + ") ASC", null);

        return cursor;

    }

    public void deletePurchaseItem(String purchase_ID, SQLiteDatabase sqLiteDatabase){

        String selection = PurchaseListDB.NewPurchaseItem.PURCHASE_ID + " LIKE ?";

    // Use the primary key, or the Item ID (which is stored and retrieved from a SharedPreferences
    // file), for selecting the item to be deleted from the DB. This will ensure that we will only
    // delete the selected item(s) and not anything else.

        String[] selection_args = {purchase_ID};

        sqLiteDatabase.delete(PurchaseListDB.NewPurchaseItem.TABLE_NAME, selection, selection_args);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
