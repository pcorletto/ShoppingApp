package com.example.android.shoppingapp3.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hernandez on 10/29/2016.
 */
public class ReloadListFromDB {

    // Data structures

    private ShoppingItem mShoppingItem;
    private int mRowNumber;
    private ShoppingList mShoppinglist = new ShoppingList();

    ShoppingListDbHelper shoppingListDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    public ReloadListFromDB(){

    }

    public ShoppingList reloadListFromDB(String selector, String searchItem, Context context){

        // Initialize shopping list item
        mShoppingItem = new ShoppingItem();

        // Initialize shoppingCartDbHelper and SQLiteDB
        shoppingListDbHelper = new ShoppingListDbHelper(context);
        sqLiteDatabase = shoppingListDbHelper.getReadableDatabase();

        if (selector.equals("search")) {

            cursor = shoppingListDbHelper.searchListItems(searchItem, sqLiteDatabase);

        }

        else if (selector.equals("get")){

            cursor = shoppingListDbHelper.getShoppingItem(sqLiteDatabase);
        }
        else if (selector.equals("sortByName")){

            cursor = shoppingListDbHelper.sortListItemsByName(sqLiteDatabase);
        }

        else if (selector.equals("sortByPriority")){

            cursor = shoppingListDbHelper.sortListItemsByPriority(sqLiteDatabase);
        }


        // Initialize the row number

        mRowNumber = 0;

        if(cursor.moveToFirst()){

            do{
                int quantity, lastQuantity;
                double price, subtotal, priority;
                String upc, lastDatePurchased, name, category, image, taxable_string;
                boolean taxable = true;

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
                taxable_string = cursor.getString(10);

                if(taxable_string.equals("false")){

                    taxable = false;

                }

                else if(taxable_string.equals("true")){

                    taxable = true;

                }

                mShoppingItem = new ShoppingItem(upc, quantity, lastQuantity, lastDatePurchased, name,
                        priority, price, category, subtotal, image, taxable);

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

    public int countFoundItems(String searchItem, Context context){

        // Initialize shopping list item
        mShoppingItem = new ShoppingItem();

        // Initialize shoppingListDbHelper and SQLiteDB
        shoppingListDbHelper = new ShoppingListDbHelper(context);
        sqLiteDatabase = shoppingListDbHelper.getReadableDatabase();

        cursor = shoppingListDbHelper.searchListItems(searchItem, sqLiteDatabase);

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
