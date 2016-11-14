package com.example.android.shoppingapp3.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.android.shoppingapp3.R;
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
    SQLiteDatabase sqLiteDatabase;

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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);

        }

        final TextView quantityTextView = (TextView) convertView.
                findViewById(R.id.quantityTextView);
        quantityTextView.setText(headerTitle.getQuantity()+"");

        TextView lblListHeader = (TextView) convertView
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

        return convertView;

    }

    private static class ViewHolder{

        public TextView quantityTextView;
        public TextView itemPriceTextView;
        public TextView subtotalTextView;
        public TextView categoryTextView;
        public TextView lastDatePurchasedTextView;
        public TextView priorityTextView;

    }

    public void updateSubtotal(String productName, String quantity, String subtotal){

        shoppingListDbHelper = new ShoppingListDbHelper(_context.getApplicationContext());
        sqLiteDatabase = shoppingListDbHelper.getWritableDatabase();

        int count = shoppingListDbHelper.updateSubtotal(productName, quantity, subtotal, sqLiteDatabase);

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
