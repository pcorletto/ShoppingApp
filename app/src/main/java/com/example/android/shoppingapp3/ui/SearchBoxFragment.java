package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.android.shoppingapp3.R;
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
public class SearchBoxFragment extends Fragment {

    // Data structures

    private ShoppingList mShoppingList = new ShoppingList();

    private int mRowNumber;

    ReloadListFromDB reloadedList = new ReloadListFromDB();

    ReloadCartFromDB reloadedCart = new ReloadCartFromDB();

    Fragment frag;
    FragmentTransaction fragTransaction;
    EditText searchBox;
    ImageButton searchButton;

    List<ShoppingItem> listDataHeader;
    HashMap<ShoppingItem, List<ShoppingItem>> listDataChild;

    private Toolbar toolbar;

    String calling_activity_name;

    public static String searchWord;

    ShoppingListDbHelper mShoppingListDbHelper;
    ShoppingCartDbHelper mShoppingCartDbHelper;
    SQLiteDatabase sqLiteDatabase;

    public SearchBoxFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_box, container, false);

        calling_activity_name = SearchActivity.calling_activity_name;

        // Initialize searchWord
        searchWord = "";

        // Get the toolbar
        toolbar = (Toolbar) rootView.findViewById(R.id.tool_bar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search");

        setHasOptionsMenu(true);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (calling_activity_name.equals("ui.DisplayListActivity")) {
                    Intent intent = new Intent(getContext(), DisplayListActivity.class);
                    startActivity(intent);
                } else {

                    Intent intent = new Intent(getContext(), DisplayCartActivity.class);
                    startActivity(intent);

                }
            }
        });

        prepareListData();

        searchBox = (EditText) rootView.findViewById(R.id.searchBox);
        searchButton = (ImageButton) rootView.findViewById(R.id.searchButton);

        frag = new ListCartFragment();
        fragTransaction = getFragmentManager().beginTransaction().add(R.id.container, frag);
        fragTransaction.commit();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchWord = searchBox.getText().toString();
                frag = new SearchResultsFragment();
                fragTransaction = getFragmentManager().beginTransaction().replace(R.id.container, frag);
                fragTransaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.search_fragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            //noinspection SimplifiableIfStatement

            case R.id.action_settings: {

                calling_activity_name = SearchActivity.calling_activity_name;
                String preceding_activity_name = "ui.SearchActivity";
                Intent intent1 = new Intent(getContext(), SettingsActivity.class);
                intent1.putExtra(getString(R.string.calling_activity_name), calling_activity_name);
                intent1.putExtra(getString(R.string.preceding_activity_name), preceding_activity_name);
                startActivity(intent1);

                return true;

            }

            case R.id.action_home: {

                Intent intent2 = new Intent(getContext(), MainActivity.class);
                startActivity(intent2);

                return true;

            }

            case R.id.action_list: {

                Intent intent3 = new Intent(getContext(), DisplayListActivity.class);
                startActivity(intent3);

                return true;

            }

            case R.id.action_delete: {

                // If we have just searched for some item, searchWord will have a value
                // In that case, call listDataHeader and listDataChild from SearchResultsFragment

                if (!(searchWord.equals(""))){ // This means we are deleting one or more of the search results

                    listDataHeader = SearchResultsFragment.listDataHeader;

                    listDataChild = SearchResultsFragment.listDataChild;

                    if (calling_activity_name.equals("ui.DisplayListActivity")) {

                        // Initialize the shoppingListDBHelper object

                        mShoppingListDbHelper = new ShoppingListDbHelper(getContext());

                        // Initialize the SQLiteDatabase object

                        sqLiteDatabase = mShoppingListDbHelper.getReadableDatabase();


                        for (int i = 0; i < listDataHeader.size(); i++) {

                            if (listDataHeader.get(i).isSelected()) {

                                // For SQLiteDatabase: Delete this item here, if checked.

                                String item_for_DB_deletion = listDataHeader.get(i).getProductName() + "";

                                // Delete the shopping item from the SQLite database

                                mShoppingListDbHelper.deleteShoppingItem(item_for_DB_deletion, sqLiteDatabase);

                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);

                            }

                        }

                    } else if (calling_activity_name.equals("ui.DisplayCartActivity")) {

                        // Initialize the shoppingCartDBHelper object

                        mShoppingCartDbHelper = new ShoppingCartDbHelper(getContext());

                        // Initialize the SQLiteDatabase object

                        sqLiteDatabase = mShoppingCartDbHelper.getReadableDatabase();


                        for (int i = 0; i < listDataHeader.size(); i++) {

                            if (listDataHeader.get(i).isSelected()) {

                                // For SQLiteDatabase: Delete this item here, if checked.

                                String item_for_DB_deletion = listDataHeader.get(i).getProductName() + "";

                                // Delete the shopping item from the SQLite database

                                mShoppingCartDbHelper.deleteCartItem(item_for_DB_deletion, sqLiteDatabase);

                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);

                            }

                        }

                    }

                }

                else if(searchWord.equals("")){ // This means we are deleting, not from a search result.

                listDataHeader = ListCartFragment.listDataHeader;

                listDataChild = ListCartFragment.listDataChild;

                if (calling_activity_name.equals("ui.DisplayListActivity")) {

                    // Initialize the shoppingListDBHelper object

                    mShoppingListDbHelper = new ShoppingListDbHelper(getContext());

                    // Initialize the SQLiteDatabase object

                    sqLiteDatabase = mShoppingListDbHelper.getReadableDatabase();


                    for (int i = 0; i < listDataHeader.size(); i++) {

                        if (listDataHeader.get(i).isSelected()) {

                            // For SQLiteDatabase: Delete this item here, if checked.

                            String item_for_DB_deletion = listDataHeader.get(i).getProductName() + "";

                            // Delete the shopping item from the SQLite database

                            mShoppingListDbHelper.deleteShoppingItem(item_for_DB_deletion, sqLiteDatabase);

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);

                        }

                    }

                } else if (calling_activity_name.equals("ui.DisplayCartActivity")) {

                    // Initialize the shoppingCartDBHelper object

                    mShoppingCartDbHelper = new ShoppingCartDbHelper(getContext());

                    // Initialize the SQLiteDatabase object

                    sqLiteDatabase = mShoppingCartDbHelper.getReadableDatabase();


                    for (int i = 0; i < listDataHeader.size(); i++) {

                        if (listDataHeader.get(i).isSelected()) {

                            // For SQLiteDatabase: Delete this item here, if checked.

                            String item_for_DB_deletion = listDataHeader.get(i).getProductName() + "";

                            // Delete the shopping item from the SQLite database

                            mShoppingCartDbHelper.deleteCartItem(item_for_DB_deletion, sqLiteDatabase);

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);

                        }

                    }

                }

                }

                return true;

            }

            default:

                return super.onOptionsItemSelected(item);
        }

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

            mShoppingList = reloadedList.reloadListFromDB("get", searchItem, getContext());

        }

        else if(sortOrder.equals("alphabetical")){

            mShoppingList = reloadedList.reloadListFromDB("sortByName", searchItem, getContext());
        }

        else if(sortOrder.equals("priority")){

            mShoppingList = reloadedList.reloadListFromDB("sortByPriority", searchItem, getContext());
        }



        mRowNumber = reloadedList.getListSize();


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
