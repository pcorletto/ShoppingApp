package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by hernandez on 12/24/2016.
 */
public class SearchBoxFragment extends Fragment {

    Fragment frag;
    FragmentTransaction fragTransaction;
    EditText searchBox;
    ImageButton searchButton;

    private Toolbar toolbar;

    String calling_activity_name;

    public static String searchWord;

    public SearchBoxFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_box, container, false);

        // Get the toolbar
        toolbar = (Toolbar) rootView.findViewById(R.id.tool_bar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search");

        setHasOptionsMenu(true);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calling_activity_name = SearchActivity.calling_activity_name;

                if(calling_activity_name.equals("ui.DisplayListActivity")) {
                    Intent intent = new Intent(getContext(), DisplayListActivity.class);
                    startActivity(intent);
                }

                else{

                    Intent intent = new Intent(getContext(), DisplayCartActivity.class);
                    startActivity(intent);

                }
            }
        });

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

        if (id == R.id.action_settings){

            Intent intent1 = new Intent(getContext(), SettingsActivity.class);
            intent1.putExtra(getString(R.string.calling_activity_name), "ui.SearchActivity");
            startActivity(intent1);

        }

        if (id == R.id.action_home){

            Intent intent2  = new Intent(getContext(), MainActivity.class);
            startActivity(intent2);

        }

        if (id == R.id.action_list){

            Intent intent3 = new Intent(getContext(), DisplayListActivity.class);
            startActivity(intent3);

        }

        return super.onOptionsItemSelected(item);

    }

}
