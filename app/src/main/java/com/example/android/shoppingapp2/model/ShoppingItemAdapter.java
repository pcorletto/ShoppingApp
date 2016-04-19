package com.example.android.shoppingapp2.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.android.shoppingapp2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hernandez on 3/3/2016.
 */
public class ShoppingItemAdapter extends ArrayAdapter<ShoppingItem> {

    private final List<ShoppingItem> list;

    ArrayList<Boolean> positionArray;

    private Context mContext;

    private ShoppingItemAdapterCallBack callback;

    public ShoppingItemAdapter(Context context, List<ShoppingItem> list) {
        super(context, R.layout.shopping_list_item, list);
        this.mContext = context;
        this.list = list;

        positionArray = new ArrayList<Boolean>(list.size());
        for(int i = 0; i < list.size();i++){
            positionArray.add(false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ShoppingItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        ShoppingItem shoppingItem = list.get(position);

        if(convertView==null){
            //brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.shopping_list_item, null);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.decrementButton = (Button) convertView.findViewById(R.id.decrementButton);
            holder.incrementButton = (Button) convertView.findViewById(R.id.incrementButton);
            holder.quantityEditText = (TextView) convertView.findViewById(R.id.quantityEditText);
            holder.productNameEditText = (TextView) convertView.findViewById(R.id.productNameEditText);
            holder.unitPriceEditText = (TextView) convertView.findViewById(R.id.unitPriceEditText);
            holder.categoryEditText = (TextView) convertView.findViewById(R.id.categoryEditText);
            holder.subtotalEditText = (TextView) convertView.findViewById(R.id.subtotalEditText);

            convertView.setTag(holder);

        }

        else{
            // We have these views set up.
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(null);

        }

        // Now, set the data:

        DecimalFormat df = new DecimalFormat("$0.00");

        holder.quantity = this.getItem(position).getQuantity();
        holder.productName = this.getItem(position).getProductName();
        holder.itemPrice = this.getItem(position).getItemPrice();
        holder.category = this.getItem(position).getCategory();
        holder.subtotal = this.getItem(position).getSubtotal();

        holder.quantityEditText.setText(holder.quantity + "");
        holder.productNameEditText.setText(holder.productName);
        holder.unitPriceEditText.setText(df.format(holder.itemPrice));
        holder.categoryEditText.setText(holder.category);
        holder.subtotalEditText.setText(df.format(holder.subtotal));

        holder.decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.quantity--;

                // Only accept positive values for the quantity
                if (holder.quantity > 0)

                {
                    holder.quantityEditText.setText(holder.quantity + "");

                    list.get(position).setQuantity(holder.quantity);

                    // Some more logic here to refresh subtotal

                    DecimalFormat df = new DecimalFormat("$0.00");

                    holder.subtotal = holder.quantity * holder.itemPrice;

                    holder.subtotalEditText.setText(df.format(holder.subtotal));

                    list.get(position).setSubtotal(holder.subtotal);

                    // Pass this updated list back to DisplayShoppingList.java activity
                    // via callback interface so that it is refreshed or updated
                    // in the footer.

                    if(callback != null){

                        callback.refreshFooter(list);

                    }


                } else {

                    // Put the quantity back to where it was.
                    holder.quantity++;
                }

            }
        });

        holder.incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.quantity++;

                // Only accept positive values for the quantity
                if(holder.quantity > 0) {

                    holder.quantityEditText.setText(holder.quantity + "");

                    list.get(position).setQuantity(holder.quantity);

                    // Some more logic here to refresh subtotal

                    DecimalFormat df = new DecimalFormat("$0.00");

                    holder.subtotal = holder.quantity * holder.itemPrice;

                    holder.subtotalEditText.setText(df.format(holder.subtotal));

                    list.get(position).setSubtotal(holder.subtotal);

                    // Pass this updated list back to DisplayShoppingList.java activity
                    // via callback interface so that it is refreshed or updated
                    // in the footer.

                    if(callback != null){

                        callback.refreshFooter(list);

                }

                    }

                else{

                    // Put the quantity back to where it was.
                    holder.quantity++;
                }


            }
        });

        holder.checkBox.setFocusable(false);
        holder.checkBox.setChecked(positionArray.get(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked ){

                    positionArray.add(position, true);
                    list.get(position).setSelected(true);

                }else {

                    positionArray.add(position, false);
                    list.get(position).setSelected(false);

                }

            }
        });

        return convertView;
    }

    private static class ViewHolder{

        int quantity;
        String productName;
        double itemPrice;
        String category;
        double subtotal;

        CheckBox checkBox;
        Button decrementButton;
        Button incrementButton;
        TextView quantityEditText;
        TextView productNameEditText;
        TextView unitPriceEditText;
        TextView categoryEditText;
        TextView subtotalEditText;

    }

    public void setCallback(ShoppingItemAdapterCallBack callback){

        this.callback = callback;

    }

    public List<ShoppingItem> getCallBack(){

        return list;

    }

    public interface ShoppingItemAdapterCallBack{

        public void refreshFooter(List<ShoppingItem> list);

    }

}
