package com.example.android.shoppingapp3.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.shoppingapp3.BuildConfig;
import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.model.ReloadListFromDB;
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
    private RadioGroup radioGroup;
    private TextView productNameTextView, priceTextView, quantityTextView;
    public static final String TAG = ScanActivity.class.getSimpleName();

    private ShoppingItem mShoppingItem;
    private int mRowNumber;
    private ShoppingList mShoppingList = new ShoppingList();
    ReloadListFromDB reloadedList = new ReloadListFromDB();

    private String mUPC, mLastDatePurchased, mName, mPrice, mCategory, mImage, mTaxable;
    int mQuantity, mLastQuantity;
    double mPriceValue, mSubtotal, mPriority;
    boolean taxable;

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
        quantityTextView = (TextView) findViewById(R.id.quantityTextView);
        productNameTextView = (TextView) findViewById(R.id.productNameTextView);
        priceTextView = (TextView) findViewById(R.id.priceTextView);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        // Get the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        getSupportActionBar().setTitle(TAG);
        toolbar.setSubtitle("PCorletto 2016");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


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

                quantityTextView.setText(mQuantity + "");
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mQuantity++;

                quantityTextView.setText(mQuantity + "");
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

                String quantity = quantityTextView.getText().toString();

                // Check if quantity is empty. If no entry, then alert
                if (TextUtils.isEmpty(quantity)) {
                    quantityTextView.setError(getString(R.string.empty_quantity_alert));
                    Toast.makeText(ScanActivity.this, getString(R.string.empty_quantity_alert), Toast.LENGTH_LONG).show();
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    return;
                }

                mQuantity = Integer.parseInt(quantityTextView.getText().toString());



                mSubtotal = mQuantity * mPriceValue;

                if(radioGroup.getCheckedRadioButtonId() == -1){

                    // Alert user to check yes

                    Toast.makeText(ScanActivity.this, "Check YES or NO!",
                            Toast.LENGTH_LONG).show();
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    return;

                }

                String productName = productNameTextView.getText().toString();

                // Check if product name is empty. If no entry, then alert
                if (TextUtils.isEmpty(productName)) {
                    productNameTextView.setError(getString(R.string.empty_product_name_alert));
                    Toast.makeText(ScanActivity.this, getString(R.string.empty_product_name_alert), Toast.LENGTH_LONG).show();
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    return;
                }

                String price = priceTextView.getText().toString();

                // Check if price is empty. If no entry, then alert
                if (TextUtils.isEmpty(price)) {
                    priceTextView.setError(getString(R.string.empty_price_alert));
                    Toast.makeText(ScanActivity.this, getString(R.string.empty_price_alert), Toast.LENGTH_LONG).show();
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    return;
                }

                if(ratingBar.getRating()==0){

                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    Toast.makeText(ScanActivity.this, "Select one or more stars!", Toast.LENGTH_LONG).show();
                    return;

                }


                    else {

                    addItem();

                    Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                    startActivity(intent);

                }


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
                mImage = cursor.getString(9);
                mTaxable = cursor.getString(10);

                if(mTaxable.equals("true")){
                    taxable = true;
                }

                else{

                    taxable = false;
                }

                    mShoppingItem = new ShoppingItem(mUPC, mQuantity, mLastQuantity, mLastDatePurchased,
                            mName, mPriority, price, mCategory, mSubtotal, mImage, taxable);

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
                                            mImage = item.getString("largeImage");

                                        }

                                        Log.d("TEST", items.toString());


                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                    }

                                    mShoppingItem.setProductName(mName);
                                    productNameTextView.setText(mName);

                                    if(mPrice==null){
                                        Toast.makeText(ScanActivity.this, "Price unavailable",Toast.LENGTH_LONG).show();;
                                    }

                                    else {

                                        mShoppingItem.setItemPrice(Double.parseDouble(mPrice));
                                        mQuantity = 1;
                                        quantityTextView.setText(mQuantity + "");
                                        priceTextView.setText(mPrice);
                                        mPriceValue = Double.parseDouble(mPrice);
                                        mSubtotal = mPriceValue * mShoppingItem.getQuantity();
                                        mShoppingItem.setSubtotal(mSubtotal);
                                        mLastQuantity=0; //Set it to 0 for now. Later, let the user enter it
                                        //mPriority=1; // Set it to 1 for now, later add a continuum or slider or
                                        // radio group to set priority, from 1 to 3
                                        mLastDatePurchased="NEVER"; // Set it to this for now, later let
                                        // this be updated using the system's date, when the user shops.

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

        /*mShoppingListDbHelper.getShoppingItem(sqLiteDatabaseW);

                if(reloadedList.countFoundItems(mName, ScanActivity.this)==0) {

                    // Before adding the newly scanned item into the DB, check if it is already in the
                    // DB or not. If already there, do not add it again! If not there, add it.
                    else{*/



        context = this;

        // Perform DB insertion...

        // Initialize shoppingDBhelper object and SQLiteDatabase object.

        mShoppingListDbHelper = new ShoppingListDbHelper(context);
        sqLiteDatabase = mShoppingListDbHelper.getWritableDatabase();

        if(reloadedList.countFoundItems(mName, ScanActivity.this)==0) {

            // Before adding the newly scanned item into the DB, check if it is already in the
            // DB or not. If already there, do not add it again! If not there, add it.

            // Insert the item details in the database
            mShoppingListDbHelper.addItem(mUPC, mQuantity, mLastQuantity, mLastDatePurchased, mName,
                    mPriority, mPriceValue, mCategory, mSubtotal, mImage, mTaxable, sqLiteDatabase);

            Toast.makeText(ScanActivity.this, "Shopping Item Saved", Toast.LENGTH_LONG).show();

            mShoppingListDbHelper.close();

            finish(); // Go back to Main Activity
        }

        else{

            // Do not add it again.

            // Otherwise, alert the user that the item is already there and does not need to be added!

            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);

            Toast.makeText(this, mName + " is already in your shopping cart! Do not add it again!",
                    Toast.LENGTH_LONG).show();

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

}
