package com.example.android.shoppingapp3.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.android.shoppingapp3.R;

public class PayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);


        if(savedInstanceState == null){

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.pay_list, new PayListFragment()).commit();

        }

    }

}
