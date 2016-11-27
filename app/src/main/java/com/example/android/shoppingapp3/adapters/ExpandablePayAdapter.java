package com.example.android.shoppingapp3.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
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
public class ExpandablePayAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<ShoppingItem> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<ShoppingItem, List<ShoppingItem>> _listDataChild;

    ShoppingListDbHelper shoppingListDbHelper;
    ShoppingCartDbHelper shoppingCartDbHelper;
    SQLiteDatabase sqLiteDatabase;

    DecimalFormat df = new DecimalFormat("$0.00");

    public ExpandablePayAdapter(Context context, List<ShoppingItem> listDataHeader,
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
            convertView = inflater.inflate(R.layout.pay_group, null);

        }

        // If the priority is the highest "3" shade it red.
        // If the priority is medium "2" shade it yellow.
        // If the priority is low "1" shade it blue.

        LinearLayout payGroupLayout = (LinearLayout) convertView.findViewById(R.id.payGroupLayout);
        if(headerTitle.getPriority()==3){

            payGroupLayout.setBackgroundColor(convertView.getResources().getColor(R.color.RedHighestPriority));
        }

        if(headerTitle.getPriority()==2){

            payGroupLayout.setBackgroundColor(convertView.getResources().getColor(R.color.YellowMediumPriority));
        }

        else if(headerTitle.getPriority()==1){

            payGroupLayout.setBackgroundColor(convertView.getResources().getColor(R.color.BlueLowPriority));
        }


        final TextView quantityTextView = (TextView) convertView.
                findViewById(R.id.quantityTextView);
        quantityTextView.setText(headerTitle.getQuantity()+"");

        final TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.getProductName());

        final TextView priceTextView = (TextView) convertView.
                findViewById(R.id.priceTextView);
        priceTextView.setText(df.format(headerTitle.getQuantity()*headerTitle.getItemPrice()));


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ShoppingItem childText = (ShoppingItem) getChild(groupPosition, childPosition);

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pay_item, null);

            holder.quantityTextView = (TextView) convertView.findViewById(R.id.quantityTextView);
            holder.itemPriceTextView = (TextView) convertView.findViewById(R.id.itemPriceTextView);
            holder.subtotalTextView = (TextView) convertView.findViewById(R.id.subtotalTextView);
            holder.categoryTextView = (TextView) convertView.findViewById(R.id.categoryTextView);
            holder.lastDatePurchasedTextView = (TextView) convertView.findViewById(R.id.lastDatePurchasedTextView);
            holder.priorityTextView = (TextView) convertView.findViewById(R.id.priorityTextView);
            holder.productImage = (WebView) convertView.findViewById(R.id.productImage);
            holder.taxableTextView = (TextView) convertView.findViewById(R.id.taxableTextView);
            holder.taxTextView = (TextView) convertView.findViewById(R.id.taxTextView);

            convertView.setTag(holder);

        } else{

            holder = (ViewHolder) convertView.getTag();
        }

        holder.quantityTextView.setText(childText.getQuantity()+"");
        holder.itemPriceTextView.setText(df.format(childText.getItemPrice()));
        holder.subtotalTextView.setText(df.format(childText.getSubtotal()));
        holder.categoryTextView.setText(childText.getCategory());
        holder.lastDatePurchasedTextView.setText(childText.getLastDatePurchased());

        holder.priorityTextView.setText(childText.getPriority()+"");

        if(childText.isTaxable()){
            holder.taxableTextView.setText("Yes");
        }

        else{
            holder.taxableTextView.setText("No");
        }

        double salesTax = 0;

        if(childText.isTaxable()) {
            salesTax = childText.getSubtotal() * 0.07;
            //salesTax = childText.getItemPrice() * childText.getQuantity() * 0.07;
        }

        else{ // Non-taxable items: food, medicine
            salesTax = 0;
        }

        holder.taxTextView.setText(df.format(salesTax));

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
        public TextView taxTextView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
