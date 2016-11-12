package com.example.android.shoppingapp3.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.android.shoppingapp3.BuildConfig;
import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.model.ShoppingList;
import com.example.android.shoppingapp3.model.ShoppingListDbHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class ScanActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Button scanBtn,storeBtn,displayBtn, minusBtn, plusBtn;
    private RatingBar ratingBar;
    private EditText productNameEditText, priceEditText, quantityEditText;
    public static final String TAG = ScanActivity.class.getSimpleName();

    private ShoppingItem mShoppingItem;
    private int mRowNumber;
    private ShoppingList mShoppingList = new ShoppingList();

    private String mUPC, mLastDatePurchased, mName, mPrice, mCategory;
    int mQuantity, mLastQuantity;
    double mPriceValue, mSubtotal, mPriority;

    Context context;
    ShoppingListDbHelper mShoppingListDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scanBtn = (Button) findViewById(R.id.scan_button);
        storeBtn = (Button) findViewById(R.id.store_button);
        minusBtn = (Button) findViewById(R.id.minusButton);
        plusBtn = (Button) findViewById(R.id.plusButton);
        displayBtn = (Button) findViewById(R.id.display_button);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        quantityEditText = (EditText) findViewById(R.id.quantityEditText);
        productNameEditText = (EditText) findViewById(R.id.productNameEditText);
        priceEditText = (EditText) findViewById(R.id.priceEditText);

        // Get the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_hamburger);
        getSupportActionBar().setTitle(TAG);
        toolbar.setSubtitle("PCorletto 2016");

        // Variables for the HTTP Request

        Log.d(TAG, "Main UI code is running!");


        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId()==R.id.scan_button){
                    //scan

                    IntentIntegrator scanIntegrator = new IntentIntegrator(ScanActivity.this);
                    scanIntegrator.initiateScan();

                }

            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mQuantity<=0){

                    mQuantity=1;

                }

                else{

                    mQuantity--;
                    if(mQuantity<=0){
                        mQuantity=1;
                    }

                }

                quantityEditText.setText(mQuantity+"");
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mQuantity++;

                quantityEditText.setText(mQuantity+"");
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                mPriority = Double.parseDouble((String.valueOf(rating)));


            }
        });

        storeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mQuantity = Integer.parseInt(quantityEditText.getText().toString());

                mSubtotal = mQuantity * mPriceValue;

                addItem();

                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Initialize shopping item

        mShoppingItem = new ShoppingItem();

        //Initialize UserDBHelper and SQLiteDB

        mShoppingListDbHelper = new ShoppingListDbHelper(getApplicationContext());
        sqLiteDatabase = mShoppingListDbHelper.getReadableDatabase();

        cursor = mShoppingListDbHelper.getShoppingItem(sqLiteDatabase);


        // Initialize the Row Number

        mRowNumber = 0;

        if(cursor.moveToFirst()){

            do{

                double price;

                mUPC = cursor.getString(0);
                mQuantity = cursor.getInt(1);
                mLastQuantity = cursor.getInt(2);
                mLastDatePurchased = cursor.getString(3);
                mName = cursor.getString(4);
                mPriority = cursor.getDouble(5);
                price = cursor.getDouble(6);
                mCategory = cursor.getString(7);

                mSubtotal = mQuantity * price;

                mShoppingItem = new ShoppingItem(mUPC, mQuantity, mLastQuantity, mLastDatePurchased,
                        mName, mPriority, price, mCategory, mSubtotal);

                mShoppingItem.setSubtotal(mSubtotal);

                mShoppingList.addShoppingItem(mShoppingItem, mRowNumber);

                mRowNumber++;

            }

            while(cursor.moveToNext());

        }

        mQuantity = 1;

        displayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ScanActivity.this, DisplayListActivity.class);

                // Next, I will pass in the array of shopping items, mShoppingList, a "ShoppingList" object
                // to DisplayShoppingList.java


                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);

                intent.putExtra(getString(R.string.shopping_list), mShoppingList.mShoppingItems);

                startActivity(intent);
            }
        });

    }

    private void getScannedItemInfo(String upc) {

        // API Key for Walmart

        String apiKey = BuildConfig.WALMART_API_KEY;

        String itemLookUpUrl = "http://api.walmartlabs.com/v1/items?apiKey=" +
                apiKey + "&upc=" + upc;


        if(isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(itemLookUpUrl)
                    .build();


            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ScanActivity.this, "Error", Toast.LENGTH_LONG).show();

                        }
                    });
                    alertUserAboutError();

                }

                @Override
                public void onResponse(final Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                    try {
                        final String jsonData = response.body().string();


                        Log.v(TAG, jsonData);

                        if (response.isSuccessful()) {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        JSONObject mainObject = new JSONObject(jsonData);
                                        JSONArray items = mainObject.getJSONArray("items");

                                        for(int i=0; i<items.length(); i++){
                                            JSONObject item = items.getJSONObject(i);
                                            mName = item.getString("name");
                                            mPrice = item.getString("salePrice");
                                            mCategory = item.getString("categoryPath");

                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                    }

                                    mShoppingItem.setProductName(mName);
                                    productNameEditText.setText(mName);

                                    if(mPrice==null){
                                        Toast.makeText(ScanActivity.this, "Price unavailable",Toast.LENGTH_LONG).show();;
                                    }

                                    else {

                                        mShoppingItem.setItemPrice(Double.parseDouble(mPrice));
                                        mQuantity = 1;
                                        quantityEditText.setText(mQuantity+"");
                                        priceEditText.setText(mPrice);
                                        mPriceValue = Double.parseDouble(mPrice);
                                        mSubtotal = mPriceValue * mQuantity;
                                        mLastQuantity=0; //Set it to 0 for now. Later, let the user enter it
                                        //mPriority=1; // Set it to 1 for now, later add a continuum or slider or
                                        // radio group to set priority, from 1 to 3
                                        mLastDatePurchased="01/01/1980"; // Set it to this for now, later let
                                        // user enter it from a calendar widget or edittext.

                                    }


                                }




                            });

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught:", e);
                    }

                }
            });

        }

        else{

            Toast.makeText(this, getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }

        return isAvailable;

    }

    private void alertUserAboutError() {

        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a result

            String scanContent = scanningResult.getContents();

            mUPC = scanContent;

            mShoppingItem.setUPC(mUPC);

            getScannedItemInfo(mUPC);

        }

        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void addItem() {

        context = this;

        // Perform DB insertion...

        // Initialize shoppingDBhelper object and SQLiteDatabase object.

        mShoppingListDbHelper = new ShoppingListDbHelper(context);
        sqLiteDatabase = mShoppingListDbHelper.getWritableDatabase();

        // Insert the item details in the database
        mShoppingListDbHelper.addItem(mUPC, mQuantity, mLastQuantity, mLastDatePurchased, mName,
                mPriority, mPriceValue, mCategory, mSubtotal, sqLiteDatabase);

        Toast.makeText(ScanActivity.this, "Shopping Item Saved", Toast.LENGTH_LONG).show();

        mShoppingListDbHelper.close();

        finish(); // Go back to Main Activity

    }

}
