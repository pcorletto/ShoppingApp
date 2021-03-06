package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.shoppingapp3.R;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton scanImageButton, listImageButton, cartImageButton;
    private boolean backPressToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_hamburger);
        getSupportActionBar().setTitle(getString(R.string.shopping_app_title));


        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPrefs.getString(
                getString(R.string.pref_language_key),
                getString(R.string.pref_language_english));

        scanImageButton = (ImageButton) findViewById(R.id.scanImageButton);

        listImageButton =  (ImageButton) findViewById(R.id.listImageButton);

        cartImageButton = (ImageButton) findViewById(R.id.cartImageButton);

        scanImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });

        listImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this, DisplayListActivity.class);

                /*

                // Next, I will pass in the array of shopping items, mShoppingList, a ShoppingList object
                // to DisplayActivity.java


                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);

                intent.putExtra(getString(R.string.shopping_list), mShoppingList.mShoppingItems);

                */

                startActivity(intent);


            }
        });

        /*

        // Reload the cart from the SQLite Database.

        mShoppingCart = reloadedCart.reloadCartFromDB("get", searchItem, getApplicationContext());

        mRowNumber = reloadedCart.getListSize();

        */

        cartImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this, DisplayCartActivity.class);

                /*

                // Next, I will pass in the array of shopping items, mShoppingCar, a ShoppingList object
                // to DisplayActivity.java


                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);

                intent.putExtra(getString(R.string.shopping_cart), mShoppingCart.mShoppingItems);

                */

                startActivity(intent);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra(getString(R.string.calling_activity_name), "ui.MainActivity");
            intent.putExtra(getString(R.string.preceding_activity_name), "ui.MainActivity");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if (backPressToExit) {
            super.onBackPressed();
        }
        this.backPressToExit = true;
        Toast.makeText(this, getString(R.string.on_back_pressed_alert), Toast.LENGTH_SHORT).show();


        new Handler().postDelayed(new Runnable() {


            @Override


            public void run() {


                backPressToExit = false;
            }
        }, 2000);
    }
}
