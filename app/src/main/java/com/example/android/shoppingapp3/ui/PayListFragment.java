package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.adapters.ExpandablePayAdapter;
import com.example.android.shoppingapp3.model.ReloadCartFromDB;
import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.model.ShoppingList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hernandez on 11/25/2016.
 */
public class PayListFragment extends Fragment {

    // Data structures

    private ShoppingList mShoppingList = new ShoppingList();

    private int mRowNumber;

    ReloadCartFromDB reloadedCart = new ReloadCartFromDB();


    // Added these for ExpandableListView extension...

    ExpandablePayAdapter listAdapter;
    public static ExpandableListView expListView;
    List<ShoppingItem> listDataHeader;
    HashMap<ShoppingItem, List<ShoppingItem>> listDataChild;

    private Toolbar toolbar;

    //FloatingActionButton payFAB;

    Fragment frag;
    FragmentTransaction fragTransaction;

    public PayListFragment() {


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pay_list_fragment, container, false);

        // Get the toolbar
        toolbar = (Toolbar) view.findViewById(R.id.tool_bar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Pay");

        toolbar.setNavigationIcon(R.drawable.ic_back);

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        String language = sharedPrefs.getString(
                getString(R.string.pref_language_key),
                getString(R.string.pref_language_english));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DisplayCartActivity.class);
                startActivity(intent);
            }
        });

        // Get the expandable list view
        expListView = (ExpandableListView) view.findViewById(android.R.id.list);

        listDataChild = new HashMap<ShoppingItem, List<ShoppingItem>>();

        // Preparing list data

        prepareListData();

        listAdapter = new ExpandablePayAdapter(getContext(), listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        frag = new FooterFragment();
        fragTransaction = getFragmentManager().beginTransaction().add(R.id.container, frag);
        fragTransaction.commit();

        /*payFAB = (FloatingActionButton) view.findViewById(R.id.payFAB);

        payFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                frag = new FooterFragment();
                fragTransaction = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                fragTransaction.commit();

            }
        });*/

        return view;
    }

    private void prepareListData() {

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

            mShoppingList = reloadedCart.reloadCartFromDB("get", searchItem, getContext());

        }

        else if(sortOrder.equals("alphabetical")){

            mShoppingList = reloadedCart.reloadCartFromDB("sortByName", searchItem, getContext());
        }

        else if(sortOrder.equals("priority")){

            mShoppingList = reloadedCart.reloadCartFromDB("sortByPriority", searchItem, getContext());
        }

        mRowNumber = reloadedCart.getListSize();


        for (int i = 0; i < mRowNumber; i++) {

            listDataHeader.add(mShoppingList.getShoppingItem(i));

        }

        // Loop through the array of shopping items

        // Adding child data:

        for (int i = 0; i < mRowNumber; i++) {

            List<ShoppingItem> childrenItems = new ArrayList<ShoppingItem>();

            childrenItems.add(mShoppingList.getShoppingItem(i));

            listDataChild.put(listDataHeader.get(i), childrenItems);

        }

    }



}
