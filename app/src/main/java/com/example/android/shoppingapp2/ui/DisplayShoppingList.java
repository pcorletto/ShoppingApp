package com.example.android.shoppingapp2.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.shoppingapp2.R;
import com.example.android.shoppingapp2.model.ShoppingDbHelper;
import com.example.android.shoppingapp2.model.ShoppingItem;
import com.example.android.shoppingapp2.model.ShoppingItemAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayShoppingList extends AppCompatActivity{

    private ListView listview;
    private ShoppingItem[] mShoppingItems;
    private List<ShoppingItem> list = new ArrayList<>();

    // Need a newList, for when shopping items are deleted, a new list is created
    private List<ShoppingItem> newList = new ArrayList<>();

    View totalFooterView;

    private ShoppingItemAdapter mAdapter;
    ShoppingDbHelper shoppingDbHelper;
    SQLiteDatabase sqLiteDatabase;
    private int mRowNumber;

    // Variables for the footer:
    private int mItemCount;
    private double mTotalPrice;
    private double mSalesTax;
    private double mFinalPrice;

    private ShoppingItem mShoppingItem;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_shopping_list);

        //mSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        //mEditor = mSharedPreferences.edit();

        mItemCount = 0;
        mTotalPrice = 0;
        mSalesTax = 0;
        mFinalPrice = 0;

        listview = (ListView) findViewById(android.R.id.list);

        Intent intent = getIntent();

        mRowNumber = intent.getIntExtra(getString(R.string.ROW_NUMBER), 0);

        Parcelable[] parcelables = intent.getParcelableArrayExtra(getString(R.string.SHOPPING_LIST));

        mShoppingItems = Arrays.copyOf(parcelables, mRowNumber, ShoppingItem[].class);

        for (int i = 0; i < mRowNumber; i++) {

            list.add(mShoppingItems[i]);

            mItemCount = mShoppingItems[i].getQuantity() + mItemCount;
            mTotalPrice = mShoppingItems[i].getItemPrice() + mTotalPrice;
            mSalesTax = (mShoppingItems[i].getItemPrice() * 0.07) + mSalesTax;
            mFinalPrice = mTotalPrice + mSalesTax;

        }

        mAdapter = new ShoppingItemAdapter(DisplayShoppingList.this, list);

        mAdapter.notifyDataSetChanged();

        listview.setAdapter(mAdapter);

        totalFooterView = View.inflate(getBaseContext(), R.layout.footer_layout, null);

        listview.addFooterView(totalFooterView);

        final ViewHolder holder = new ViewHolder();

        holder.itemCountEditText = (TextView) totalFooterView.findViewById(R.id.itemCountEditText);
        holder.totalPriceEditText = (TextView) totalFooterView.findViewById(R.id.totalPriceEditText);
        holder.salesTaxEditText = (TextView) totalFooterView.findViewById(R.id.salesTaxEditText);
        holder.finalPriceEditText = (TextView) totalFooterView.findViewById(R.id.finalPriceEditText);
        holder.deleteSelectedItemsBtn = (Button) totalFooterView.findViewById(R.id.deleteSelectedItemsBtn);
        holder.returnToMainBtn = (Button) totalFooterView.findViewById(R.id.returnToMainBtn);

        final DecimalFormat df = new DecimalFormat("$0.00");

        holder.itemCountEditText.setText(mItemCount + "");
        holder.totalPriceEditText.setText(df.format(mTotalPrice));
        holder.salesTaxEditText.setText(df.format(mSalesTax));
        holder.finalPriceEditText.setText(df.format(mFinalPrice));

        holder.deleteSelectedItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).isSelected()) {

                        // For ListView: Skip checked or selected items. These will be deleted and will not
                        // be added to the new list view.

                        // For SQLiteDatabase: Delete this item here, if checked.

                        String item_for_DB_deletion = list.get(i).getProductName() + "";

                        // Initialize the shoppingDBHelper object

                        shoppingDbHelper = new ShoppingDbHelper(getApplicationContext());

                        // Initialize the SQLiteDatabase object

                        sqLiteDatabase = shoppingDbHelper.getReadableDatabase();

                        mItemCount = mItemCount - list.get(i).getQuantity();
                        mTotalPrice = mTotalPrice - list.get(i).getItemPrice();
                        mSalesTax = mSalesTax - (list.get(i).getItemPrice() * 0.07);
                        mFinalPrice = mFinalPrice - list.get(i).getSubtotal();

                        // Delete the shopping item from the SQLite database

                        shoppingDbHelper.deleteShoppingItem(item_for_DB_deletion, sqLiteDatabase);

                        // Set the text views in the footer to the new values after deletion

                        holder.itemCountEditText.setText(mItemCount +"");
                        holder.totalPriceEditText.setText(df.format(mTotalPrice));
                        holder.salesTaxEditText.setText(df.format(mSalesTax));
                        holder.finalPriceEditText.setText(df.format(mFinalPrice));

                        finish();


                    } else {

                        // Add the item to the listview, because it won't be deleted.

                        newList.add(list.get(i));

                    }

                }

                mAdapter.notifyDataSetChanged();

                mAdapter = new ShoppingItemAdapter(DisplayShoppingList.this, newList);

                listview.setAdapter(mAdapter);

                // Add footer

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

        public static final class ViewHolder{

            // This ViewHolder is for the footer, to hold the item count, total price, sales tax,
            //and final price.

            public static TextView itemCountEditText;
            public static TextView totalPriceEditText;
            public static TextView salesTaxEditText;
            public static TextView finalPriceEditText;
            public Button deleteSelectedItemsBtn, returnToMainBtn;

        }

    @Override
    public void onResume(){

        super.onResume();
        list.clear();

        mItemCount = 0;
        mTotalPrice = 0;
        mSalesTax = 0;
        mFinalPrice = 0;

        // Reload the items from the database

        // Initialize shopping item

        mShoppingItem = new ShoppingItem();

        //Initialize ShoppingDbHelper and SQLiteDB

        shoppingDbHelper = new ShoppingDbHelper(getApplicationContext());
        sqLiteDatabase = shoppingDbHelper.getReadableDatabase();

        cursor = ShoppingDbHelper.getShoppingItem(sqLiteDatabase);

        mRowNumber = 0;

        if(cursor.moveToFirst()) {

            do {

                String productName, category;
                int quantity;
                double unitPrice, subtotal;

                quantity = cursor.getInt(0);
                productName = cursor.getString(1);
                unitPrice = cursor.getDouble(2);
                category = cursor.getString(3);
                subtotal = cursor.getDouble(4);

                mShoppingItem = new ShoppingItem(quantity, productName, unitPrice, category, subtotal);

                list.add(mShoppingItem);

                mItemCount = quantity + mItemCount;
                mTotalPrice = subtotal + mTotalPrice;
                mSalesTax = (subtotal * 0.07)+ mSalesTax;
                mFinalPrice = mTotalPrice + mSalesTax;

                mRowNumber++;


            }

            while (cursor.moveToNext());

        }

        // Done reloading items from the database

        mAdapter.refresh(list);


        // Refresh the footer:


        final DecimalFormat df = new DecimalFormat("$0.00");


        TextView itemCountEditText = (TextView) totalFooterView.findViewById(R.id.itemCountEditText);
        TextView totalPriceEditText = (TextView) totalFooterView.findViewById(R.id.totalPriceEditText);
        TextView salesTaxEditText = (TextView) totalFooterView.findViewById(R.id.salesTaxEditText);
        TextView finalPriceEditText = (TextView) totalFooterView.findViewById(R.id.finalPriceEditText);

        itemCountEditText.setText(mItemCount +"");
        totalPriceEditText.setText(df.format(mTotalPrice));
        salesTaxEditText.setText(df.format(mSalesTax));
        finalPriceEditText.setText(df.format(mFinalPrice));

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


}