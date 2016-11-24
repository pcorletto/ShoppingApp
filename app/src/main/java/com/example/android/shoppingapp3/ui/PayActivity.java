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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.adapters.ExpandablePayAdapter;
import com.example.android.shoppingapp3.model.ReloadCartFromDB;
import com.example.android.shoppingapp3.model.ShoppingCartDbHelper;
import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.model.ShoppingList;
import com.example.android.shoppingapp3.model.ShoppingListDbHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PayActivity extends ActionBarActivity {

    // Data structures

    private ShoppingList mShoppingList = new ShoppingList();

    private int mRowNumber;

    ReloadCartFromDB reloadedCart = new ReloadCartFromDB();


    // Added these for ExpandableListView extension...

    ExpandablePayAdapter listAdapter;
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

    View totalFooterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        // Get the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        getSupportActionBar().setTitle("Pay");
        toolbar.setSubtitle("Checkout");

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPrefs.getString(
                getString(R.string.pref_language_key),
                getString(R.string.pref_language_english));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this, DisplayCartActivity.class);
                startActivity(intent);
            }
        });

        //Toast.makeText(this, "Language selected: " + language, Toast.LENGTH_LONG).show();

        // Get the expandable list view
        expListView = (ExpandableListView) findViewById(android.R.id.list);

        listDataChild = new HashMap<ShoppingItem, List<ShoppingItem>>();

        // Preparing list data

        prepareListData();

        listAdapter = new ExpandablePayAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        placeFAB();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {

            //noinspection SimplifiableIfStatement
            case R.id.action_settings:

            {
                Intent intent = new Intent(PayActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            }
            case R.id.action_search:
            {

                Toast.makeText(PayActivity.this, "Search button pressed!", Toast.LENGTH_LONG).show();
                return true;

            }

            case R.id.action_edit:
            {

                Intent intent = new Intent(PayActivity.this, DisplayCartActivity.class);

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

        mShoppingList = reloadedCart.reloadCartFromDB("get", searchItem, getApplicationContext());

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

                                          // Footer section:

                                          // Calculate the total item count, subtotal, total sales tax, and total

                                          int count = 0;
                                          double subtotal = 0;
                                          double tax = 0;
                                          double total;
                                          double addtax = 0;

                                          for(int i=0; i<listDataHeader.size(); i++){

                                              count = count + listDataHeader.get(i).getQuantity();
                                              subtotal = subtotal + listDataHeader.get(i).getQuantity() * listDataHeader.get(i).getItemPrice();
                                              if(listDataHeader.get(i).isTaxable()){

                                                  addtax = listDataHeader.get(i).getSubtotal() * 0.07;
                                                  tax = tax + addtax;

                                              }

                                              else{

                                                  addtax = 0;
                                                  tax = tax + addtax;
                                              }

                                          }

                                          DecimalFormat df = new DecimalFormat("$0.00");

                                          total = subtotal + tax;

                                          totalFooterView = View.inflate(getBaseContext(), R.layout.footer_layout, null);

                                          expListView.addFooterView(totalFooterView);

                                          final ViewHolder holder = new ViewHolder();

                                          holder.footerLayout = (LinearLayout) totalFooterView.findViewById(R.id.footerLayout);

                                          holder.quantityTextView = (TextView) totalFooterView.findViewById(R.id.quantityTextView);
                                          holder.subtotalTextView = (TextView) totalFooterView.findViewById(R.id.subtotalTextView);
                                          holder.salesTaxTextView = (TextView) totalFooterView.findViewById(R.id.salesTaxTextView);
                                          holder.totalTextView = (TextView) totalFooterView.findViewById(R.id.totalTextView);

                                          holder.quantityTextView.setText(count+"");
                                          holder.subtotalTextView.setText(df.format(subtotal));
                                          holder.salesTaxTextView.setText(df.format(tax));
                                          holder.totalTextView.setText(df.format(total));


                                      }
                                  }
        );

    }

    public final class ViewHolder{

        // This ViewHolder is for the footer, to hold the item count, total price, sales tax,
        //and final price.

        public LinearLayout footerLayout;
        public TextView quantityTextView;
        public TextView subtotalTextView;
        public TextView salesTaxTextView;
        public TextView totalTextView;

    }
}
