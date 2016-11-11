package com.example.android.shoppingapp3.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.model.ShoppingList;
import com.example.android.shoppingapp3.model.ShoppingListDbHelper;

public class StoreDummyActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText quantityEditText, lastQuantityEditText, lastDatePurchasedEditText,
        productNameEditText, priorityEditText, itemPriceEditText, categoryEditText,
        subtotalEditText;

    private int quantity, lastQuantity, priority;
    private double itemPrice, subtotal;
    private String lastDatePurchased, productName, category;

    private Button storeButton;

    // Data structures

    private ShoppingItem mShoppingItem;
    private int mRowNumber;
    private ShoppingList mShoppingList = new ShoppingList();

    Context context;
    ShoppingListDbHelper shoppingListDbHelper;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_dummy);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_hamburger);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        //toolbar.setSubtitle("Subtitle");

        context = this;

        // Perform DB insertion...

        // Initialize shoppingListDbhelper object and SQLiteDatabase object.

        shoppingListDbHelper = new ShoppingListDbHelper(context);
        sqLiteDatabase = shoppingListDbHelper.getWritableDatabase();

        quantityEditText = (EditText) findViewById(R.id.quantityEditText);
        lastQuantityEditText = (EditText) findViewById(R.id.lastQuantityEditText);
        lastDatePurchasedEditText = (EditText) findViewById(R.id.lastDatePurchasedEditText);
        productNameEditText = (EditText) findViewById(R.id.productNameEditText);
        priorityEditText = (EditText) findViewById(R.id.priorityEditText);
        itemPriceEditText = (EditText) findViewById(R.id.itemPriceEditText);
        categoryEditText = (EditText) findViewById(R.id.categoryEditText);
        subtotalEditText = (EditText) findViewById(R.id.subtotalEditText);
        storeButton = (Button) findViewById(R.id.storeButton);

        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String upc = "xfsdfs";

                quantity = Integer.parseInt(quantityEditText.getText().toString());
                lastQuantity = Integer.parseInt(lastQuantityEditText.getText().toString());
                lastDatePurchased = lastDatePurchasedEditText.getText().toString();
                productName = productNameEditText.getText().toString();
                priority = Integer.parseInt(priorityEditText.getText().toString());
                itemPrice = Double.parseDouble(itemPriceEditText.getText().toString());
                category = categoryEditText.getText().toString();
                subtotal = Double.parseDouble(subtotalEditText.getText().toString());

                shoppingListDbHelper.addItem(upc, quantity, lastQuantity, lastDatePurchased, productName,
                        priority, itemPrice, category, subtotal, sqLiteDatabase);

                shoppingListDbHelper.close();
                finish();
            }
        });





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store_dummy, menu);
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
