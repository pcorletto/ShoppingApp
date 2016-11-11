package com.example.android.shoppingapp3.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.shoppingapp3.model.ShoppingItem;
import com.example.android.shoppingapp3.R;

import java.util.HashMap;
import java.util.List;


/**
 * Created by hernandez on 10/9/2016.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<ShoppingItem>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<ShoppingItem>> listChildData) {
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);

        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

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

            holder.systolicReading = (TextView) convertView.findViewById(R.id.systolicReading);
            holder.diastolicReading = (TextView) convertView.findViewById(R.id.diastolicReading);
            holder.systolicStatusImageView = (ImageView) convertView.findViewById(R.id.systolicStatusImageView);
            holder.diastolicStatusImageView = (ImageView) convertView.findViewById(R.id.diastolicStatusImageView);
            holder.pulseLabel = (TextView) convertView.findViewById(R.id.pulseLabel);
            holder.pulseReading = (TextView) convertView.findViewById(R.id.pulseReading);
            holder.descriptionLabel = (TextView) convertView.findViewById(R.id.descriptionLabel);
            holder.description = (TextView) convertView.findViewById(R.id.description);

            convertView.setTag(holder);

        } else{

            holder = (ViewHolder) convertView.getTag();
        }

        //TextView txtListChild = (TextView) convertView
        //      .findViewById(R.id.lblListItem);

        //txtListChild.setText(childText);

        holder.systolicReading.setText(childText.getQuantity()+"");
        holder.diastolicReading.setText(childText.getQuantity()+"");


        //holder.systolicStatusImageView.setImageResource(childText.getSystolicIconId());

        //holder.diastolicStatusImageView.setImageResource(childText.getDiastolicIconId());

        holder.pulseLabel.setText("PULSE RATE: ");
        holder.pulseReading.setText(childText.getLastQuantity()+"");
        holder.descriptionLabel.setText("DESCRIPTION");
        holder.description.setText(childText.getCategory());



        return convertView;

    }

    private static class ViewHolder{

        public TextView systolicReading;
        public TextView diastolicReading;
        ImageView systolicStatusImageView;
        ImageView diastolicStatusImageView;
        public TextView pulseLabel;
        public TextView pulseReading;
        public TextView descriptionLabel;
        public TextView description;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
