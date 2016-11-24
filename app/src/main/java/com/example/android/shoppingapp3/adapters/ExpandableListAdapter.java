package com.example.android.shoppingapp3.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.android.shoppingapp3.R;
import com.example.android.shoppingapp3.model.ShoppingCartDbHelper;
import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.model.ShoppingListDbHelper;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;


/**
 * Created by hernandez on 10/9/2016.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<ShoppingItem> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<ShoppingItem, List<ShoppingItem>> _listDataChild;

    ShoppingListDbHelper shoppingListDbHelper;
    ShoppingCartDbHelper shoppingCartDbHelper;
    SQLiteDatabase sqLiteDatabase;

    private String calling_activity_name;

    public ExpandableListAdapter(Context context, List<ShoppingItem> listDataHeader,
                                 HashMap<ShoppingItem, List<ShoppingItem>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        final ShoppingItem headerTitle = (ShoppingItem) getGroup(groupPosition);

        calling_activity_name = _context.getClass().getName().toString();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);

        }

        final TextView quantityTextView = (TextView) convertView.
                findViewById(R.id.quantityTextView);
        quantityTextView.setText(headerTitle.getQuantity()+"");

        final TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.getProductName());

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

        Button increaseButton = (Button) convertView.findViewById(R.id.increaseButton);
        Button decreaseButton = (Button) convertView.findViewById(R.id.decreaseButton);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                if(isChecked ){

                    _listDataHeader.get(groupPosition).setSelected(true);

                }else {

                    _listDataHeader.get(groupPosition).setSelected(false);

                }

            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = headerTitle.getQuantity();
                newQuantity++;
                headerTitle.setQuantity(newQuantity);
                quantityTextView.setText(newQuantity+"");

                double newSubtotal = newQuantity * headerTitle.getItemPrice();

                // Update quantity and subtotal in SQLite DB...
                updateSubtotal(headerTitle.getProductName(), newQuantity+"", newSubtotal+"");

                if(calling_activity_name.equals("com.example.android.shoppingapp3.ui.DisplayListActivity")){

                    // If the group view was already expanded, collapse it, and expand it again,
                    // so that the data in the child view can be refreshed.

                    if(com.example.android.shoppingapp3.ui.DisplayListActivity.expListView.isGroupExpanded(groupPosition)){

                        com.example.android.shoppingapp3.ui.DisplayListActivity.expListView.collapseGroup(groupPosition);
                        com.example.android.shoppingapp3.ui.DisplayListActivity.expListView.expandGroup(groupPosition);

                    }

                }

                else if(calling_activity_name.equals("com.example.android.shoppingapp3.ui.DisplayCartActivity")) {

                    // If the group view was already expanded, collapse it, and expand it again,
                    // so that the data in the child view can be refreshed.

                    if(com.example.android.shoppingapp3.ui.DisplayCartActivity.expListView.isGroupExpanded(groupPosition)){

                        com.example.android.shoppingapp3.ui.DisplayCartActivity.expListView.collapseGroup(groupPosition);
                        com.example.android.shoppingapp3.ui.DisplayCartActivity.expListView.expandGroup(groupPosition);

                    }


                }

            }
        });

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = headerTitle.getQuantity();
                newQuantity--;
                if(newQuantity==0){
                    newQuantity=1;
                }
                headerTitle.setQuantity(newQuantity);
                quantityTextView.setText(newQuantity+"");

                double newSubtotal = newQuantity * headerTitle.getItemPrice();

                // Update quantity and subtotal in SQLite DB...
                updateSubtotal(headerTitle.getProductName(), newQuantity+"", newSubtotal+"");

                if(calling_activity_name.equals("com.example.android.shoppingapp3.ui.DisplayListActivity")){

                    // If the group view was already expanded, collapse it, and expand it again,
                    // so that the data in the child view can be refreshed.

                    if(com.example.android.shoppingapp3.ui.DisplayListActivity.expListView.isGroupExpanded(groupPosition)){

                        com.example.android.shoppingapp3.ui.DisplayListActivity.expListView.collapseGroup(groupPosition);
                        com.example.android.shoppingapp3.ui.DisplayListActivity.expListView.expandGroup(groupPosition);

                    }

                }

                else if(calling_activity_name.equals("com.example.android.shoppingapp3.ui.DisplayCartActivity")) {

                    // If the group view was already expanded, collapse it, and expand it again,
                    // so that the data in the child view can be refreshed.

                    if(com.example.android.shoppingapp3.ui.DisplayCartActivity.expListView.isGroupExpanded(groupPosition)){

                        com.example.android.shoppingapp3.ui.DisplayCartActivity.expListView.collapseGroup(groupPosition);
                        com.example.android.shoppingapp3.ui.DisplayCartActivity.expListView.expandGroup(groupPosition);

                    }


                }

            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ShoppingItem childText = (ShoppingItem) getChild(groupPosition, childPosition);

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);

            holder.quantityTextView = (TextView) convertView.findViewById(R.id.quantityTextView);
            holder.itemPriceTextView = (TextView) convertView.findViewById(R.id.itemPriceTextView);
            holder.subtotalTextView = (TextView) convertView.findViewById(R.id.subtotalTextView);
            holder.categoryTextView = (TextView) convertView.findViewById(R.id.categoryTextView);
            holder.lastDatePurchasedTextView = (TextView) convertView.findViewById(R.id.lastDatePurchasedTextView);
            holder.priorityTextView = (TextView) convertView.findViewById(R.id.priorityTextView);
            holder.productImage = (WebView) convertView.findViewById(R.id.productImage);
            holder.taxableTextView = (TextView) convertView.findViewById(R.id.taxableTextView);

            convertView.setTag(holder);

        } else{

            holder = (ViewHolder) convertView.getTag();
        }

        DecimalFormat df = new DecimalFormat("$0.00");

        holder.quantityTextView.setText(childText.getQuantity()+"");
        holder.itemPriceTextView.setText(df.format(childText.getItemPrice()));
        holder.subtotalTextView.setText(df.format(childText.getQuantity()*childText.getItemPrice()));
        holder.categoryTextView.setText(childText.getCategory());
        holder.lastDatePurchasedTextView.setText(childText.getLastDatePurchased());

        holder.priorityTextView.setText(childText.getPriority()+"");

        if(childText.isTaxable()){
            holder.taxableTextView.setText("Yes");
        }

        else{
            holder.taxableTextView.setText("No");
        }

        holder.productImage.getSettings().setLoadsImagesAutomatically(true);
        holder.productImage.getSettings().setLoadWithOverviewMode(true);
        holder.productImage.getSettings().setUseWideViewPort(true);

        holder.productImage.loadUrl(childText.getImage());

        return convertView;

    }

    private static class ViewHolder{

        public TextView quantityTextView;
        public TextView itemPriceTextView;
        public TextView subtotalTextView;
        public TextView categoryTextView;
        public TextView lastDatePurchasedTextView;
        public TextView priorityTextView;
        public WebView productImage;
        public TextView taxableTextView;

    }

    public void updateSubtotal(String productName, String quantity, String subtotal){

        // If the calling activity is the DisplayListActivity, update the subtotal in the LIST DB
        // If the calling activity is the DisplayCartActivity, update the subtotal in the CART DB

        if(calling_activity_name.equals("com.example.android.shoppingapp3.ui.DisplayListActivity")){

            shoppingListDbHelper = new ShoppingListDbHelper(_context.getApplicationContext());
            sqLiteDatabase = shoppingListDbHelper.getWritableDatabase();
            shoppingListDbHelper.updateSubtotal(productName, quantity, subtotal, sqLiteDatabase);

        }

        else if(calling_activity_name.equals("com.example.android.shoppingapp3.ui.DisplayCartActivity")) {

            shoppingCartDbHelper = new ShoppingCartDbHelper(_context.getApplicationContext());
            sqLiteDatabase = shoppingCartDbHelper.getWritableDatabase();
            shoppingCartDbHelper.updateSubtotal(productName, quantity, subtotal, sqLiteDatabase);

        }


    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
