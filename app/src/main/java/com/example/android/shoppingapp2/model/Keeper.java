package com.example.android.shoppingapp2.model;

/**
 * Created by hernandez on 3/28/2016.
 */
public class Keeper {

    public ShoppingItem[] mShoppingItems = new ShoppingItem[100];

    public void addShoppingItem(ShoppingItem shoppingItem, int rowNumber){

        mShoppingItems[rowNumber] = shoppingItem;

    }

    public String getShoppingItem(int i){
        return mShoppingItems[i].getProductName() +
               " " + mShoppingItems[i].getItemPrice()+ "\n\n";
    }

}
