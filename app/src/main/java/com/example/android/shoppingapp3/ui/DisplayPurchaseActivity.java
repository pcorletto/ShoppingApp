package com.example.android.shoppingapp3.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.model.PurchaseDbHelper;
import com.example.android.shoppingapp3.model.PurchaseItem;
import com.example.android.shoppingapp3.model.PurchaseItemAdapter;
import com.example.android.shoppingapp3.model.PurchaseList;
import com.example.android.shoppingapp3.model.ReloadPurchasesFromDB;

import java.util.ArrayList;
import java.util.List;

public class DisplayPurchaseActivity extends AppCompatActivity {

    private ListView listview;
    private List<PurchaseItem> list = new ArrayList<>();

    private Toolbar toolbar;

    private PurchaseItemAdapter mAdapter;
    private PurchaseDbHelper purchaseDbHelper;
    private PurchaseList mPurchaseList = new PurchaseList();

    private SQLiteDatabase sqLiteDatabase;

    ReloadPurchasesFromDB reloadedPurchases = new ReloadPurchasesFromDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_purchase);

        // Get the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        getSupportActionBar().setTitle("Purchases");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayPurchaseActivity.this, PayActivity.class);
                startActivity(intent);
            }
        });


        listview = (ListView) findViewById(android.R.id.list);

        String searchItem = "";

        mPurchaseList = reloadedPurchases.reloadPurchasesFromDB("get", searchItem, getApplicationContext());

        for(int i=0; i<reloadedPurchases.getListSize(); i++){

            list.add(mPurchaseList.mPurchaseItem[i]);

        }

            mAdapter = new PurchaseItemAdapter(DisplayPurchaseActivity.this, list);

            mAdapter.notifyDataSetChanged();

            listview.setAdapter(mAdapter);

        }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_purchase, menu);
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
            return true;
        }

        if (id == R.id.action_home){

            Intent intent = new Intent(DisplayPurchaseActivity.this, MainActivity.class);
            startActivity(intent);

        }

        if (id == R.id.action_delete){

            // Initialize the purchaseDBHelper object

            purchaseDbHelper = new PurchaseDbHelper(getApplicationContext());

            // Initialize the SQLiteDatabase object

            sqLiteDatabase = purchaseDbHelper.getReadableDatabase();


            for(int i=0; i<list.size(); i++) {

                if(list.get(i).isSelected()){

                    // For SQLiteDatabase: Delete this item here, if checked.

                    String item_for_DB_deletion = list.get(i).getPurchaseID() + "";

                    // Delete the purchase item from the SQLite database

                    purchaseDbHelper.deletePurchaseItem(item_for_DB_deletion, sqLiteDatabase);

                    Intent intent = new Intent(DisplayPurchaseActivity.this, PayActivity.class);
                    startActivity(intent);

                }

            }

            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
