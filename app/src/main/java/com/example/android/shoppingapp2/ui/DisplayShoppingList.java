package com.example.android.shoppingapp2.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.shoppingapp2.R;
import com.example.android.shoppingapp2.model.ShoppingDbHelper;
import com.example.android.shoppingapp2.model.ShoppingItem;
import com.example.android.shoppingapp2.model.ShoppingItemAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayShoppingList extends ListActivity {

    private ListView listview;
    private ShoppingItem[] mShoppingItems;
    private List<ShoppingItem> list = new ArrayList<>();

    private List<ShoppingItem> newList = new ArrayList<>();

    ShoppingDbHelper shoppingDbHelper;
    SQLiteDatabase sqLiteDatabase;


    private int mRowNumber;
    private int mItemCount;
    private double mTotalPrice, mSalesTax, mFinalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_shopping_list);
        listview = (ListView) findViewById(android.R.id.list);

        // Added a footer to the ListView to display the item count, total price,
        // sales tax and final price at the bottom of the list.

        View totalFooterView = View.inflate(this, R.layout.footer_layout, null);

        ViewHolder holder = new ViewHolder();

        holder.itemCountEditText = (EditText) totalFooterView.findViewById(R.id.itemCountEditText);
        holder.totalPriceEditText = (EditText) totalFooterView.findViewById(R.id.totalPriceEditText);
        holder.salesTaxEditText = (EditText) totalFooterView.findViewById(R.id.salesTaxEditText);
        holder.finalPriceEditText = (EditText) totalFooterView.findViewById(R.id.finalPriceEditText);
        holder.deleteSelectedItemsBtn = (Button) totalFooterView.findViewById(R.id.deleteSelectedItemsBtn);
        holder.returnToMainBtn = (Button) totalFooterView.findViewById(R.id.returnToMainBtn);

        Intent intent = getIntent();

        mRowNumber = intent.getIntExtra(getString(R.string.ROW_NUMBER), 0);

        mItemCount = intent.getIntExtra(getString(R.string.ITEM_COUNT), 0);
        mTotalPrice = intent.getDoubleExtra(getString(R.string.TOTAL_PRICE), 0);
        mSalesTax = intent.getDoubleExtra(getString(R.string.SALES_TAX), 0);
        mFinalPrice = intent.getDoubleExtra(getString(R.string.FINAL_PRICE), 0);

        // Set the TOTAL data:

        DecimalFormat df = new DecimalFormat("$0.00");

        holder.itemCountEditText.setText(mItemCount+"");
        holder.totalPriceEditText.setText(df.format(mTotalPrice));
        holder.salesTaxEditText.setText(df.format(mSalesTax));
        holder.finalPriceEditText.setText(df.format(mFinalPrice));

        listview.addFooterView(totalFooterView);

        Parcelable[] parcelables = intent.getParcelableArrayExtra(getString(R.string.SHOPPING_LIST));


        mShoppingItems = Arrays.copyOf(parcelables, mRowNumber, ShoppingItem[].class);

        for(int i=0; i<mRowNumber; i++){

            list.add(mShoppingItems[i]);

        }

        final ShoppingItemAdapter adapter = new ShoppingItemAdapter(this, list);

        listview.setAdapter(adapter);

        holder.returnToMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DisplayShoppingList.this, MainActivity.class);

                startActivity(intent);

            }

        });

        holder.deleteSelectedItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                for(int i=0; i<list.size(); i++) {

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

                        ShoppingItemAdapter adapter2 = new ShoppingItemAdapter(DisplayShoppingList.this, newList);

                        listview.setAdapter(adapter2);

            }

        });

    }

    public void onBackPressed() {

        Intent startMain = new Intent(DisplayShoppingList.this, MainActivity.class);
        startActivity(startMain);

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

        public EditText itemCountEditText;
        public EditText totalPriceEditText;
        public EditText salesTaxEditText;
        public EditText finalPriceEditText;
        public Button deleteSelectedItemsBtn, returnToMainBtn;

    }

}
