package com.example.android.shoppingapp3.ui;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

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
public class ListCartFragment extends Fragment {

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

    private String mUPC, mLastDatePurchased, mName, mPrice, mCategory, mImage;
    private boolean mTaxable;
    int mQuantity, mLastQuantity;
    double mPriceValue, mSubtotal, mPriority;

    String calling_activity_name;

    public ListCartFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        // Get the expandable list view
        expListView = (ExpandableListView) view.findViewById(android.R.id.list);

        listDataChild = new HashMap<ShoppingItem, List<ShoppingItem>>();

        calling_activity_name = com.example.android.shoppingapp3.ui.SearchActivity.calling_activity_name;

        // Preparing list data

        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);


        return view;
    }

    private void prepareListData(){

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());

        String sortOrder = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));


        listDataHeader = new ArrayList<ShoppingItem>();

        listDataChild = new HashMap<ShoppingItem, List<ShoppingItem>>();

        // Get the list of shopping items from the database

        // Reload the list from the SQLite Database. Since we are not searching for anything
        // we will make searchItem just be blank.

        String searchItem = "";

        if(sortOrder.equals("no sort")){

            if(calling_activity_name.equals("ui.DisplayListActivity")){

                mShoppingList = reloadedList.reloadListFromDB("get", searchItem, getContext());

            }


            else{

                mShoppingList = reloadedCart.reloadCartFromDB("get", searchItem, getContext());
            }

        }

        else if(sortOrder.equals("alphabetical")){

            if(calling_activity_name.equals("ui.DisplayListActivity")){

                mShoppingList = reloadedList.reloadListFromDB("sortByName", searchItem, getContext());

            }


            else{

                mShoppingList = reloadedCart.reloadCartFromDB("sortByName", searchItem, getContext());

            }

        }

        else if(sortOrder.equals("priority")){

            if(calling_activity_name.equals("ui.DisplayListActivity")){

                mShoppingList = reloadedList.reloadListFromDB("sortByPriority", searchItem, getContext());

            }

            else{

                mShoppingList = reloadedCart.reloadCartFromDB("sortByPriority", searchItem, getContext());
            }

        }


        if(calling_activity_name.equals("ui.DisplayListActivity")){

            mRowNumber = reloadedList.getListSize();

        }


        else{

            mRowNumber = reloadedCart.getListSize();
        }


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

}
