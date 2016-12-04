package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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

/**
 * Created by hernandez on 11/25/2016.
 */
public class FooterFragment extends Fragment{

    ShoppingListDbHelper shoppingListDbHelper;
    ShoppingCartDbHelper shoppingCartDbHelper;
    SQLiteDatabase sqLiteDatabase;

    private ShoppingList mShoppingList = new ShoppingList();

    private int mRowNumber;

    ReloadCartFromDB reloadedCart = new ReloadCartFromDB();

    List<ShoppingItem> listDataHeader;
    HashMap<ShoppingItem, List<ShoppingItem>> listDataChild;


    FloatingActionButton payFAB;

    public TextView quantityTextView;
    public TextView subtotalTextView;
    public TextView salesTaxTextView;
    public TextView totalTextView;

    // Radio group, radio buttons go here...
    public String paymentMethod;

    public RadioGroup paymentGroup;
    public RadioButton cashRadioButton;
    public RadioButton debitRadioButton;
    public RadioButton creditRadioButton;

    public String summary;

    public TextView lastFourDigitsTextView;
    public EditText lastFourDigitsEditText;

    public int lastFourDigits;

    public FooterFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

        cashRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                paymentMethod = "Cash";

                // Set the dummy value 9999 for the EditText
                lastFourDigitsEditText.setText("9999");

                // Clear any previous radio button selections
                paymentGroup.clearCheck();

                // Hide the last 4-digits textbox, since the user is paying cash
                lastFourDigitsTextView.setVisibility(View.INVISIBLE);
                lastFourDigitsEditText.setVisibility(View.INVISIBLE);

            }
        });

        debitRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                paymentMethod = "Debit";

                // Clear any previous radio button selections
                paymentGroup.clearCheck();

                // Display the last four digits if they were already invisible
                if(lastFourDigitsEditText.getVisibility()==View.INVISIBLE){
                    lastFourDigitsTextView.setVisibility(View.VISIBLE);
                    lastFourDigitsEditText.setVisibility(View.VISIBLE);
                }

            }
        });

        creditRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                paymentMethod = "Credit";

                // Clear any previous radio button selections
                paymentGroup.clearCheck();

                // Display the last four digits if they were already invisible
                if(lastFourDigitsEditText.getVisibility()==View.INVISIBLE){
                    lastFourDigitsTextView.setVisibility(View.VISIBLE);
                    lastFourDigitsEditText.setVisibility(View.VISIBLE);
                }

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


                if(paymentMethod=="Cash") { // No last four digits
                    // In the next line, the store name and the location will be obtained using Google Maps, later...
                    summary += "Paid by: " + paymentMethod + ".\n" +
                            "Store: ABCDEFGH Store" + "\n" + "Location: AnyTown, CA";
                }

                else{ // Last four digits displayed
                    // In the next line, the store name and the location will be obtained using Google Maps, later...
                    summary += "Paid by: " + paymentMethod + ". Ending in " + lastFourDigitsString + "\n" +
                            "Store: ABCDEFGH Store" + "\n" + "Location: AnyTown, CA";
                }

                Toast.makeText(getContext(), summary, Toast.LENGTH_LONG).show();

                // Return to MainActivity
                Intent intent = new Intent(getContext().getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        return rootView;

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


}
