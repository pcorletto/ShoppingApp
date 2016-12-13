package com.example.android.shoppingapp3.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.android.shoppingapp3.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hernandez on 12/12/2016.
 */
public class PurchaseItemAdapter extends ArrayAdapter<PurchaseItem> {

    private List<PurchaseItem> list;

    ArrayList<Boolean> positionArray;

    private Context mContext;

    public PurchaseItemAdapter(Context context, List<PurchaseItem> list) {
        super(context, R.layout.purchase_list_item, list);
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
    public PurchaseItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        PurchaseItem purchaseItem = list.get(position);

        if(convertView == null){
            //brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.purchase_list_item, null);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
            holder.summaryTextView = (TextView) convertView.findViewById(R.id.summaryTextView);

            convertView.setTag(holder);

        }

        else{
            // We have these views set up.
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(null);

        }

        // Now, set the data:

        holder.date = this.getItem(position).getDate();
        holder.summary = this.getItem(position).getSummary();
        holder.purchase_ID = this.getItem(position).getPurchaseID();

        holder.dateTextView.setText(holder.date);
        holder.summaryTextView.setText(holder.summary);

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

    public void refresh(List<PurchaseItem> list){

        this.list = list;
        notifyDataSetChanged();

    }

    private static class ViewHolder{

        int purchase_ID;
        String date;
        String summary;

        CheckBox checkBox;
        TextView dateTextView;
        TextView summaryTextView;

    }

}
