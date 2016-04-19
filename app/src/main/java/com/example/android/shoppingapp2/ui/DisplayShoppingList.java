package com.example.android.shoppingapp2.ui;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.shoppingapp2.R;
import com.example.android.shoppingapp2.model.ShoppingDbHelper;
import com.example.android.shoppingapp2.model.ShoppingItem;
import com.example.android.shoppingapp2.model.ShoppingItemAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayShoppingList extends ListActivity implements ShoppingItemAdapter.ShoppingItemAdapterCallBack {

    private static final String PREFS_FILE = "com.example.android.shoppingapp2.preferences";
    private static final String ITEMCOUNT = "itemcount" ;

    private ListView listview;
    private ShoppingItem[] mShoppingItems;
    private List<ShoppingItem> list = new ArrayList<>();
    private List<ShoppingItem> newList = new ArrayList<>();

    View totalFooterView;

    ShoppingDbHelper shoppingDbHelper;
    SQLiteDatabase sqLiteDatabase;

    private int mRowNumber;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_shopping_list);

        mSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();


        listview = (ListView) findViewById(android.R.id.list);

        Intent intent = getIntent();

        mRowNumber = intent.getIntExtra(getString(R.string.ROW_NUMBER), 0);

        Parcelable[] parcelables = intent.getParcelableArrayExtra(getString(R.string.SHOPPING_LIST));

        mShoppingItems = Arrays.copyOf(parcelables, mRowNumber, ShoppingItem[].class);

        for(int i=0; i<mRowNumber; i++){

            int quantity = mSharedPreferences.getInt(ITEMCOUNT + i, 1);

            double subtotal = quantity * mShoppingItems[i].getItemPrice();

            mShoppingItems[i].setQuantity(quantity);

            mShoppingItems[i].setSubtotal(subtotal);

            list.add(mShoppingItems[i]);

        }

        // Added a footer to the ListView to display the item count, total price,
        // sales tax and final price at the bottom of the list.

        // What list do we display on the footer? We have three options:

        // If the app just starts, it should take the list from "list", which is the one
        // we get from the mShoppingItems array passed in via Intent from StoreActivity.java

        // If the user taps the increment or decrement quantity buttons on the ListView,
        // then we need to use the updated "list" from the callback we get from ShoppingItemAdapter

        // If the user deletes an item from the list, then we need to use "newList" from the
        // holder.deleteSelectedItemsBtn.onClickListener method down below.



        refreshFooter(list);

        // Update footer

        ShoppingItemAdapter adapter = new ShoppingItemAdapter(DisplayShoppingList.this, list);

        adapter.setCallback(this);

        adapter.notifyDataSetChanged();

        listview.setAdapter(adapter);

        // Add Footer

        listview.addFooterView(totalFooterView);

    }

    @Override
    protected void onPause() {
        super.onPause();

        DecimalFormat df = new DecimalFormat("$0.00");

        for(int i=0; i<mShoppingItems.length; i++){

            mEditor.putInt(ITEMCOUNT + i, mShoppingItems[i].getQuantity());

        }

        mEditor.apply();
    }

    public void onBackPressed() {

        Intent startMain = new Intent(DisplayShoppingList.this, MainActivity.class);
        startActivity(startMain);

    }

    public void refreshFooter(List<ShoppingItem> passedInList) {

        // Refresh footer

        totalFooterView = View.inflate(getBaseContext(), R.layout.footer_layout, null);

        ViewHolder holder = new ViewHolder();

        holder.itemCountEditText = (TextView) totalFooterView.findViewById(R.id.itemCountEditText);
        holder.totalPriceEditText = (TextView) totalFooterView.findViewById(R.id.totalPriceEditText);
        holder.salesTaxEditText = (TextView) totalFooterView.findViewById(R.id.salesTaxEditText);
        holder.finalPriceEditText = (TextView) totalFooterView.findViewById(R.id.finalPriceEditText);
        holder.deleteSelectedItemsBtn = (Button) totalFooterView.findViewById(R.id.deleteSelectedItemsBtn);
        holder.returnToMainBtn = (Button) totalFooterView.findViewById(R.id.returnToMainBtn);

        int itemCount = 0;
        double totalPrice = 0;
        double salesTax;
        double finalPrice;

        for (int i = 0; i < passedInList.size(); i++) {

            itemCount = itemCount + passedInList.get(i).getQuantity();
            totalPrice = totalPrice + passedInList.get(i).getSubtotal();
        }

        salesTax = totalPrice * 0.07;
        finalPrice = totalPrice + salesTax;

        //Set the TOTAL data:

        DecimalFormat df = new DecimalFormat("$0.00");

        holder.itemCountEditText.setText(itemCount + "");
        holder.totalPriceEditText.setText(df.format(totalPrice));
        holder.salesTaxEditText.setText(df.format(salesTax));
        holder.finalPriceEditText.setText(df.format(finalPrice));

        holder.deleteSelectedItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listview.removeFooterView(totalFooterView);

                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).isSelected()) {

                        // For ListView: Skip checked or selected items. These will be deleted and will not
                        // be added to the new listview.

                        // For SQLiteDatabase: Delete this item here, if checked.

                        String item_for_DB_deletion = list.get(i).getProductName();

                        // Initialize the shoppingDbHelper object

                        shoppingDbHelper = new ShoppingDbHelper(getApplicationContext());

                        // Initialize the SQLiteDatabase object

                        sqLiteDatabase = shoppingDbHelper.getReadableDatabase();

                        shoppingDbHelper.deleteShoppingItem(item_for_DB_deletion, sqLiteDatabase);

                        Toast.makeText(getApplicationContext(), "Shopping item deleted", Toast.LENGTH_LONG).show();


                    } else {

                        // Add the item to the listview, because it won't be deleted.

                        newList.add(list.get(i));

                    }

                }

                refreshFooter(newList);

                // Just testing ...

                ShoppingItemAdapter adapter = new ShoppingItemAdapter(DisplayShoppingList.this, newList);

                adapter.notifyDataSetChanged();

                listview.setAdapter(adapter);

                // Add Footer

                listview.addFooterView(totalFooterView);

            }

        });

        holder.returnToMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DisplayShoppingList.this, MainActivity.class);

                startActivity(intent);

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_shopping_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public final class ViewHolder{

        // This ViewHolder is for the footer, to hold the item count, total price, sales tax,
        //and final price.

        public TextView itemCountEditText;
        public TextView totalPriceEditText;
        public TextView salesTaxEditText;
        public TextView finalPriceEditText;
        public Button deleteSelectedItemsBtn, returnToMainBtn;

    }

}