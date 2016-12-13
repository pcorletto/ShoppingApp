package com.example.android.shoppingapp3.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.model.PurchaseItem;
import com.example.android.shoppingapp3.model.PurchaseItemAdapter;
import com.example.android.shoppingapp3.model.PurchaseList;
import com.example.android.shoppingapp3.model.ReloadPurchasesFromDB;

import java.util.ArrayList;
import java.util.List;

public class DisplayPurchaseActivity extends AppCompatActivity {

    private ListView listview;
    private List<PurchaseItem> list = new ArrayList<>();

    private PurchaseItemAdapter mAdapter;
    private PurchaseList mPurchaseList = new PurchaseList();

    ReloadPurchasesFromDB reloadedPurchases = new ReloadPurchasesFromDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_purchase);

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

        return super.onOptionsItemSelected(item);
    }
}
