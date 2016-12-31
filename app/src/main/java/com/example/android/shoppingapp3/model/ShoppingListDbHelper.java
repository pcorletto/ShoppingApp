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
public class ShoppingListDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SHOPPINGLIST.DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY = "CREATE TABLE " + ShoppingListDB.NewListItem.TABLE_NAME +
            "(" + ShoppingListDB.NewListItem.UPC_CODE + " TEXT," +
            ShoppingListDB.NewListItem.QUANTITY + " INTEGER," +
            ShoppingListDB.NewListItem.LAST_QUANTITY + " INTEGER," +
            ShoppingListDB.NewListItem.LAST_DATE_PURCHASED + " TEXT," +
            ShoppingListDB.NewListItem.PRODUCT_NAME + " TEXT," +
            ShoppingListDB.NewListItem.PRIORITY + " INTEGER," +
            ShoppingListDB.NewListItem.ITEM_PRICE + " REAL," +
            ShoppingListDB.NewListItem.CATEGORY + " TEXT," +
            ShoppingListDB.NewListItem.SUBTOTAL + " TEXT," +
            ShoppingListDB.NewListItem.IMAGE + " TEXT," +
            ShoppingListDB.NewListItem.TAXABLE + " TEXT);";

    // Default Constructor:

    public ShoppingListDbHelper(Context context){

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
                        String productName, double priority, double itemPrice, String category,
                        double subtotal, String image, String taxable, SQLiteDatabase db){

        // Map key-values

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShoppingListDB.NewListItem.UPC_CODE, upc);
        contentValues.put(ShoppingListDB.NewListItem.QUANTITY, quantity);
        contentValues.put(ShoppingListDB.NewListItem.LAST_QUANTITY, last_quantity);
        contentValues.put(ShoppingListDB.NewListItem.LAST_DATE_PURCHASED, last_date_purchased);
        contentValues.put(ShoppingListDB.NewListItem.PRODUCT_NAME, productName);
        contentValues.put(ShoppingListDB.NewListItem.PRIORITY, priority);
        contentValues.put(ShoppingListDB.NewListItem.ITEM_PRICE, itemPrice);
        contentValues.put(ShoppingListDB.NewListItem.CATEGORY, category);
        contentValues.put(ShoppingListDB.NewListItem.SUBTOTAL, subtotal);
        contentValues.put(ShoppingListDB.NewListItem.IMAGE, image);
        contentValues.put(ShoppingListDB.NewListItem.TAXABLE, taxable);

        // Save all these into the database

        db.insert(ShoppingListDB.NewListItem.TABLE_NAME, null, contentValues);

        Log.e("DATABASE OPERATIONS", "One row is inserted ...");

    }

    public Cursor getShoppingItem(SQLiteDatabase db){

        // The return type of Object is "Cursor"
        Cursor cursor;

        // Create projections, or the needed column names
        String[] projections = {ShoppingListDB.NewListItem.UPC_CODE,
                ShoppingListDB.NewListItem.QUANTITY,
                ShoppingListDB.NewListItem.LAST_QUANTITY,
                ShoppingListDB.NewListItem.LAST_DATE_PURCHASED,
                ShoppingListDB.NewListItem.PRODUCT_NAME,
                ShoppingListDB.NewListItem.PRIORITY,
                ShoppingListDB.NewListItem.ITEM_PRICE,
                ShoppingListDB.NewListItem.CATEGORY,
                ShoppingListDB.NewListItem.SUBTOTAL,
                ShoppingListDB.NewListItem.IMAGE,
                ShoppingListDB.NewListItem.TAXABLE};

        // We only need the table name and projection parameters. No conditions will be specified,
        // so, we will pass in null for the last five parameters.

        cursor = db.query(ShoppingListDB.NewListItem.TABLE_NAME, projections, null, null, null, null, null);

        return cursor;

    }


    public Cursor searchListItems(String searchItem, SQLiteDatabase db){

        // The return type of Object is "Cursor"
        Cursor cursor;

        //cursor = db.rawQuery("SELECT * FROM " + EntryListDB.NewEntryItem.TABLE_NAME +
        //      " WHERE " + EntryListDB.NewEntryItem.WORD + " LIKE? " + searchItem, null);

        cursor = db.rawQuery("SELECT * FROM " +
                ShoppingListDB.NewListItem.TABLE_NAME + " where " +ShoppingListDB.NewListItem.PRODUCT_NAME+ " like '"
                +searchItem+"%'" + " ORDER BY " + ShoppingListDB.NewListItem.PRODUCT_NAME + " COLLATE NOCASE ASC", null);

        return cursor;
    }

    public Cursor sortListItemsByName(SQLiteDatabase db){

        Cursor cursor;

        // Modified this method so that the results are sorted in alphabetical ascending order,
        // by the word. It worked!!!

        cursor = db.rawQuery("SELECT * FROM " + ShoppingListDB.NewListItem.TABLE_NAME +
                " ORDER BY " + ShoppingListDB.NewListItem.PRODUCT_NAME + " COLLATE NOCASE ASC", null);

        // Another valid way to sort it, case insensitive, in SQL is as follows:
        // cursor = db.rawQuery("SELECT * FROM " + EntryListDB.NewEntryItem.TABLE_NAME +
        //        " ORDER BY LOWER(" + EntryListDB.NewEntryItem.WORD + ") ASC", null);

        return cursor;

    }

    public Cursor sortListItemsByPriority(SQLiteDatabase db){

        Cursor cursor;

        cursor = db.rawQuery("SELECT * FROM " + ShoppingListDB.NewListItem.TABLE_NAME +
                " ORDER BY " + ShoppingListDB.NewListItem.PRIORITY + " DESC", null);

        return cursor;

    }


    public void deleteShoppingItem(String product_name, SQLiteDatabase sqLiteDatabase){

        String selection = ShoppingListDB.NewListItem.PRODUCT_NAME + " LIKE ?";

        String[] selection_args = {product_name};

        sqLiteDatabase.delete(ShoppingListDB.NewListItem.TABLE_NAME, selection, selection_args);


    }

    public void updateSubtotal(String product_name, String quantity, String subtotal, SQLiteDatabase sqLiteDatabase){

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShoppingListDB.NewListItem.QUANTITY, quantity);
        contentValues.put(ShoppingListDB.NewListItem.SUBTOTAL, subtotal);

        // Provide a condition or selection

        String selection = ShoppingListDB.NewListItem.PRODUCT_NAME + " LIKE ?";

        String[] selection_args = {product_name};

        sqLiteDatabase.update(ShoppingListDB.NewListItem.TABLE_NAME, contentValues,
                selection, selection_args);

    }

    public void updateLastDatePurchased(String product_name, String lastDatePurchased, SQLiteDatabase sqLiteDatabase){

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShoppingListDB.NewListItem.LAST_DATE_PURCHASED, lastDatePurchased);

        // Provide a condition or selection

        String selection = ShoppingListDB.NewListItem.PRODUCT_NAME + " LIKE ?";

        String[] selection_args = {product_name};

        sqLiteDatabase.update(ShoppingListDB.NewListItem.TABLE_NAME, contentValues,
                selection, selection_args);

    }

    public void updateLastQuantityPurchased(String product_name, int lastQuantityPurchased, SQLiteDatabase sqLiteDatabase){

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShoppingListDB.NewListItem.LAST_QUANTITY, lastQuantityPurchased);

        // Provide a condition or selection

        String selection = ShoppingListDB.NewListItem.PRODUCT_NAME + " LIKE ?";

        String[] selection_args = {product_name};

        sqLiteDatabase.update(ShoppingListDB.NewListItem.TABLE_NAME, contentValues,
                selection, selection_args);

    }

    public void updateShoppingItem(String product_name, String mName, String mCategory, double mPrice, int mQuantity,
                                   double mPriority, boolean taxable, int mLastQuantityPurchased,
                                   String mLastDatePurchased, SQLiteDatabase sqLiteDatabase){

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShoppingListDB.NewListItem.PRODUCT_NAME, mName);
        contentValues.put(ShoppingListDB.NewListItem.CATEGORY, mCategory);
        contentValues.put(ShoppingListDB.NewListItem.ITEM_PRICE, mPrice);
        contentValues.put(ShoppingListDB.NewListItem.QUANTITY, mQuantity);
        contentValues.put(ShoppingListDB.NewListItem.PRIORITY, mPriority);
        contentValues.put(ShoppingListDB.NewListItem.TAXABLE, taxable);
        contentValues.put(ShoppingListDB.NewListItem.LAST_QUANTITY, mLastQuantityPurchased);
        contentValues.put(ShoppingListDB.NewListItem.LAST_DATE_PURCHASED, mLastDatePurchased);

        // Provide a condition or selection

        String selection = ShoppingListDB.NewListItem.PRODUCT_NAME + " LIKE ?";

        String[] selection_args = {product_name};

        sqLiteDatabase.update(ShoppingListDB.NewListItem.TABLE_NAME, contentValues,
                selection, selection_args);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
