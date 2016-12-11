package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
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
import com.example.android.shoppingapp3.model.ShoppingCartDbHelper;
import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.model.ShoppingList;
import com.example.android.shoppingapp3.model.ShoppingListDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayCartActivity extends ActionBarActivity {

    // Data structures

    private ShoppingList mShoppingList = new ShoppingList();

    private int mRowNumber;

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

    private String mUPC, mLastDatePurchased, mName, mPrice, mCategory;
    int mQuantity, mLastQuantity;
    double mPriceValue, mSubtotal, mPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_cart);

        // Get the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        getSupportActionBar().setTitle("Cart");

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPrefs.getString(
                getString(R.string.pref_language_key),
                getString(R.string.pref_language_english));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayCartActivity.this, DisplayListActivity.class);
                startActivity(intent);
            }
        });

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
        getMenuInflater().inflate(R.menu.menu_display_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {

            //noinspection SimplifiableIfStatement
            case R.id.action_settings:

            {
                Intent intent = new Intent(DisplayCartActivity.this, SettingsActivity.class);

                // Pass the name of the activity so that we can detect it inside SettingsActivity,
                // and we can return to this activity and list can be refreshed if the sort
                // order is changed.
                intent.putExtra(getString(R.string.calling_activity_name), this.getLocalClassName());

                startActivity(intent);
                return true;

            }

            case R.id.action_home:
            {

                Intent intent = new Intent(DisplayCartActivity.this, MainActivity.class);
                startActivity(intent);

            }



            case R.id.action_search:
            {



            }

            case R.id.action_delete:
            {

                // Initialize the shoppingCartDBHelper object

                mShoppingCartDbHelper = new ShoppingCartDbHelper(getApplicationContext());

                // Initialize the SQLiteDatabase object

                sqLiteDatabase = mShoppingCartDbHelper.getReadableDatabase();


                for(int i=0; i<listDataHeader.size(); i++) {

                    if(listDataHeader.get(i).isSelected()){

                        // For SQLiteDatabase: Delete this item here, if checked.

                        String item_for_DB_deletion = listDataHeader.get(i).getProductName() + "";

                        // Delete the shopping item from the SQLite database

                        mShoppingCartDbHelper.deleteCartItem(item_for_DB_deletion, sqLiteDatabase);

                        Intent intent = new Intent(DisplayCartActivity.this, DisplayListActivity.class);

                        startActivity(intent);

                    }

                }

                return true;

            }

            default:

                return super.onOptionsItemSelected(item);

        }

    }

    private void prepareListData(){

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);

        String sortOrder = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));

        listDataHeader = new ArrayList<ShoppingItem>();

        listDataChild = new HashMap<ShoppingItem, List<ShoppingItem>>();

        // Get the list of shopping items from the database

        // Reload the list from the SQLite Database. Since we are not searching for anything
        // we will make searchItem just be blank.

        String searchItem = "";

        if(sortOrder.equals("no sort")){

            mShoppingList = reloadedCart.reloadCartFromDB("get", searchItem, getApplicationContext());

        }

        else if(sortOrder.equals("alphabetical")){

            mShoppingList = reloadedCart.reloadCartFromDB("sortByName", searchItem, getApplicationContext());
        }

        else if(sortOrder.equals("priority")){

            mShoppingList = reloadedCart.reloadCartFromDB("sortByPriority", searchItem, getApplicationContext());
        }


        mRowNumber = reloadedCart.getListSize();


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

        FloatingActionButton payFAB = (FloatingActionButton) findViewById(R.id.payFAB);

        payFAB.setOnClickListener(new View.OnClickListener() {

                                     @Override
                                     public void onClick(View v) {

                                         // Only send the cart items that have a check mark to the PayActivity and
                                         // reject those unchecked. In other words, delete unchecked cart items from the
                                         // ShoppingCartDB. If no items are selected at all, and the FAB button is pressed,
                                         // then alert the user to mark some items, and do not send intent to PayActivity

                                         int unMarked = 0;


                                         for(int i=0; i<listDataHeader.size(); i++) {

                                             if (!listDataHeader.get(i).isSelected()) {

                                                 // Count the number of items that are not selected,
                                                 // if it equals the size of the list, it means that none are selected.
                                                 // In that case, alert the user to check some items for checkout
                                                 // and DO NOT go to PayActivity

                                                 unMarked++;

                                             }

                                         }

                                             if(unMarked==listDataHeader.size()){

                                                 Toast.makeText(getApplicationContext(), "You have not selected any items for checkout. Please check some!",
                                                         Toast.LENGTH_LONG).show();

                                             }

                                         else{ // Some items are checked. Delete the unchecked ones from the ShoppingCartDB.

                                                 // Initialize the shoppingCartDBHelper object

                                                 mShoppingCartDbHelper = new ShoppingCartDbHelper(getApplicationContext());

                                                 // Initialize the SQLiteDatabase object

                                                 sqLiteDatabase = mShoppingCartDbHelper.getReadableDatabase();

                                                 for(int i=0; i<listDataHeader.size(); i++) {

                                                     if (!listDataHeader.get(i).isSelected()) {

                                                         // For SQLiteDatabase: Delete this item here, if checked.

                                                         String item_for_DB_deletion = listDataHeader.get(i).getProductName() + "";

                                                         // Delete the shopping item from the SQLite database

                                                         mShoppingCartDbHelper.deleteCartItem(item_for_DB_deletion, sqLiteDatabase);

                                                     }

                                                 }

                                                 // Then, play a cash register sound, and go to PayActivity to show the checkout list.

                                                 MediaPlayer player = MediaPlayer.create(getApplicationContext(), R.raw.cashregister);
                                                 player.start();

                                                 Intent intent = new Intent(DisplayCartActivity.this, PayActivity.class);
                                                 startActivity(intent);

                                             }

                                     }
                                 }
        );

    }
}
