package com.example.android.shoppingapp3.model;

/**
 * Created by hernandez on 12/12/2016.
 */
public class PurchaseListDB {

    // In the class PurchaseListDB a user can store as many purchases as
    // he or she would like. However, PurchaseList only holds 1,000 purchases

    public static abstract class NewPurchaseItem{

        // PURCHASE_ID is the primary key. If the item does not have a unique identifier, we must use an Item ID.
        // Later, use the “ROW_NUMBER” incrementer to assign a value to this Item ID or primary key.
        // This “ROW_NUMBER” needs to be stored and retrieved from a SharedPreferences file.

        public static final String PURCHASE_ID = "purchase_id";
        public static final String DATE = "date";
        public static final String SUMMARY = "summary";
        public static final String STORE_LOCATION = "store_location";

        // Table name

        public static final String TABLE_NAME = "purchase_list";

    }

}
