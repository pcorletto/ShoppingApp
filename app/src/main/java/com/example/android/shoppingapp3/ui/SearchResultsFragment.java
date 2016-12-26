package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.adapters.ExpandableListAdapter;
import com.example.android.shoppingapp3.model.ReloadCartFromDB;
import com.example.android.shoppingapp3.model.ReloadListFromDB;
import com.example.android.shoppingapp3.model.ShoppingCartDbHelper;
import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.model.ShoppingList;
import com.example.android.shoppingapp3.model.ShoppingListDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hernandez on 12/24/2016.
 */
public class SearchResultsFragment extends Fragment {

    // Data structures

    private ShoppingList mShoppingList = new ShoppingList();

    private int mRowNumber;

    ReloadListFromDB reloadedList = new ReloadListFromDB();

    ReloadCartFromDB reloadedCart = new ReloadCartFromDB();


    // Added these for ExpandableListView extension...

    ExpandableListAdapter listAdapter;
    public static ExpandableListView expListView;
    List<ShoppingItem> listDataHeader;
    HashMap<ShoppingItem, List<ShoppingItem>> listDataChild;

    private Toolbar toolbar;

    ShoppingListDbHelper mShoppingListDbHelper;
    ShoppingCartDbHelper mShoppingCartDbHelper;
    SQLiteDatabase sqLiteDatabase;

    private String mUPC, mLastDatePurchased, mName, mPrice,
            mCategory, mImage;
    private boolean mTaxable;
    int mQuantity, mLastQuantity;
    double mPriceValue, mSubtotal, mPriority;

    String calling_activity_name;

    public String searchWord;

    View view;

    public FloatingActionButton myFAB;

    public SearchResultsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate
                (R.layout.fragment_search_results, container, false);

        // Get the expandable list view
        expListView = (ExpandableListView) view.findViewById
                (android.R.id.list);

        myFAB = (FloatingActionButton) view.findViewById(R.id.myFAB);

        listDataChild = new HashMap<ShoppingItem,
                List<ShoppingItem>>();

        calling_activity_name = com.example.android.shoppingapp3.ui.SearchActivity.calling_activity_name;

        myFAB.setVisibility(View.INVISIBLE);

        // Preparing list data

        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(),
                listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        if(calling_activity_name.equals("ui.DisplayListActivity")) {

            myFAB.setVisibility(View.VISIBLE);
            placeFAB();

        }

        return view;
    }

    private void prepareListData(){

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences
                        (getContext());

        String sortOrder = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));


        listDataHeader = new ArrayList<ShoppingItem>();

        listDataChild = new HashMap<ShoppingItem,
                List<ShoppingItem>>();

        // Get the list of shopping items from the database

        // Reload the list from the SQLite Database. Since we are not searching for anything
        // we will make searchItem just be blank.

                searchWord = SearchBoxFragment.searchWord;

        if(calling_activity_name.equals
                ("ui.DisplayListActivity")){

            mShoppingList = reloadedList.reloadListFromDB("search",
                    searchWord, getContext());

            mRowNumber = reloadedList.getListSize();

        }


        else{

            mShoppingList = reloadedCart.reloadCartFromDB
                    ("search", searchWord, getContext());

            mRowNumber = reloadedCart.getListSize();
        }

        for(int i = 0; i<mRowNumber; i++){

            listDataHeader.add(mShoppingList.getShoppingItem
                    (i));

        }

        // Loop through the array of shopping items

        // Adding child data:

        for(int i=0; i<mRowNumber; i++) {

            List<ShoppingItem> childrenItems = new
                    ArrayList<ShoppingItem>();

            childrenItems.add(mShoppingList.getShoppingItem(i));

            listDataChild.put(listDataHeader.get(i),
                    childrenItems);

        }

    }

    public void placeFAB(){

        FloatingActionButton myFAB = (FloatingActionButton) view.findViewById(R.id.myFAB);

        myFAB.setOnClickListener(new View.OnClickListener() {

                                     @Override
                                     public void onClick(View v) {

                                         {

                                             // For SQLiteDatabase: insert the item into ShoppingCartDB, if checked.

                                             // Initialize the shoppingCartDBHelper object

                                             mShoppingCartDbHelper = new ShoppingCartDbHelper(getContext());

                                             // Initialize the SQLiteDatabase object

                                             sqLiteDatabase = mShoppingCartDbHelper.getWritableDatabase();


                                             for (int i = 0; i < listDataHeader.size(); i++) {

                                                 if (listDataHeader.get(i).isSelected()) {

                                                     mUPC = listDataHeader.get(i).getUPC();
                                                     mQuantity = listDataHeader.get(i).getQuantity();
                                                     mLastQuantity = listDataHeader.get(i).getLastQuantity();
                                                     mName = listDataHeader.get(i).getProductName();
                                                     mPriority = listDataHeader.get(i).getPriority();
                                                     mPriceValue = listDataHeader.get(i).getItemPrice();
                                                     mCategory = listDataHeader.get(i).getCategory();
                                                     mSubtotal = listDataHeader.get(i).getSubtotal();
                                                     mLastDatePurchased = listDataHeader.get(i).getLastDatePurchased();
                                                     mImage = listDataHeader.get(i).getImage();
                                                     mTaxable = listDataHeader.get(i).isTaxable();

                                                     String taxable_string;

                                                     if (mTaxable) {
                                                         taxable_string = "true";
                                                     } else {
                                                         taxable_string = "false";
                                                     }

                                                     mShoppingCartDbHelper.getCartItem(sqLiteDatabase);

                                                     if(reloadedCart.countFoundItems(listDataHeader.get(i).getProductName(), getContext())==0) {

                                                         // Before inserting the checked item(s) into the Shopping Cart SQLite
                                                         // database, search for it(them) in the Shopping Cart SQLite. If not found,
                                                         // add it(them)


                                                         // Insert the shopping item into the Shopping Cart SQLite database

                                                         mShoppingCartDbHelper.addItem(mUPC, mQuantity, mLastQuantity, mLastDatePurchased, mName,
                                                                 mPriority, mPriceValue, mCategory, mSubtotal, mImage, taxable_string, sqLiteDatabase);

                                                         Intent intent = new Intent(getContext(), DisplayCartActivity.class);

                                                         startActivity(intent);
                                                     }

                                                     else{

                                                         // Otherwise, alert the user that the item is already there and does not need to be added!

                                                         ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                                                         toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);

                                                         Toast.makeText(getContext(), listDataHeader.get(i).getProductName() + " is already in your shopping cart! Do not add it again!",
                                                                 Toast.LENGTH_LONG).show();

                                                     }

                                                 }

                                             }


                                         }


                                     }
                                 }
        );

    }


}
