package com.example.android.shoppingapp3.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
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

import com.example.android.shoppingapp3.BuildConfig;
import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.model.GooglePlacesReadTask;
import com.example.android.shoppingapp3.model.PurchaseDbHelper;
import com.example.android.shoppingapp3.model.ReloadCartFromDB;
import com.example.android.shoppingapp3.model.ShoppingCartDbHelper;
import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.model.ShoppingList;
import com.example.android.shoppingapp3.model.ShoppingListDbHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

    // Data structures for purchases list

    PurchaseDbHelper purchaseDbHelper;
    private int mPurchaseID;

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

    private String storeLocation;

    private String purchaseSummary, locationSummary;

    private String paidByString;

    private TextView lastFourDigitsTextView;
    private EditText lastFourDigitsEditText;

    private int lastFourDigits;

    MapView mMapView;
    private GoogleMap googleMap;

    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;

    private int PROXIMITY_RADIUS = 800;

    public FooterFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        purchaseSummary = "";

        locationSummary = "";

        storeLocation = "";

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

        getLocation();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        String storeType = "store";

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&name=" + storeType);
        googlePlacesUrl.append("&sensor=true");

        googlePlacesUrl.append("&key=" + BuildConfig.GOOGLE_API_KEY);

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                locationSummary = "";
                storeLocation = marker.getTitle();
                locationSummary += "\n" + "Store Location: " + storeLocation;
                return false;
            }
        });



        // Footer section:

        // Calculate the total item count, subtotal, total sales tax, and total

        int count = 0;
        double subtotal = 0;
        double tax = 0;
        double total;
        double addtax = 0;

        DecimalFormat df = new DecimalFormat("$0.00");

        // Initialize the purchaseSummary string. This string will hold a purchaseSummary of a list of
        // items purchased, quantities, unit prices, subtotals, total, and tax paid

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

            purchaseSummary += listDataHeader.get(i).getQuantity() + " " +
                    listDataHeader.get(i).getProductName() + " @ " +
                    df.format(listDataHeader.get(i).getItemPrice()) + " each = " +
                    df.format(listDataHeader.get(i).getSubtotal()) + "\n\n";

        }

        total = subtotal + tax;

        purchaseSummary += "Date: " + getCurrentDate() + "  " + getCurrentTime() +
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

                paidByString = "Paid by: " + paymentMethod;

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

                // Display the last four digits edit text if they were already invisible
                if (lastFourDigitsEditText.getVisibility() == View.INVISIBLE) {
                    lastFourDigitsTextView.setVisibility(View.VISIBLE);
                    lastFourDigitsEditText.setVisibility(View.VISIBLE);
                    lastFourDigitsEditText.setText("");

                }

                return false;
            }


        });


        payFAB = (FloatingActionButton) rootView.findViewById(R.id.payFAB);

        payFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                    paidByString =  "Paid by: " + paymentMethod;

                }

                else{ // Credit or debit

                    paidByString =  "Paid by: " + paymentMethod + " ending in " +
                            String.format("%04d", lastFourDigits);

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

                purchaseSummary += paidByString;

                // Save purchase purchaseSummary, purchase date and store location in purchase list
                addPurchaseItem(v);

                Toast.makeText(getContext(), "Thank you for Shopping!!!", Toast.LENGTH_LONG).show();

                // Go to Display Purchase Activity
                Intent intent = new Intent(getContext(), DisplayPurchaseActivity.class);
                startActivity(intent);

            }

        });

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.footer_fragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){

            Intent intent1 = new Intent(getContext(), SettingsActivity.class);
            intent1.putExtra(getString(R.string.calling_activity_name), "ui.PayActivity");
            startActivity(intent1);

        }

        if (id == R.id.action_home){

            Intent intent2  = new Intent(getContext(), MainActivity.class);
            startActivity(intent2);

        }

        if (id == R.id.action_list){

            Intent intent3 = new Intent(getContext(), DisplayPurchaseActivity.class);
            startActivity(intent3);

        }

        if (id == R.id.action_share){

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Summary of Purchase");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, purchaseSummary);
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));

        }

        return super.onOptionsItemSelected(item);

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

    public void addPurchaseItem(View view) {

        // Perform DB insertion...

        // Initialize purchaseDBhelper object and SQLiteDatabase object.

        purchaseDbHelper = new PurchaseDbHelper(getContext());
        sqLiteDatabase = purchaseDbHelper.getWritableDatabase();

        // Before inserting the item, retrieve the last value of mPurchaseID
        // which is stored in SharedPref file. If there isn't any previous value, assign zero.

        SharedPreferences sharedPreferences = getContext()
                .getSharedPreferences(getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
        mPurchaseID = sharedPreferences.getInt(getString(R.string.PURCHASE_ID), 0);

        // Add one to purchase ID

        mPurchaseID = mPurchaseID + 1;

        // Insert the item details in the database
        purchaseDbHelper.addItem(mPurchaseID, getCurrentDate(), storeLocation, purchaseSummary
                + "\n" + locationSummary, sqLiteDatabase);

        // Store new mPurchaseID in SharedPrefs file

        sharedPreferences = getContext()
                .getSharedPreferences(getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.PURCHASE_ID), mPurchaseID);
        editor.commit();

        purchaseDbHelper.close();

    }

    public static boolean isLocationEnabled(Context context)
    {
        //...............
        return true;
    }

    protected void getLocation() {
        if (isLocationEnabled(getContext())) {
            locationManager = (LocationManager)  getContext().getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }
            else{
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        }
        else
        {
            //prompt user to enable location....
            //.................
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Hey, a non null location! Sweet!

        //remove location callback:
        locationManager.removeUpdates(this);

        //open the map:
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //Toast.makeText(getContext(), "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}

