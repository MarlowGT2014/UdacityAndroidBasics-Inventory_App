package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

import java.text.DecimalFormat;

import static com.example.android.inventoryapp.R.id.itemName;

/**
 * Created by joshua on 10/13/17.
 */

public class ItemCursorAdapter extends CursorAdapter {

    //Constructor
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(itemName);
        TextView priceTextView = (TextView) view.findViewById(R.id.itemPrice);
        TextView quantityTextView = (TextView) view.findViewById(R.id.itemQuantity);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        //Find the columns of item attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(ItemEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_IMAGE);


        //Read the item attributes from the cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        String itemQuantity = cursor.getString(quantityColumnIndex);

        //If the the item for item name is empty
        if (TextUtils.isEmpty(itemName)) {
            itemName = "Unknown Item";
        }

        //Format Price
        double priceStringDouble = Double.valueOf(itemPrice);
        DecimalFormat twoDecimalPlaces = new DecimalFormat("#.00");
        String priceStringFormatted = (twoDecimalPlaces.format(priceStringDouble)).toString();

        //Update the textviews with the attributes for the current item
        nameTextView.setText(itemName);
        priceTextView.setText(priceStringFormatted);
        quantityTextView.setText(itemQuantity);

        //For the following section, I relied heavily on a page from stackoverflow that utilized an example specifically from this project
        //URL: https://stackoverflow.com/questions/44034208/updating-listview-with-cursoradapter-after-an-onclick-changes-a-value-in-sqlite
        //The concepts shown were largely followed, but tailored to the needs of my particular application

        //Getting info for Sales Button and pointing it to the current item clicked

        Button itemSalesButton = (Button) view.findViewById(R.id.itemSaleButton);
        int currentItemID = cursor.getInt(cursor.getColumnIndex(ItemEntry._ID));
        final Uri contentUri = Uri.withAppendedPath(ItemEntry.CONTENT_URI, Integer.toString(currentItemID));

        final TextView viewQuantity = (TextView) view.findViewById(R.id.itemQuantity);
        final TextView viewName = (TextView) nameTextView.findViewById(R.id.itemName);


        //Setting the 'Sale' Button to adjust the item quantity
        itemSalesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemQuantityMain = Integer.valueOf(viewQuantity.getText().toString());
                String itemName = viewName.getText().toString().trim();

                if (itemQuantityMain > 0) {
                    itemQuantityMain--;
                }

                //Update Content Values to show new Quantity & and push to DB
                ContentValues vals = new ContentValues();
                vals.put(ItemEntry.COLUMN_ITEM_QUANTITY, itemQuantityMain);
                context.getContentResolver().update(contentUri, vals, null, null);
                Toast.makeText(v.getContext(), "Item Sale On: " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
