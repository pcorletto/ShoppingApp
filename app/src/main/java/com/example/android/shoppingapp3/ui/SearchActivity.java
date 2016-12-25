package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.android.shoppingapp3.R;

public class SearchActivity extends ActionBarActivity {

    public static String calling_activity_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);

        Intent intent = getIntent();
        calling_activity_name = intent.getStringExtra(getString(R.string.calling_activity_name));

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.searchbox, new SearchBoxFragment()).commit();

        }

        }

}
