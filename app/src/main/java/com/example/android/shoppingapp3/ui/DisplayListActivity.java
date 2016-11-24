package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.adapters.ExpandableListAdapter;
import com.example.android.shoppingapp3.model.ReloadCartFromDB;
import com.example.android.shoppingapp3.model.ReloadListFromDB;
import com.example.android.shoppingapp3.model.ShoppingCartDbHelper;
import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.model.ShoppingList;
import com.example.android.shoppingapp3.model.ShoppingListDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayListActivity extends ActionBarActivity {

    // Data structures

    private ShoppingList mShoppingList = new ShoppingList();

    private int mRowNumber;

    ReloadListFromDB reloadedList = new ReloadListFromDB();

    ReloadCartFromDB reloadedCart = new ReloadCartFromDB();


    // Added these for ExpandableListView extension...

    ExpandableListAdapter listAdapter;
    public static ExpandableListView expListView;
    List<ShoppingItem> listDataHeader;
    HashMap<ShoppingItem, List<ShoppingItem>> listDataChild;

    private Toolbar toolbar;

    ShoppingListDbHelper mShoppingListDbHelper;
    ShoppingCartDbHelper mShoppingCartDbHelper;
    SQLiteDatabase sqLiteDatabase;

    private String mUPC, mLastDatePurchased, mName, mPrice, mCategory, mImage;
    private boolean mTaxable;
    int mQuantity, mLastQuantity;
    double mPriceValue, mSubtotal, mPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);

        // Get the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_hamburger);
        getSupportActionBar().setTitle("List");
        toolbar.setSubtitle("Shopping");

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPrefs.getString(
                getString(R.string.pref_language_key),
                getString(R.string.pref_language_english));

        //Toast.makeText(this, "Language selected: " + language, Toast.LENGTH_LONG).show();

        // Get the expandable list view
        expListView = (ExpandableListView) findViewById(android.R.id.list);

        listDataChild = new HashMap<ShoppingItem, List<ShoppingItem>>();

        // Preparing list data

        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        placeFAB();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_list, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {

            //noinspection SimplifiableIfStatement
            case R.id.action_settings:

            {
                Intent intent = new Intent(DisplayListActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            }
            case R.id.action_search:
            {

                Toast.makeText(DisplayListActivity.this, "Search button pressed!", Toast.LENGTH_LONG).show();
                return true;

            }

            case R.id.action_delete:
            {

                // Initialize the shoppingListDBHelper object

                mShoppingListDbHelper = new ShoppingListDbHelper(getApplicationContext());

                // Initialize the SQLiteDatabase object

                sqLiteDatabase = mShoppingListDbHelper.getReadableDatabase();


                for(int i=0; i<listDataHeader.size(); i++) {

                    if(listDataHeader.get(i).isSelected()){

                        // For SQLiteDatabase: Delete this item here, if checked.

                        String item_for_DB_deletion = listDataHeader.get(i).getProductName() + "";

                        // Delete the shopping item from the SQLite database

                        mShoppingListDbHelper.deleteShoppingItem(item_for_DB_deletion, sqLiteDatabase);

                        finish();

                    }

                }

                return true;

            }

            case R.id.action_cart:

            {

                // For SQLiteDatabase: insert the item into ShoppingCartDB, if checked.

                // Initialize the shoppingCartDBHelper object

                mShoppingCartDbHelper = new ShoppingCartDbHelper(getApplicationContext());

                // Initialize the SQLiteDatabase object

                sqLiteDatabase = mShoppingCartDbHelper.getWritableDatabase();


                for (int i = 0; i < listDataHeader.size(); i++) {

                    if (listDataHeader.get(i).isSelected()) {

                        mUPC = listDataHeader.get(i).getUPC();
                        mQuantity = listDataHeader.get(i).getQuantity();
                        mLastQuantity = listDataHeader.get(i).getLastQuantity();
                        mName = listDataHeader.get(i).getProductName();
                        mPriority = listDataHeader.get(i).getPriority();
                        mPriceValue = listDataHeader.get(i).getItemPrice();
                        mCategory = listDataHeader.get(i).getCategory();
                        mSubtotal = listDataHeader.get(i).getSubtotal();
                        mLastDatePurchased = listDataHeader.get(i).getLastDatePurchased();
                        mImage = listDataHeader.get(i).getImage();
                        mTaxable = listDataHeader.get(i).isTaxable();

                        String taxable_string;

                        if (mTaxable) {
                            taxable_string = "true";
                        } else {
                            taxable_string = "false";
                        }

                        mShoppingCartDbHelper.getCartItem(sqLiteDatabase);


                        // Insert the shopping item into the Shopping Cart SQLite database

                        mShoppingCartDbHelper.addItem(mUPC, mQuantity, mLastQuantity, mLastDatePurchased, mName,
                                mPriority, mPriceValue, mCategory, mSubtotal, mImage, taxable_string, sqLiteDatabase);

                    }

                }

                Intent intent = new Intent(DisplayListActivity.this, DisplayCartActivity.class);

                startActivity(intent);

                return true;

            }

            default:

                return super.onOptionsItemSelected(item);

        }

    }

    private void prepareListData(){


        listDataHeader = new ArrayList<ShoppingItem>();

        listDataChild = new HashMap<ShoppingItem, List<ShoppingItem>>();

        // Get the list of shopping items from the database

        // Reload the list from the SQLite Database. Since we are not searching for anything
        // we will make searchItem just be blank.

        String searchItem = "";

        mShoppingList = reloadedList.reloadListFromDB("get", searchItem, getApplicationContext());

        mRowNumber = reloadedList.getListSize();


        for(int i = 0; i<mRowNumber; i++){

            listDataHeader.add(mShoppingList.getShoppingItem(i));

        }

        // Loop through the array of shopping items

        // Adding child data:

        for(int i=0; i<mRowNumber; i++) {

            List<ShoppingItem> childrenItems = new ArrayList<ShoppingItem>();

            childrenItems.add(mShoppingList.getShoppingItem(i));

            listDataChild.put(listDataHeader.get(i), childrenItems);

        }

    }



    public void placeFAB(){

        FloatingActionButton myFAB = (FloatingActionButton) findViewById(R.id.myFAB);

        myFAB.setOnClickListener(new View.OnClickListener() {

                                     @Override
                                     public void onClick(View v) {

                                         Intent intent = new Intent(DisplayListActivity.this, ScanActivity.class);
                                         startActivity(intent);

                                     }
                                 }
        );

    }

}
