package com.example.android.shoppingapp2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hernandez on 3/2/2016.
 */
public class ShoppingDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SHOPPINGLIST.DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY = "CREATE TABLE " + ShoppingList.NewShoppingItem.TABLE_NAME +
            "(" + ShoppingList.NewShoppingItem.UPC_CODE + " TEXT," +
            ShoppingList.NewShoppingItem.QUANTITY + " INTEGER," +
            ShoppingList.NewShoppingItem.PRODUCT_NAME + " TEXT," +
            ShoppingList.NewShoppingItem.ITEM_PRICE + " REAL," +
            ShoppingList.NewShoppingItem.CATEGORY + " TEXT," +
            ShoppingList.NewShoppingItem.SUBTOTAL + " TEXT);";

    // Default Constructor:

    public ShoppingDbHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DATABASE OPERATIONS", "Database created / opened ...");

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_QUERY);
        Log.e("DATABASE OPERATIONS", "Table created ...");

    }


    // Insert the item  next. Method for inserting the shopping item.

    public void addItem(String upc, double quantity, String productName,
                        String itemPrice, String category, String subtotal, SQLiteDatabase db){

        // Map key-values

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShoppingList.NewShoppingItem.UPC_CODE, upc);
        contentValues.put(ShoppingList.NewShoppingItem.QUANTITY, quantity);
        contentValues.put(ShoppingList.NewShoppingItem.PRODUCT_NAME, productName);
        contentValues.put(ShoppingList.NewShoppingItem.ITEM_PRICE, itemPrice);
        contentValues.put(ShoppingList.NewShoppingItem.CATEGORY, category);
        contentValues.put(ShoppingList.NewShoppingItem.SUBTOTAL, subtotal);

        // Save all these into the database

        db.insert(ShoppingList.NewShoppingItem.TABLE_NAME, null, contentValues);

        Log.e("DATABASE OPERATIONS", "One row is inserted ...");

    }

    public static Cursor getShoppingItem(SQLiteDatabase db){

        // The return type of Object is "Cursor"
        Cursor cursor;

        // Create projections, or the needed column names
        String[] projections = {ShoppingList.NewShoppingItem.QUANTITY,
                ShoppingList.NewShoppingItem.PRODUCT_NAME,
                ShoppingList.NewShoppingItem.ITEM_PRICE,
                ShoppingList.NewShoppingItem.CATEGORY,
                ShoppingList.NewShoppingItem.SUBTOTAL};

        // We only need the table name and projection parameters. No conditions will be specified,
        // so, we will pass in null for the last five parameters.

        cursor = db.query(ShoppingList.NewShoppingItem.TABLE_NAME, projections, null, null, null, null, null);

        return cursor;

    }

    public void deleteShoppingItem(String product_name, SQLiteDatabase sqLiteDatabase){

        String selection = ShoppingList.NewShoppingItem.PRODUCT_NAME + " LIKE ?";

        String[] selection_args = {product_name};

        sqLiteDatabase.delete(ShoppingList.NewShoppingItem.TABLE_NAME, selection, selection_args);


    }

    public int updateSubtotal(String old_product_name, String quantity, String subtotal, SQLiteDatabase sqLiteDatabase){

        ContentValues contentValues = new ContentValues();
        contentValues.put(ShoppingList.NewShoppingItem.QUANTITY, quantity);
        contentValues.put(ShoppingList.NewShoppingItem.SUBTOTAL, subtotal);

        // Provide a condition or selection

        String selection = ShoppingList.NewShoppingItem.PRODUCT_NAME + " LIKE ?";

        String[] selection_args = {old_product_name};

        int count = sqLiteDatabase.update(ShoppingList.NewShoppingItem.TABLE_NAME, contentValues,
                selection, selection_args);

        return count;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
