package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by joshua on 10/8/17.
 */

public class ItemProvider extends ContentProvider {

    //Log tag
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();

    //URI matcher code - items table
    private static final int ITEMS = 100;

    //URI matcher code - single item in the items table
    private static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static Initializer
    static {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);

        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    private ItemDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());

        return true;
    }

    @Override
    public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //Cursor that holds the results of the query
        Cursor cursor;

        //Can the URI matcher match to a specific code?
        int match = sUriMatcher.match(uri);

        switch(match) {
            case ITEMS:
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ITEM_ID:
                //All of the '?'s will need to be replaced
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                //Query the items table for _id
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //If the data at this URI changes, then we know we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        //return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not support for " + uri);
        }
    }

    //Insert item in the database, return new content uri
    private Uri insertItem(Uri uri, ContentValues values) {

        //Is Item Name null?
        String name = values.getAsString(ItemEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a Name.");
        }

        //Is Current Quantity null?
        String quantity = values.getAsString(ItemEntry.COLUMN_ITEM_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Item requires a Quantity.");
        }

        //Is Price null?
        String price = values.getAsString(ItemEntry.COLUMN_ITEM_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Item requires a Price.");
        }

        //Is Description null?
        String description = values.getAsString(ItemEntry.COLUMN_ITEM_DESCRIPTION);
        if (description == null) {
            throw new IllegalArgumentException("Item requires a Description.");
        }

        //get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //insert the new item
        long id = database.insert(ItemEntry.TABLE_NAME, null, values);
        //Thrown error if failed
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the item content uri
        getContext().getContentResolver().notifyChange(uri, null);

        //Return the new URI with the ID of the newly inserted row, appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not support for " + uri);
        }
    }


    //Update Items
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ItemEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(ItemEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a Name.");
            }
        }

        if (values.containsKey(ItemEntry.COLUMN_ITEM_QUANTITY)) {
            String quantity = values.getAsString(ItemEntry.COLUMN_ITEM_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Item requires a Quantity.");
            }
        }

        if (values.containsKey(ItemEntry.COLUMN_ITEM_PRICE)) {
            String price = values.getAsString(ItemEntry.COLUMN_ITEM_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Item requires a Price.");
            }
        }

        //Get Writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Perform update on the db and get the number of rows affected
        int rowsUpdated = database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);

        //If 1 or more rows were updated, notify the listeners that the data at the given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Get Writable Database
        SQLiteDatabase  database = mDbHelper.getWritableDatabase();

        //Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch(match) {
            case ITEMS:
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows are deleted, notifiy all listeners that the data at the given uri has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case ITEMS:
                return ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri + " with match " + match);
        }
    }
}