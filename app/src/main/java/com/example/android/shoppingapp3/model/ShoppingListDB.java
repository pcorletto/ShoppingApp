package com.example.android.shoppingapp3.model;

/**
 * Created by hernandez on 3/2/2016.
 */
public class ShoppingListDB {

    public static abstract class NewListItem {

        public static final String UPC_CODE = "upc_code";
        public static final String QUANTITY = "quantity";
        public static final String LAST_QUANTITY = "last_quantity";
        public static final String LAST_DATE_PURCHASED = "last_date_purchased";
        public static final String PRODUCT_NAME = "product_name";
        public static final String PRIORITY = "priority";
        public static final String ITEM_PRICE = "item_price";
        public static final String CATEGORY = "category";
        public static final String SUBTOTAL = "subtotal";
        public static final String IMAGE = "image";

        // Table name

        public static final String TABLE_NAME = "shopping_list";



    }
}