package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by joshua on 10/8/17.
 */

public final class ItemContract {

    //Empty constructor to prevent anyone from accidentally instantiating it
    private ItemContract() {}

    //The Content Authority
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    //Create all URI's to contact the content provider, using the CONTENT_AUTHORITY
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible Path
    //This is appended to the base content URI for possible URI's
    public static final String PATH_ITEMS = "items";

    //Inner Class
    //This defines the constant values for the pets database table
    //Each entry in the table represents a single item
    public static final class ItemEntry implements BaseColumns {

        //The content URI to access the item data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        //The MIME type for a list of items
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        //The MIME type for a single item
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        //Name of the database for the items
        public static final String TABLE_NAME = "items";

        //COMPONENTS OF THE TABLE

        //ID Number for the Items
        public static final String _ID = BaseColumns._ID;

        //Name of the item
        public static final String COLUMN_ITEM_NAME = "name";

        //Quantity of the item
        public static final String COLUMN_ITEM_QUANTITY = "quantity";

        //Price of the item
        public static final String COLUMN_ITEM_PRICE = "price";

        //Description of the Item
        public static final String COLUMN_ITEM_DESCRIPTION = "description";

        //Image of the Item
        public static final String COLUMN_ITEM_IMAGE = "image";
    }



}