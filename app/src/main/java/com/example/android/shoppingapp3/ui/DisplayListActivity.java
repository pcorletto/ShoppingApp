package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
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

    private String item_for_DB_edit;

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

        toolbar.setNavigationIcon(R.drawable.ic_back);
        getSupportActionBar().setTitle("List");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

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

                // Pass the name of the activity so that we can detect it inside SettingsActivity,
                // and we can return to this activity and list can be refreshed if the sort
                // order is changed.
                intent.putExtra(getString(R.string.calling_activity_name), this.getLocalClassName());
                intent.putExtra(getString(R.string.preceding_activity_name), this.getLocalClassName());
                startActivity(intent);
                return true;

            }

            case R.id.action_edit:
            {

                // Initialize the shoppingListDBHelper object

                mShoppingListDbHelper = new ShoppingListDbHelper(getApplicationContext());

                // Initialize the SQLiteDatabase object

                sqLiteDatabase = mShoppingListDbHelper.getReadableDatabase();

                int count = 0;

                for(int i=0; i<listDataHeader.size(); i++) {

                    if(listDataHeader.get(i).isSelected()){

                        // For SQLiteDatabase: Edit this item here, if checked.

                        item_for_DB_edit = listDataHeader.get(i).getProductName() + "";

                        count ++;

                    }

                }

                if(count>1){

                    Toast.makeText(this, "Only edit one item at a time!", Toast.LENGTH_LONG).show();
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    Intent intent = new Intent(DisplayListActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                else if (count == 1) {

                    // Edit the shopping item from the SQLite database
                    Intent intent = new Intent(DisplayListActivity.this, EditActivity.class);
                    intent.putExtra(getString(R.string.product_name), item_for_DB_edit);
                    startActivity(intent);
                }

                return true;

            }

            case R.id.action_cart:
            {

                Intent intent = new Intent(DisplayListActivity.this, DisplayCartActivity.class);
                startActivity(intent);
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

            case R.id.action_scan:

            {

                Intent intent = new Intent(DisplayListActivity.this, ScanActivity.class);
                startActivity(intent);


                return true;

            }

            case R.id.action_search:

            {

                Intent intent = new Intent(this, SearchActivity.class);
                String calling_activity_name = this.getLocalClassName();
                intent.putExtra(getString(R.string.calling_activity_name), calling_activity_name);
                startActivity(intent);

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

            mShoppingList = reloadedList.reloadListFromDB("get", searchItem, getApplicationContext());

        }

        else if(sortOrder.equals("alphabetical")){

            mShoppingList = reloadedList.reloadListFromDB("sortByName", searchItem, getApplicationContext());
        }

        else if(sortOrder.equals("priority")){

            mShoppingList = reloadedList.reloadListFromDB("sortByPriority", searchItem, getApplicationContext());
        }



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

                                                     if(reloadedCart.countFoundItems(listDataHeader.get(i).getProductName(), DisplayListActivity.this)==0) {

                                                         // Before inserting the checked item(s) into the Shopping Cart SQLite
                                                         // database, search for it(them) in the Shopping Cart SQLite. If not found,
                                                         // add it(them)


                                                         // Insert the shopping item into the Shopping Cart SQLite database

                                                         mShoppingCartDbHelper.addItem(mUPC, mQuantity, mLastQuantity, mLastDatePurchased, mName,
                                                                 mPriority, mPriceValue, mCategory, mSubtotal, mImage, taxable_string, sqLiteDatabase);

                                                         Intent intent = new Intent(DisplayListActivity.this, DisplayCartActivity.class);

                                                         startActivity(intent);
                                                     }

                                                     else{

                                                         // Otherwise, alert the user that the item is already there and does not need to be added!

                                                         ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                                                         toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);

                                                         Toast.makeText(DisplayListActivity.this, listDataHeader.get(i).getProductName() + " is already in your shopping cart! Do not add it again!",
                                                                 Toast.LENGTH_LONG).show();

                                                     }

                                                 }

                                             }


                                         }


                                     }
                                 }
        );

    }

}
