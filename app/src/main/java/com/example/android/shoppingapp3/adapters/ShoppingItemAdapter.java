package com.example.android.shoppingapp3.adapters;

/**
 * Created by hernandez on 3/3/2016.
 */
/*
public class ShoppingItemAdapter extends ArrayAdapter<ShoppingItem> {

    private List<ShoppingItem> list;

    ArrayList<Boolean> positionArray;

    private Context mContext;
    ShoppingListDbHelper mShoppingListDbHelper;
    SQLiteDatabase sqLiteDatabase;

    private int mRowNumber;

    // Variables for the footer:
    private int mItemCount;
    private double mTotalPrice;
    private double mSalesTax;
    private double mFinalPrice;

    private ShoppingItem mShoppingItem;
    Cursor cursor;

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

        holder.quantity = this.getItem(position).getQuantity();
        holder.productName = this.getItem(position).getProductName();
        holder.itemPrice = this.getItem(position).getItemPrice();
        holder.category = this.getItem(position).getCategory();
        holder.subtotal = this.getItem(position).getSubtotal();

        // Set the text views for the list view

        DecimalFormat df = new DecimalFormat("$0.00");

        holder.quantityEditText.setText(holder.quantity + "");
        holder.productNameEditText.setText(holder.productName);
        holder.unitPriceEditText.setText(df.format(holder.itemPrice));
        holder.categoryEditText.setText(holder.category);
        holder.subtotalEditText.setText(df.format(holder.subtotal));

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

                    // Update new subtotal in the SQLite Database

                    updateSubtotal(list.get(position).getProductName(),
                            list.get(position).getQuantity()+"", list.get(position).getSubtotal()+"");

                    // Refactor the following block of code, and put it in a method called "reloadFromDB"

                    // In the next update, pull the appropriate values from SQLiteDB, and update
                    // itemCount, totalPrice, salesTax and finalPrice

                    mItemCount = 0;
                    mTotalPrice = 0;
                    mSalesTax = 0;
                    mFinalPrice = 0;

                    // Reload the items from the database

                    // Initialize shopping item

                    mShoppingItem = new ShoppingItem();

                    //Initialize ShoppingListDbHelper and SQLiteDB

                    mShoppingListDbHelper = new ShoppingListDbHelper(mContext);
                    sqLiteDatabase = mShoppingListDbHelper.getReadableDatabase();

                    cursor = ShoppingListDbHelper.getShoppingItem(sqLiteDatabase);

                    mRowNumber = 0;

                    if(cursor.moveToFirst()) {

                        do {

                            String productName, category;
                            int quantity;
                            double unitPrice, subtotal;

                            quantity = cursor.getInt(0);
                            productName = cursor.getString(1);
                            unitPrice = cursor.getDouble(2);
                            category = cursor.getString(3);
                            subtotal = cursor.getDouble(4);

                            mShoppingItem = new ShoppingItem(quantity, productName, unitPrice, category, subtotal);

                            mItemCount = quantity + mItemCount;
                            mTotalPrice = subtotal + mTotalPrice;
                            mSalesTax = (subtotal * 0.07)+ mSalesTax;
                            mFinalPrice = mTotalPrice + mSalesTax;

                            mRowNumber++;


                        }

                        while (cursor.moveToNext());

                    }

                    // Done reloading items from the database

                    com.example.android.shoppingapp3.ui.DisplayShoppingList.ViewHolder.itemCountEditText.setText(mItemCount+"");
                    com.example.android.shoppingapp3.ui.DisplayShoppingList.ViewHolder.totalPriceEditText.setText(df.format(mTotalPrice));
                    com.example.android.shoppingapp3.ui.DisplayShoppingList.ViewHolder.salesTaxEditText.setText(df.format(mSalesTax));
                    com.example.android.shoppingapp3.ui.DisplayShoppingList.ViewHolder.finalPriceEditText.setText(df.format(mFinalPrice));


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

                    // Update new subtotal in the SQLite Database

                    updateSubtotal(list.get(position).getProductName(),
                            list.get(position).getQuantity()+"", list.get(position).getSubtotal()+"");

                    // Set the text views in the footer to the new values after update.

                    // Refactor the following block of code, and put it in a method called "reloadFromDB"

                    // In the next update, pull the appropriate values from SQLiteDB, and update
                    // itemCount, totalPrice, salesTax and finalPrice

                    mItemCount = 0;
                    mTotalPrice = 0;
                    mSalesTax = 0;
                    mFinalPrice = 0;

                    // Reload the items from the database

                    // Initialize shopping item

                    mShoppingItem = new ShoppingItem();

                    //Initialize ShoppingListDbHelper and SQLiteDB

                    mShoppingListDbHelper = new ShoppingListDbHelper(mContext);
                    sqLiteDatabase = mShoppingListDbHelper.getReadableDatabase();

                    cursor = ShoppingListDbHelper.getShoppingItem(sqLiteDatabase);

                    mRowNumber = 0;

                    if(cursor.moveToFirst()) {

                        do {

                            String productName, category;
                            int quantity;
                            double unitPrice, subtotal;

                            quantity = cursor.getInt(0);
                            productName = cursor.getString(1);
                            unitPrice = cursor.getDouble(2);
                            category = cursor.getString(3);
                            subtotal = cursor.getDouble(4);

                            mShoppingItem = new ShoppingItem(quantity, productName, unitPrice, category, subtotal);

                            mItemCount = quantity + mItemCount;
                            mTotalPrice = subtotal + mTotalPrice;
                            mSalesTax = (subtotal * 0.07)+ mSalesTax;
                            mFinalPrice = mTotalPrice + mSalesTax;

                            mRowNumber++;


                        }

                        while (cursor.moveToNext());

                    }

                    // Done reloading items from the database

                    com.example.android.shoppingapp3.ui.DisplayShoppingList.ViewHolder.itemCountEditText.setText(mItemCount+"");
                    com.example.android.shoppingapp3.ui.DisplayShoppingList.ViewHolder.totalPriceEditText.setText(df.format(mTotalPrice));
                    com.example.android.shoppingapp3.ui.DisplayShoppingList.ViewHolder.salesTaxEditText.setText(df.format(mSalesTax));
                    com.example.android.shoppingapp3.ui.DisplayShoppingList.ViewHolder.finalPriceEditText.setText(df.format(mFinalPrice));

                }

                else{

                    // Put the quantity back to where it was.
                    holder.quantity++;
                }

            }
        });



        return convertView;
    }

    public void refresh(List<ShoppingItem> list){

        this.list = list;
        notifyDataSetChanged();

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

    public void updateSubtotal(String productName, String quantity, String subtotal){

        mShoppingListDbHelper = new ShoppingListDbHelper(mContext.getApplicationContext());
        sqLiteDatabase = mShoppingListDbHelper.getWritableDatabase();

        int count = mShoppingListDbHelper.updateSubtotal(productName, quantity, subtotal, sqLiteDatabase);

    }

}

*/
