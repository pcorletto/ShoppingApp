package com.example.android.shoppingapp3.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
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

    public SearchBoxFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_box, container, false);

        searchBox = (EditText) rootView.findViewById(R.id.searchBox);
        searchButton = (ImageButton) rootView.findViewById(R.id.searchButton);

        frag = new ResultsFoundFragment();
        fragTransaction = getFragmentManager().beginTransaction().add(R.id.container, frag);
        fragTransaction.commit();

        return rootView;
    }
}
