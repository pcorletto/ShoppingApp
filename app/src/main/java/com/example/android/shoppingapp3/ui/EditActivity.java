package com.example.android.shoppingapp3.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.model.ReloadListFromDB;
import com.example.android.shoppingapp3.model.ShoppingList;
import com.example.android.shoppingapp3.model.ShoppingListDbHelper;

public class EditActivity extends ActionBarActivity {

    private Toolbar toolbar;

    private Button save_changes_button, display_button;
    private RatingBar ratingBar;
    private RadioGroup radioGroup;
    private RadioButton yesRadioButton, noRadioButton;
    private EditText productNameEditText, categoryEditText, priceEditText, quantityEditText;
    private EditText lastQuantityPurchasedEditText, lastDatePurchasedEditText;

    public static final String TAG = EditActivity.class.getSimpleName();

    private ShoppingList mShoppingList = new ShoppingList();
    ReloadListFromDB reloadedList = new ReloadListFromDB();

    private String mName, mCategory, mLastDatePurchased, mTaxable;

    int mQuantity, mLastQuantityPurchased;
    double mPrice, mPriority;
    boolean taxable;

    Context context;
    ShoppingListDbHelper mShoppingListDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        productNameEditText = (EditText) findViewById(R.id.productNameEditText);
        categoryEditText = (EditText) findViewById(R.id.categoryEditText);
        priceEditText = (EditText) findViewById(R.id.priceEditText);
        quantityEditText = (EditText) findViewById(R.id.quantityEditText);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        yesRadioButton = (RadioButton) findViewById(R.id.yesRadioButton);
        noRadioButton = (RadioButton) findViewById(R.id.noRadioButton);
        lastQuantityPurchasedEditText = (EditText) findViewById(R.id.lastQuantityPurchasedEditText);
        lastDatePurchasedEditText = (EditText) findViewById(R.id.lastDatePurchasedEditText);
        save_changes_button = (Button) findViewById(R.id.save_changes_button);
        display_button = (Button) findViewById(R.id.display_button);

        Intent intent = getIntent();
        mName = intent.getStringExtra(getString(R.string.product_name));

        productNameEditText.setText(mName);

        prepareListData(mName);

        mCategory = mShoppingList.getShoppingItem(0).getCategory();
        categoryEditText.setText(mCategory);

        mPrice = mShoppingList.getShoppingItem(0).getItemPrice();
        priceEditText.setText(mPrice+"");

        mQuantity = mShoppingList.getShoppingItem(0).getQuantity();
        quantityEditText.setText(mQuantity+"");

        mPriority = mShoppingList.getShoppingItem(0).getPriority();

        float priority = (float) mPriority;
        ratingBar.setRating(priority);

        if(mShoppingList.getShoppingItem(0).isTaxable()){

            yesRadioButton.setChecked(true);
            mTaxable = "true";

        }

        else{

            noRadioButton.setChecked(true);
            mTaxable = "false";

        }

        mLastQuantityPurchased = mShoppingList.getShoppingItem(0).getLastQuantity();
        lastQuantityPurchasedEditText.setText(mLastQuantityPurchased+"");

        mLastDatePurchased = mShoppingList.getShoppingItem(0).getLastDatePurchased();
        lastDatePurchasedEditText.setText(mLastDatePurchased);

        // Get the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        getSupportActionBar().setTitle(TAG);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditActivity.this, DisplayListActivity.class);
                startActivity(intent);
            }
        });

        save_changes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mName = productNameEditText.getText().toString();
                mCategory = categoryEditText.getText().toString();
                mPrice = Double.parseDouble(priceEditText.getText().toString());
                mQuantity = Integer.parseInt(quantityEditText.getText().toString());
                double subtotal = mPrice * mQuantity;
                mPriority = ratingBar.getRating();

                if(mTaxable.equals("true")){
                    taxable = true;
                }

                else{

                    taxable = false;
                }

                mLastQuantityPurchased = Integer.parseInt(lastQuantityPurchasedEditText.getText().toString());
                mLastDatePurchased = lastDatePurchasedEditText.getText().toString();

                updateShoppingItem(mShoppingList.getShoppingItem(0).getProductName(),
                        mName, mCategory, mPrice, mQuantity, subtotal, mPriority,
                        mTaxable, mLastQuantityPurchased, mLastDatePurchased);

                Toast.makeText(getApplicationContext(), "Shopping Item Updated!!!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        display_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditActivity.this, DisplayListActivity.class);
                startActivity(intent);

            }
        });

    }

    private void prepareListData(String searchItem){


        // Get the shopping item to be edited from the database
        mShoppingList = reloadedList.reloadListFromDB("search", searchItem, EditActivity.this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_settings: {
                return true;
            }

            case R.id.action_home: {

                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);

            }

            default:

                return super.onOptionsItemSelected(item);

        }

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.yesRadioButton:
                if (checked)
                    mTaxable = "true";
                break;

            case R.id.noRadioButton:
                if (checked)
                    mTaxable = "false";
                break;
        }
    }

    public void updateShoppingItem(String product_name, String mName, String mCategory, double mPrice, int mQuantity,
                                   double subtotal, double mPriority, String taxable, int mLastQuantityPurchased,
                                   String mLastDatePurchased){

        mShoppingListDbHelper = new ShoppingListDbHelper(this);
        sqLiteDatabase = mShoppingListDbHelper.getWritableDatabase();
        mShoppingListDbHelper.updateShoppingItem(product_name, mName, mCategory, mPrice, mQuantity, subtotal,
                mPriority, taxable, mLastQuantityPurchased, mLastDatePurchased, sqLiteDatabase);

    }

}
