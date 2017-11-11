package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import static com.example.android.inventoryapp.R.id.imageView;

/**
 * Created by joshua on 10/13/17.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Identifier
    private static final int EXISTING_ITEM_LOADER = 0;

    //Setting up Codes imageSelect
    public static final int IMAGE_REQUEST = 20;
    public static final int EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 21;

    //Content URI for the existing Item (null if it's a new item)
    private Uri mCurrentItemUri;

    //EditText field to enter the items' name
    private EditText mNameEditText;

    //EditText field to enter the items' Price
    private EditText mPriceEditText;

    //EditText field to enter the items' Quantity
    private EditText mQuantityEditText;

    //EditText field to enter the items' Description
    private EditText mDescriptionEditText;

    //ImageView field to add item image
    private ImageView mImageView;

    boolean newItem;

    //Image Uri
    Uri mImageUri;

    //Image Uri String
    private String mCurrentImageUri = "No Images";

    //Has Item been changed?
    private boolean mItemHasChanged = false;

    //Listener that listens for any user touches on a View, implying that they are modifying
    //the view, and we change the mItemHasChanged boolean to true
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        //Examine the intent that was used to launch this activity
        //in order to figure out if we're creating a new item or editing an existing one
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        //If the intent does not contain an item content URI, then we know that we are creating a new item
        if (mCurrentItemUri == null) {
            setTitle("Add an Item");
            newItem = true;

            //Invalidate the options menu, so the "Delete" menu option can be hidden
            invalidateOptionsMenu();
        } else {
            //Otherwise, this is an exiting item, so we will change app bar to say "Edit Item"
            setTitle("Edit Item");
            newItem = false;

            //Initialize the loader to read the item data from the database and display
            //the current values in the editor
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        //Find all relevant views that will be needed to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_item_description);
        mImageView = (ImageView) findViewById(imageView);

        //Setup OnTouchListener on all the input fields, so we can determine if the user
        //has touched ormodified them. This will let us know if there are unsaved changes or not,
        //if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);

    }

    public void onPlusClick(View view) {
        String quantityString = mQuantityEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(quantityString)) {
            int quantityStringInt = Integer.valueOf(quantityString);
            quantityStringInt++;

            quantityString = Integer.toString(quantityStringInt);

            mQuantityEditText.setText(quantityString);
        }
        else {
            Toast.makeText(getApplicationContext(), "Please enter an Item Quantity.", Toast.LENGTH_SHORT).show();
        }

    }

    public void onMinusClick(View view) {
        String quantityString = mQuantityEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(quantityString)) {
            int quantityStringInt = Integer.valueOf(quantityString);
            if (quantityStringInt > 0) {
                quantityStringInt--;

                quantityString = Integer.toString(quantityStringInt);

                mQuantityEditText.setText(quantityString);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Please enter an Item Quantity.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantReults) {
        switch(requestCode){
            case EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE: {
                if (grantReults[0] == PackageManager.PERMISSION_GRANTED && grantReults.length > 0) {
                    openImageChooser();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                mImageUri = resultData.getData();
                mCurrentImageUri = mImageUri.toString();
                //imageReadyToSend
                Log.e("Selected Images", "Uri: " + mCurrentImageUri);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);

                    mImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onChooseImageClick(View view) {
        Log.e("Image Button Clicked", "Starting next...");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);

            return;
        }
        Log.e("openImageChooser", "was started");
        openImageChooser();
    }

    public void openImageChooser() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            //intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose Image for Item"), IMAGE_REQUEST);
    }

    boolean continueCheck = true;

    //Get user input from the editor and save item into database
    private void saveItem() {
        //Read from input fields
        //Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();

        if (mImageView.getDrawable() == null) {
            Toast.makeText(this, "Please add an image.", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap imageBTP = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBTP.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();

        String imageString = mCurrentImageUri;
        if (mCurrentImageUri == "No Images") {
            imageString = "";
        }

        //Check if this is supposed to be a new item and check if all the fields are blank
        if (TextUtils.isEmpty(nameString)
                || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(descriptionString)
                || (mCurrentImageUri == "No Images") && newItem == true) {
            //Since no fields were modified, we can return early without creating a new item
            //No need to create ContentValues and no need to do any ContentProviderr operations
            Toast.makeText(getApplicationContext(), "Please enter all info fields.", Toast.LENGTH_SHORT).show();
            continueCheck = false;
            return;
        }

        else {
            continueCheck = true;
            double priceStringDouble = Double.valueOf(priceString);
            DecimalFormat twoDecimalPlaces = new DecimalFormat("#.##");
            String priceStringFormatted = (twoDecimalPlaces.format(priceStringDouble)).toString();
            int quantityStringInt = Integer.valueOf(quantityString);

            //Create a ContentValues object where column names are the keys
            //and item attributes from the editor are the values
            ContentValues values = new ContentValues();
            values.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
            values.put(ItemEntry.COLUMN_ITEM_PRICE, priceStringFormatted);
            values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantityStringInt);
            values.put(ItemEntry.COLUMN_ITEM_IMAGE, imageByteArray);
            values.put(ItemEntry.COLUMN_ITEM_DESCRIPTION, descriptionString);

            //Determine if this is a new or existing item by checking if mCurrentItemUri is null or not
            if (mCurrentItemUri == null) {
                //This is a NEW ITEM, so insert a new item into the provider
                //return the content URI for the new item
                Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

                //Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    //If null, then there was an error with insertion
                    Toast.makeText(this, "Error with Saving Item", Toast.LENGTH_SHORT).show();
                } else {
                    //Otherwise, the insertion was successful and we can display a toast
                    Toast.makeText(this, "Item Saved", Toast.LENGTH_SHORT).show();
                }
            } else {
                //Otherwise, this is an Existing Item - so update the item with content URI: MCurrentUri
                //and pass in the new ContentValues. Pass in null for the selection and selection args
                //because mCurrentItemUri will already identify the correct row in the database that
                //we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

                //Show a toast message depending on a whether or not the update was successful
                if (rowsAffected == 0) {
                    //if no rows were affected, then there was an error with the update
                    Toast.makeText(this, "Error with Updating Items", Toast.LENGTH_SHORT).show();
                } else {
                    //Otherwise, the update was successful and we can display a toast
                    Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    //NEED TO IMPLEMENT A MENU
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options from the res/men/menu_editor.xml file
        //This adds menu tiems to the app bar
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //Hide delete if the if this is a new item
        if (mCurrentItemUri == null) {
            MenuItem deleteItem = menu.findItem(R.id.action_delete);
            deleteItem.setVisible(false);
            MenuItem orderItem = menu.findItem(R.id.action_order);
            orderItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        //User clicked on a menu option in the app bar overflow menu
        switch(menuItem.getItemId()) {
            //Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save item to database
                saveItem();
                //Exit activity
                if (continueCheck == true) { finish(); }
                return true;
            //Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                //pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            //Respond to a click on the "Order from Supplier" menu option
            case R.id.action_order:
                //Open Chrome for user to order new items from
                orderFromSupplier();
                return true;
            //Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                //if the item hasn't changed, continue navigating up to parent activity
                //which is the DetailActivity link
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }

                //Otherwise if there are unsaved changes, setup a dialog to warn the user.
                //Create a click listener to handle the user confirming that changes shoudl be discarded
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //User clicked "Discard" button, navigate to parent activity
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };

                //Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    //Method called when the back button is pressed
    @Override
    public void onBackPressed() {
        //If the item hasn't change, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        //Otherwise, if there are unsaved changes, setup a dialog to warn the user.
        //Create a click listener to handle the user confirming that changes should be discarded
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //User clicked "Discard" button, close the current activity
                        finish();
                    }
                };

        //Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Since the editor shows all item attributes, define a projection that contains
        //all column from the item table
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_IMAGE,
                ItemEntry.COLUMN_ITEM_DESCRIPTION};

        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   //Parent activity context
                mCurrentItemUri,        //Query the content URI for the current item
                projection,             //Columns to include in the resulting Cursor
                null,                   //No selection clause
                null,                   //No selection arguments
                null);                  //default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Bail out early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        //Proceed with moving to the first row of the cursor and reading data from it
        //This should be the only row in the cursor
        if (cursor.moveToFirst()) {
            //Find the columns of item attributes that we're interested in
            int idColumnIndex = cursor.getColumnIndex(ItemEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int descriptionColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_DESCRIPTION);
            int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_IMAGE);

            Log.e("imageColumnIndex, DA", Integer.toString(imageColumnIndex));

            //Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            byte[] imageByteArray = cursor.getBlob(imageColumnIndex);

            //Update views on screen with values from database
            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(quantity);
            mDescriptionEditText.setText(description);

            Bitmap btp = BitmapFactory.decodeByteArray(imageByteArray, 0 , imageByteArray.length);
            mImageView.setImageBitmap(btp);
            newItem = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //If the loader is invalidated, clear out all the data from the input fields
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mDescriptionEditText.setText("");
        //mImageView.setImageURI(null);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        // Create an AlertDialog.Builder and set the message, and click listeners
        //for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editting?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //User clicked the "keep editing" button, so dismiss the dialog and
                //continue editing the item
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //SET STRINGS INTO ANDROID STRING CLASS
        builder.setMessage("Do you want to deleted?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the image.
                deleteItem();
                //Close the activity
                finish();
            }
        });
        //PUT STRING IN THE STRING LIST
        builder.setNegativeButton("Cancel?", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the image.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Perform the deletion of the item in the database
    private void deleteItem() {
        //Only perform the delete if this is an existing item
        if (mCurrentItemUri != null) {
            //Call the ContentResolver to delete the item at the given content URI.
            //Pass in null for the selection and selection args because the mCurrentItemUri
            //content URI already identifies the item that we want

            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            //Show a toast message depending on whether or not the delete was successful
            if (rowsDeleted == 0) {
                //If no rows were deleted, then there was an error with the delete
                Toast.makeText(this, "Error with deleting Item", Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise, the delete was successful and we can display a toast.
                Log.v("DetailActivity", rowsDeleted + " rows deleted from the Item database.");
                Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Open Chrome to order new items from supplier
    private void orderFromSupplier() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Product Resupply");
        intent.putExtra(Intent.EXTRA_TEXT, "Dear Supplier:\n\nPlease place an order for the following:\n\nItem: " + mNameEditText.getText().toString().trim() + "\nQuantity: (Enter Quantity Desired)\n\nSincerely,\n\nDisgruntled Store Manager");
        startActivity(Intent.createChooser(intent, "Select Email Client to Contact Supplier."));
    }

}
