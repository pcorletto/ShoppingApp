package com.example.android.shoppingapp2.model;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.android.shoppingapp2.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by hernandez on 3/3/2016.
 */
public class ShoppingItemAdapter extends ArrayAdapter<ShoppingItem> {

    private final List<ShoppingItem> list;

    private Activity mContext;


    public ShoppingItemAdapter(Activity context, List<ShoppingItem> list) {
        super(context, R.layout.shopping_list_item, list);
        this.mContext = context;
        this.list = list;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView==null){
            //brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.shopping_list_item, null);

            holder = new ViewHolder();
            holder.quantityEditText = (EditText) convertView.findViewById(R.id.quantityEditText);
            holder.productNameEditText = (EditText) convertView.findViewById(R.id.productNameEditText);
            holder.unitPriceEditText = (EditText) convertView.findViewById(R.id.unitPriceEditText);
            holder.categoryEditText = (EditText) convertView.findViewById(R.id.categoryEditText);
            holder.subtotalEditText = (EditText) convertView.findViewById(R.id.subtotalEditText);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    ShoppingItem element = (ShoppingItem) holder.checkBox.getTag();

                    element.setSelected(buttonView.isChecked());

                }
            });

            holder.checkBox.setTag(list.get(position));
            convertView.setTag(holder);
        }

        else{
            // We have these views set up.
            holder = (ViewHolder) convertView.getTag();
        }

        // Now, set the data:

        DecimalFormat df = new DecimalFormat("$0.00");

        ShoppingItem shoppingItem =list.get(position);

        holder.quantityEditText.setText(shoppingItem.getQuantity()+"");
        holder.productNameEditText.setText(shoppingItem.getProductName());
        holder.unitPriceEditText.setText(df.format(shoppingItem.getItemPrice()));
        holder.categoryEditText.setText(shoppingItem.getCategory());
        holder.subtotalEditText.setText(df.format(shoppingItem.getSubtotal()));

        return convertView;
    }

    private static class ViewHolder{

        CheckBox checkBox;
        EditText quantityEditText;
        EditText productNameEditText;
        EditText unitPriceEditText;
        EditText categoryEditText;
        EditText subtotalEditText;

    }

}

