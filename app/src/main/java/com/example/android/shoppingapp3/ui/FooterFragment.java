package com.example.android.shoppingapp3.ui;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.model.ReloadCartFromDB;
import com.example.android.shoppingapp3.model.ShoppingCartDbHelper;
import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.model.ShoppingList;
import com.example.android.shoppingapp3.model.ShoppingListDbHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by hernandez on 11/25/2016.
 */
public class FooterFragment extends Fragment implements LocationListener{

    ShoppingListDbHelper shoppingListDbHelper;
    ShoppingCartDbHelper shoppingCartDbHelper;
    SQLiteDatabase sqLiteDatabase;

    private ShoppingList mShoppingList = new ShoppingList();

    private int mRowNumber;

    private ReloadCartFromDB reloadedCart = new ReloadCartFromDB();

    private List<ShoppingItem> listDataHeader;
    private HashMap<ShoppingItem, List<ShoppingItem>> listDataChild;


    private FloatingActionButton payFAB;

    private ShareActionProvider mShareActionProvider;

    private TextView quantityTextView;
    private TextView subtotalTextView;
    private TextView salesTaxTextView;
    private TextView totalTextView;

    // Radio group, radio buttons go here...
    private String paymentMethod;

    private RadioGroup paymentGroup;
    private RadioButton cashRadioButton;
    private RadioButton debitRadioButton;
    private RadioButton creditRadioButton;

    private String storeName, storeLocation;

    private String summary;

    private String paidByString;

    private TextView lastFourDigitsTextView;
    private EditText lastFourDigitsEditText;

    private int lastFourDigits;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String lat;
    String provider;
    protected double latitude,longitude;
    protected boolean gps_enabled,network_enabled;

    private static final String LOG_TAG = FooterFragment.class.getSimpleName();

    public FooterFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        summary = "";

        paidByString = "";

        setHasOptionsMenu(true);

        listDataChild = new HashMap<ShoppingItem, List<ShoppingItem>>();

        // Preparing list data

        prepareListData();

        final View rootView = inflater.inflate(R.layout.footer_layout, container, false);

        quantityTextView = (TextView) rootView.findViewById(R.id.quantityTextView);
        subtotalTextView = (TextView)rootView.findViewById(R.id.subtotalTextView);
        salesTaxTextView = (TextView) rootView.findViewById(R.id.salesTaxTextView);
        totalTextView = (TextView) rootView.findViewById(R.id.totalTextView);
        // Radio Group, radio buttons go here ...

        paymentGroup = (RadioGroup) rootView.findViewById(R.id.paymentGroup);


        cashRadioButton = (RadioButton) rootView.findViewById(R.id.cashRadioButton);
        debitRadioButton = (RadioButton) rootView.findViewById(R.id.debitRadioButton);
        creditRadioButton = (RadioButton) rootView.findViewById(R.id.creditRadioButton);

        lastFourDigitsTextView = (TextView) rootView.findViewById(R.id.lastFourDigitsTextView);
        lastFourDigitsEditText = (EditText) rootView.findViewById(R.id.lastFourDigitsEditText);

        // This section is for the Location sensor:
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        // Footer section:

        // Calculate the total item count, subtotal, total sales tax, and total

        int count = 0;
        double subtotal = 0;
        double tax = 0;
        double total;
        double addtax = 0;

        DecimalFormat df = new DecimalFormat("$0.00");

        // Initialize the summary string. This string will hold a summary of a list of
        // items purchased, quantities, unit prices, subtotals, total, and tax paid

        summary = "";
        paidByString = "";

        for(int i=0; i<listDataHeader.size(); i++){

            count = count + listDataHeader.get(i).getQuantity();
            subtotal = subtotal + listDataHeader.get(i).getSubtotal();
            if(listDataHeader.get(i).isTaxable()){

                addtax = listDataHeader.get(i).getSubtotal() * 0.07;
                tax = tax + addtax;

            }

            else{

                addtax = 0;
                tax = tax + addtax;
            }

            summary += listDataHeader.get(i).getQuantity() + " " +
                    listDataHeader.get(i).getProductName() + " @ " +
                    df.format(listDataHeader.get(i).getItemPrice()) + " each = " +
                    df.format(listDataHeader.get(i).getSubtotal()) + "\n\n";

        }

        total = subtotal + tax;

        summary += "Date: " + getCurrentDate() + "  " + getCurrentTime() +
                "\n" + "Subtotal: " + df.format(subtotal) + "\n" +
                "Tax: " + df.format(tax) + "\n" + "Total: " + df.format(total) + "\n";

        quantityTextView.setText(count+"");
        subtotalTextView.setText(df.format(subtotal));
        salesTaxTextView.setText(df.format(tax));
        totalTextView.setText(df.format(total));

        cashRadioButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Clear any previous radio button selections
                paymentGroup.clearCheck();

                paymentMethod = "Cash";

                paidByString = "\nPaid by: " + paymentMethod;

                // Set the last four digits on the edit text to a dummy value of 9999
                lastFourDigitsEditText.setText("9999");

                // Hide the last 4-digits text box, since the user is paying cash
                lastFourDigitsTextView.setVisibility(View.INVISIBLE);
                lastFourDigitsEditText.setVisibility(View.INVISIBLE);
                return false;
            }
        });


        debitRadioButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // Clear any previous radio button selections
                paymentGroup.clearCheck();

                paymentMethod = "Debit";

                paidByString = "\nPaid by: " + paymentMethod + " ending in: " + lastFourDigits;

                // Display the last four digits edit text if they were already invisible
                if (lastFourDigitsEditText.getVisibility() == View.INVISIBLE) {
                    lastFourDigitsTextView.setVisibility(View.VISIBLE);
                    lastFourDigitsEditText.setVisibility(View.VISIBLE);
                    lastFourDigitsEditText.setText("");

                }

                return false;
            }

        });

        creditRadioButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                
                // Clear any previous radio button selections
                paymentGroup.clearCheck();

                paymentMethod = "Credit";

                paidByString = "\nPaid by: " + paymentMethod + " ending in: " + lastFourDigits;

                // Display the last four digits edit text if they were already invisible
                if (lastFourDigitsEditText.getVisibility() == View.INVISIBLE) {
                    lastFourDigitsTextView.setVisibility(View.VISIBLE);
                    lastFourDigitsEditText.setVisibility(View.VISIBLE);
                    lastFourDigitsEditText.setText("");

                }

                return false;
            }


        });

        storeLocation = getCompleteAddressString(latitude, longitude);

        summary += "\n" + "Store Location: " + storeLocation;

        payFAB = (FloatingActionButton) rootView.findViewById(R.id.payFAB);

        payFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Fetch new store location when FAB button clicked
                storeLocation = getCompleteAddressString(latitude, longitude);

                if(paymentGroup.getCheckedRadioButtonId() == -1){

                    // Alert user to check one payment type

                    Toast.makeText(getContext(), "Select one payment type!",
                            Toast.LENGTH_LONG).show();
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    return;

                }

                // Capture the count, subtotal, sales tax, total, payment method (cash, debit or credit)
                // and the last 4 digits entered by the user. Then, store all these data in the
                // PURCHASE TABLE of the database so that a record of purchases can be kept

                // Get the last four digits from the user
                String lastFourDigitsString = lastFourDigitsEditText.getText().toString();

                // Check if last four digits are empty. If no entry, then alert
                if (TextUtils.isEmpty(lastFourDigitsString)) {
                    lastFourDigitsEditText.setError(getString(R.string.empty_four_digits_alert));
                    Toast.makeText(getContext(), getString(R.string.empty_four_digits_alert), Toast.LENGTH_LONG).show();
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    return;
                }

                // Check if last four digits are not exactly 4 digits long. Then alert
                if ((lastFourDigitsString.length()!=4)){

                    lastFourDigitsEditText.setError(getString(R.string.not_four_digits_alert));
                    Toast.makeText(getContext(), getString(R.string.not_four_digits_alert), Toast.LENGTH_LONG).show();
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    return;
                }

                // Check if the user enter only digits. If not, alert
                for(int i=0; i<lastFourDigitsString.length(); i++){

                    if (!Character.isDigit(lastFourDigitsString.charAt(i))) {
                        lastFourDigitsEditText.setError(getString(R.string.not_digits_alert));
                        Toast.makeText(getContext(), getString(R.string.not_digits_alert), Toast.LENGTH_LONG).show();
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                        return;
                    }
                }

                lastFourDigits = Integer.parseInt(lastFourDigitsString);

                // If paid by cash, do not show any last four digits. If paid by credit or debit,
                // show last four digits:

                if(paymentMethod.equals("Cash")){

                    paidByString = "\nPaid by: " + paymentMethod;

                }

                else{ // Credit or debit

                paidByString = "\nPaid by: " + paymentMethod + " ending in: " + lastFourDigits;

                }

                // Traverse all the items in the shopping cart using a loop

                // Update all those items, in the Shopping LIST database, not on the
                // Shopping Cart DB. The update will include the last date purchased
                // with will be obtained using the System's Date

                for(int i=0; i<listDataHeader.size(); i++){

                    updateLastDatePurchased(listDataHeader.get(i).getProductName(),
                            getCurrentDate());

                }

                // Delete all the items from the Shopping CART database.

                // Initialize the shoppingCartDBHelper object

                shoppingCartDbHelper = new ShoppingCartDbHelper(getContext().getApplicationContext());

                // Initialize the SQLiteDatabase object

                sqLiteDatabase = shoppingCartDbHelper.getReadableDatabase();


                for(int i=0; i<listDataHeader.size(); i++) {


                        // Delete the cart item from the SQLite database

                        shoppingCartDbHelper.deleteCartItem(listDataHeader.get(i).getProductName(), sqLiteDatabase);

                }

                // Play a "Thank you for Shopping" sound.

                MediaPlayer player = MediaPlayer.create(getContext().getApplicationContext(), R.raw.thankyou);
                player.start();

                Toast.makeText(getContext(), summary + "\n" + paidByString, Toast.LENGTH_LONG).show();


            }

        });

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.footer_fragment, menu);

        // Retrieve the share menu intent
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent
        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {

            mShareActionProvider.setShareIntent(createShareForecastIntent());

        } else {

            Log.d(LOG_TAG, "Share Action Provider is null?");

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                // do stuff
                Intent intent1 = new Intent(getContext(), SettingsActivity.class);
                intent1.putExtra(getString(R.string.calling_activity_name), "ui.PayActivity");
                startActivity(intent1);
                return true;

            case R.id.action_home:
                Intent intent2  = new Intent(getContext(), MainActivity.class);
                startActivity(intent2);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent(){

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Summary of Purchase");
        shareIntent.putExtra(Intent.EXTRA_TEXT, summary + paidByString);

        return shareIntent;

    }



    private void prepareListData(){


        listDataHeader = new ArrayList<ShoppingItem>();

        listDataChild = new HashMap<ShoppingItem, List<ShoppingItem>>();

        // Get the list of shopping items from the database

        // Reload the list from the SQLite Database. Since we are not searching for anything
        // we will make searchItem just be blank.

        String searchItem = "";

        mShoppingList = reloadedCart.reloadCartFromDB("get", searchItem, getContext());

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

    public String getCurrentDate(){

        Calendar ci = Calendar.getInstance();

        // Add one to the number of the month, because in Java, January is represented
        // using zero.

        String formattedMonth = String.format("%02d", ci.get(Calendar.MONTH)+1 );
        String formattedDay = String.format("%02d", ci.get(Calendar.DAY_OF_MONTH));
        String formattedYear = String.format("%04d", ci.get(Calendar.YEAR));

        String currentDate = formattedMonth + "/" + formattedDay + "/" + formattedYear;

        return currentDate;


    }

    public String getCurrentTime(){

        Calendar ci = Calendar.getInstance();

        String formattedHour, timeSuffix;

        if(ci.get(Calendar.HOUR_OF_DAY)>12){

            int non_military_hour = ci.get(Calendar.HOUR_OF_DAY)%12;
            formattedHour = String.format("%02d", non_military_hour);
            timeSuffix = "PM";
        }

        else{

            formattedHour = String.format("%02d", ci.get(Calendar.HOUR_OF_DAY));
            timeSuffix = "AM";

        }

        String formattedMinute = String.format("%02d", ci.get(Calendar.MINUTE));


        String currentTime = formattedHour + ":" + formattedMinute + " " + timeSuffix;

        return currentTime;


    }

    public void updateLastDatePurchased(String productName, String lastDatePurchased){

            shoppingListDbHelper = new ShoppingListDbHelper(getContext().getApplicationContext());
            sqLiteDatabase = shoppingListDbHelper.getWritableDatabase();
            shoppingListDbHelper.updateLastDatePurchased(productName, lastDatePurchased, sqLiteDatabase);

    }


    @Override
    public void onLocationChanged(Location location) {

            latitude = location.getLatitude();
            longitude = location.getLongitude();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Log.d("Latitude","status");
    }

    @Override
    public void onProviderEnabled(String provider) {

        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(String provider) {

        Log.d("Latitude","disable");

    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");

                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current location address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location address", "Cannot get Address!");
        }
        return strAdd;
    }
}
