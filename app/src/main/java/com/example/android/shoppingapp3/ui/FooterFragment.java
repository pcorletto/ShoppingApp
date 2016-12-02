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

        View rootView = inflater.inflate(R.layout.footer_layout, container, false);

        quantityTextView = (TextView) rootView.findViewById(R.id.quantityTextView);
        subtotalTextView = (TextView)rootView.findViewById(R.id.subtotalTextView);
        salesTaxTextView = (TextView) rootView.findViewById(R.id.salesTaxTextView);
        totalTextView = (TextView) rootView.findViewById(R.id.totalTextView);
        // Radio Group, radio buttons go here ...

        paymentGroup = (RadioGroup) rootView.findViewById(R.id.paymentGroup);


        cashRadioButton = (RadioButton) rootView.findViewById(R.id.cashRadioButton);
        debitRadioButton = (RadioButton) rootView.findViewById(R.id.debitRadioButton);
        creditRadioButton = (RadioButton) rootView.findViewById(R.id.creditRadioButton);

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
                    df.format(listDataHeader.get(i).getItemPrice()) + " = " +
                    df.format(listDataHeader.get(i).getSubtotal()) + "\n";

        }

        total = subtotal + tax;

        summary += "\n" + "Subtotal: " + df.format(subtotal) + "\n" +
                "Tax: " + df.format(tax) + "\n" + "Total: " + df.format(total) + "\n";

        quantityTextView.setText(count+"");
        subtotalTextView.setText(df.format(subtotal));
        salesTaxTextView.setText(df.format(tax));
        totalTextView.setText(df.format(total));

        cashRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                paymentMethod = "cash";
            }
        });

        debitRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                paymentMethod = "debit";
            }
        });

        creditRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                paymentMethod = "credit";
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
                lastFourDigits = Integer.parseInt(lastFourDigitsEditText.getText().toString());


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

                summary += "Paid by: " + paymentMethod + " ending in " + lastFourDigits;

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

    public void updateLastDatePurchased(String productName, String lastDatePurchased){

            shoppingListDbHelper = new ShoppingListDbHelper(getContext().getApplicationContext());
            sqLiteDatabase = shoppingListDbHelper.getWritableDatabase();
            shoppingListDbHelper.updateLastDatePurchased(productName, lastDatePurchased, sqLiteDatabase);

    }


}
