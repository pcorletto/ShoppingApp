package com.example.android.shoppingapp2.model;

/**
 * Created by hernandez on 3/2/2016.
 */
public class ShoppingList {

    public static abstract class NewShoppingItem{

        public static final String UPC_CODE = "upc_code";
        public static final String QUANTITY = "quantity";
        public static final String PRODUCT_NAME = "product_name";
        public static final String CATEGORY = "category";
        public static final String ITEM_PRICE = "item_price";
        public static final String SUBTOTAL = "subtotal";

        // Table name

        public static final String TABLE_NAME = "shopping_list";



    }
}