package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by joshua on 10/8/17.
 */

public class ItemDbHelper extends SQLiteOpenHelper {

    //Name of the db file
    private static final String DATABASE_NAME = "inventory.db";

    //Db Version
    private static final int DATABASE_VERSION = 1;

    //Constructor
    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creating the SQL table with SQLite Code
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_ITEM_PRICE + " REAL NOT NULL, "
                + ItemEntry.COLUMN_ITEM_IMAGE + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_DESCRIPTION + " TEXT NOT NULL);";

        //Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Implement only if there are additional versions
    }
}
