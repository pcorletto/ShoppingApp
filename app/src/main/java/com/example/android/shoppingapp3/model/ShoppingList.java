package com.example.android.shoppingapp3.model;

/**
 * Created by hernandez on 3/28/2016.
 */
public class ShoppingList {

    public ShoppingItem[] mShoppingItems = new ShoppingItem[500];

    public void addShoppingItem(ShoppingItem shoppingItem, int rowNumber){

        mShoppingItems[rowNumber] = shoppingItem;

    }

    public ShoppingItem getShoppingItem(int i){
        return mShoppingItems[i];
    }

    public String getShoppingItemName(int i){

        return mShoppingItems[i].getProductName();
    }

    public double getShoppingItemPrice(int i){

        return mShoppingItems[i].getItemPrice();
    }


}
