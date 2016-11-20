package com.example.android.shoppingapp3.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hernandez on 10/29/2016.
 */
public class ReloadCartFromDB {

    // Data structures

    private ShoppingItem mShoppingItem;
    private int mRowNumber;
    private ShoppingList mShoppinglist = new ShoppingList();

    ShoppingCartDbHelper shoppingCartDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    public ReloadCartFromDB(){

    }

    public ShoppingList reloadCartFromDB(String selector, String searchItem, Context context){

        // Initialize shopping list item
        mShoppingItem = new ShoppingItem();

        // Initialize shoppingCartDbHelper and SQLiteDB
        shoppingCartDbHelper = new ShoppingCartDbHelper(context);
        sqLiteDatabase = shoppingCartDbHelper.getReadableDatabase();

        if (selector.equals("search")) {

            cursor = shoppingCartDbHelper.searchCartItems(searchItem, sqLiteDatabase);

        }

        else if (selector.equals("get")){

            cursor = shoppingCartDbHelper.getCartItem(sqLiteDatabase);
        }
        else if (selector.equals("sort")){

            cursor = shoppingCartDbHelper.sortCartItems(sqLiteDatabase);
        }

        // Initialize the row number

        mRowNumber = 0;

        if(cursor.moveToFirst()){

            do{
                int quantity, lastQuantity;
                double price, priority, subtotal;
                String upc, lastDatePurchased, name, category, image;

                upc = cursor.getString(0);
                quantity = cursor.getInt(1);
                lastQuantity = cursor.getInt(2);
                lastDatePurchased = cursor.getString(3);
                name = cursor.getString(4);
                priority = cursor.getDouble(5);
                price = cursor.getDouble(6);
                category = cursor.getString(7);
                subtotal = cursor.getDouble(8);
                image = cursor.getString(9);

                mShoppingItem = new ShoppingItem(upc, quantity, lastQuantity, lastDatePurchased, name, priority,
                        price, category, subtotal, image);

                mShoppinglist.addShoppingItem(mShoppingItem, mRowNumber);

                mRowNumber++;

            }

            while(cursor.moveToNext());
        }

        return mShoppinglist;
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
