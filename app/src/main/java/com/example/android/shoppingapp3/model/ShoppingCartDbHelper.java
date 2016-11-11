package com.example.android.shoppingapp3.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hernandez on 3/2/2016.
 */
public class ShoppingCartDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SHOPPINGCART.DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY = "CREATE TABLE " + ShoppingCartDB.NewCartItem.TABLE_NAME +
            "(" + ShoppingCartDB.NewCartItem.UPC_CODE + " TEXT," +
            ShoppingCartDB.NewCartItem.QUANTITY + " INTEGER," +
            ShoppingCartDB.NewCartItem.LAST_QUANTITY + " INTEGER," +
            ShoppingCartDB.NewCartItem.LAST_DATE_PURCHASED + " TEXT," +
            ShoppingCartDB.NewCartItem.PRODUCT_NAME + " TEXT," +
            ShoppingCartDB.NewCartItem.PRIORITY + " INTEGER," +
            ShoppingCartDB.NewCartItem.ITEM_PRICE + " REAL," +
            ShoppingCartDB.NewCartItem.CATEGORY + " TEXT," +
            ShoppingCartDB.NewCartItem.SUBTOTAL + " TEXT);";

    // Default Constructor:

    public ShoppingCartDbHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DATABASE OPERATIONS", "Database created / opened ...");

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_QUERY);
        Log.e("DATABASE OPERATIONS", "Table created ...");

    }


    // Insert the item  next. Method for inserting the shopping item.

    public void addItem(String upc, int quantity, int last_quantity, String last_date_purchased,
                        String productName, int priority, double itemPrice, String category,
                        double subtotal, SQLiteDatabase db){

        // Map key-values

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShoppingCartDB.NewCartItem.UPC_CODE, upc);
        contentValues.put(ShoppingCartDB.NewCartItem.QUANTITY, quantity);
        contentValues.put(ShoppingCartDB.NewCartItem.LAST_QUANTITY, last_quantity);
        contentValues.put(ShoppingCartDB.NewCartItem.LAST_DATE_PURCHASED, last_date_purchased);
        contentValues.put(ShoppingCartDB.NewCartItem.PRODUCT_NAME, productName);
        contentValues.put(ShoppingCartDB.NewCartItem.PRIORITY, priority);
        contentValues.put(ShoppingCartDB.NewCartItem.ITEM_PRICE, itemPrice);
        contentValues.put(ShoppingCartDB.NewCartItem.CATEGORY, category);
        contentValues.put(ShoppingCartDB.NewCartItem.SUBTOTAL, subtotal);

        // Save all these into the database

        db.insert(ShoppingCartDB.NewCartItem.TABLE_NAME, null, contentValues);

        Log.e("DATABASE OPERATIONS", "One row is inserted ...");

    }

    public Cursor getCartItem(SQLiteDatabase db){

        // The return type of Object is "Cursor"
        Cursor cursor;

        // Create projections, or the needed column names
        String[] projections = {ShoppingCartDB.NewCartItem.UPC_CODE,
                ShoppingCartDB.NewCartItem.QUANTITY,
                ShoppingCartDB.NewCartItem.LAST_QUANTITY,
                ShoppingCartDB.NewCartItem.LAST_DATE_PURCHASED,
                ShoppingCartDB.NewCartItem.PRODUCT_NAME,
                ShoppingCartDB.NewCartItem.PRIORITY,
                ShoppingCartDB.NewCartItem.ITEM_PRICE,
                ShoppingCartDB.NewCartItem.CATEGORY,
                ShoppingCartDB.NewCartItem.SUBTOTAL};

        // We only need the table name and projection parameters. No conditions will be specified,
        // so, we will pass in null for the last five parameters.

        cursor = db.query(ShoppingCartDB.NewCartItem.TABLE_NAME, projections, null, null, null, null, null);

        return cursor;

    }

    public Cursor searchCartItems(String searchItem, SQLiteDatabase db){

        // The return type of Object is "Cursor"
        Cursor cursor;

        //cursor = db.rawQuery("SELECT * FROM " + EntryListDB.NewEntryItem.TABLE_NAME +
        //      " WHERE " + EntryListDB.NewEntryItem.WORD + " LIKE? " + searchItem, null);

        cursor = db.rawQuery("SELECT * FROM " +
                ShoppingCartDB.NewCartItem.TABLE_NAME + " where " +ShoppingCartDB.NewCartItem.PRODUCT_NAME+ " like '"
                +searchItem+"%'" + " ORDER BY " + ShoppingCartDB.NewCartItem.PRODUCT_NAME + " COLLATE NOCASE ASC", null);

        return cursor;
    }

    public Cursor sortCartItems(SQLiteDatabase db){

        Cursor cursor;

        // Modified this method so that the results are sorted in alphabetical ascending order,
        // by the word. It worked!!!

        cursor = db.rawQuery("SELECT * FROM " + ShoppingCartDB.NewCartItem.TABLE_NAME +
                " ORDER BY " + ShoppingCartDB.NewCartItem.PRODUCT_NAME + " COLLATE NOCASE ASC", null);

        // Another valid way to sort it, case insensitive, in SQL is as follows:
        // cursor = db.rawQuery("SELECT * FROM " + EntryListDB.NewEntryItem.TABLE_NAME +
        //        " ORDER BY LOWER(" + EntryListDB.NewEntryItem.WORD + ") ASC", null);

        return cursor;

    }

    public void deleteCartItem(String product_name, SQLiteDatabase sqLiteDatabase){

        String selection = ShoppingCartDB.NewCartItem.PRODUCT_NAME + " LIKE ?";

        String[] selection_args = {product_name};

        sqLiteDatabase.delete(ShoppingCartDB.NewCartItem.TABLE_NAME, selection, selection_args);


    }

    public int updateSubtotal(String old_product_name, String quantity, String subtotal, SQLiteDatabase sqLiteDatabase){

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShoppingCartDB.NewCartItem.QUANTITY, quantity);
        contentValues.put(ShoppingCartDB.NewCartItem.SUBTOTAL, subtotal);

        // Provide a condition or selection

        String selection = ShoppingCartDB.NewCartItem.PRODUCT_NAME + " LIKE ?";

        String[] selection_args = {old_product_name};

        int count = sqLiteDatabase.update(ShoppingCartDB.NewCartItem.TABLE_NAME, contentValues,
                selection, selection_args);

        return count;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
