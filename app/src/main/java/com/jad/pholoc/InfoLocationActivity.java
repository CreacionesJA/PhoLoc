package com.jad.pholoc;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jad.pholoc.model.LocationModel;
import com.jad.pholoc.model.PhotoModel;
import com.jad.pholoc.util.BitmapOperations;
import com.jad.pholoc.util.DeleteLocationDialog;
import com.jad.pholoc.util.LocationsSQLite;

/**
 * Information Location Activity
 *
 * @author Jorge Alvarado
 */
public class InfoLocationActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText name;
    private EditText address;
    private EditText coordinates;
    private EditText notes;

    private String pathLastPhoto;
    private String nameLastPhoto;
    private int idLocation;
    private LocationModel loc;
    /**
     * Current date array: [0]=Year | [1]=Month | [2]=Day
     * | [3]=Hours| [4]=Minutes | [5]=Seconds
     **/
    private String[] currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infolocation);

        // Put the back arrow on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Initialize variables
        pathLastPhoto = "";
        nameLastPhoto = "";
        currentDate = new String[6];

        // Initialize views
        initializeViews();

        // Get the id of the location
        Bundle bundle = getIntent().getExtras();
        idLocation = bundle.getInt("idLocation");
        // Get Location Information
        getInfoLocation(idLocation);
    }

    /**
     * Initialize views
     */
    private void initializeViews() {
        name = (EditText) findViewById(R.id.tfd_infolocation_name);
        address = (EditText) findViewById(R.id.tfd_infolocation_address);
        coordinates = (EditText) findViewById(R.id.tfd_infolocation_coordinates);
        notes = (EditText) findViewById(R.id.tfd_infolocation_notes);
        // Block editing to EditTexts
        name.setKeyListener(null);
        address.setKeyListener(null);
        coordinates.setKeyListener(null);
        notes.setKeyListener(null);
    }

    /**
     * Get Location Information
     *
     * @param id Id of the location
     */
    public void getInfoLocation(int id) {
        DecimalFormat decimalFormat = new DecimalFormat("0.000000");
        // Open the location database
        LocationsSQLite dbloc = new LocationsSQLite(this, "DBLocations.db",
                null, 1);
        // Get the location from your id
        loc = dbloc.getLocalizacion(id);
        // If the location exists
        if (loc != null) {
            // Show your name and address
            name.setText(loc.getName());
            address.setText(loc.getAddress());
            // If the location has GPS coordinates, they are shown
            if (!(loc.getLatitude() == 0 && loc.getLongitude() == 0)) {
                String c = String.format(getResources().getString(
                        R.string.txt_infolocation_latitude)
                        , decimalFormat.format(loc.getLatitude())) + "\n";
                c += String.format(getResources().getString(
                        R.string.txt_infolocation_longitude)
                        , decimalFormat.format(loc.getLongitude()));
                coordinates.setText(c);
            } else {
                coordinates.setText(R.string.txt_infolocation_withoutcoordinates);
            }
            // If the location has notes, they are shown
            if (!loc.getNotes().equals("")) {
                notes.setText(loc.getNotes());
            } else {
                notes.setText(R.string.txt_infolocation_withoutnotes);
            }
            Log.i("InfoLocation", "Localizacion con id " + idLocation
                    + " cargada");
        } else {
            // If the location does not exist, a toast is displayed and the activity is closed
            showToast(getResources().getString(
                    R.string.txt_infolocation_noexits));
            Log.e(TAG, "Location with id " + idLocation
                    + " does not exist");
            this.finish();
        }
    }

    /**
     * Take a photo for the location
     */
    public void takePhoto() {
        // Check the permissions of: Camera and External Storage
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Camera Intent
            Intent cameraIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    // Create file for the image
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    // Path of the image file
                    Uri uriSavedImage = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    // Open the Camera
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {
            // Request permissions for: Camera and External Storage
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    /**
     * Actions to take when the camera is closed
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the photo is taken correctly
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            BitmapOperations bp = new BitmapOperations();
            // Open the location database
            LocationsSQLite dbloc = new LocationsSQLite(this, "DBLocations.db",
                    null, 1);
            // Date the photo was shot in format: yyyy-MM-dd HH:mm:ss
            String datePhoto = currentDate[0] + "-" + currentDate[1] + "-" + currentDate[2] + " "
                    + currentDate[3] + ":" + currentDate[4] + ":" + currentDate[5];
            try {
                // Format the date
                SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                Date date = sdf.parse(datePhoto);
                // Create a photo
                PhotoModel photo = new PhotoModel(1, idLocation, nameLastPhoto,
                        pathLastPhoto, date);

                // Generate the thumbnail of the photo
                bp.generateThumbnail(pathLastPhoto, nameLastPhoto,
                        getResources().getDisplayMetrics());
                Log.i("InfoLocation", "Miniatura de la foto " + nameLastPhoto
                        + " generada");

                // Add the photo to the database
                dbloc.addPhoto(photo);

                // Notify the system gallery that adds the photo
                galleryAddPic();
                // Show that the photo has been taken successfully
                showToast(String.format(
                        getResources().getString(
                                R.string.txt_infolocation_newphoto),
                        nameLastPhoto));
                Log.i(TAG, "Photo " + nameLastPhoto
                        + " saved in gallery");
            } catch (ParseException e) {
                Log.e(TAG, "Error getting date");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,
                        "Error generating photo thumbnail of "
                                + nameLastPhoto);
            }

        } else {
            // Delete the file of the temporary image in case it exists
            File image = new File(pathLastPhoto + nameLastPhoto);
            if (image.exists()) {
                image.delete();
            }
        }
    }

    /**
     * Create image file
     *
     * @return Image File
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Directory where the photo will be stored
        File imagesFolder = new File(Environment.getExternalStorageDirectory(),
                "/PhoLoc/" + loc.getPath() + "/");
        // If the directory does not exist, we create it
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }
        // Get array with the current date and time
        currentDate = getCurrentDateArray();
        // Name of the photo (IMG_yyyyMMdd_HHmmss)
        String namePhoto = "IMG_" + currentDate[0] + currentDate[1] + currentDate[2] +
                "_" + currentDate[3] + currentDate[4] + currentDate[5];
        // Directory where the photo will be stored
        pathLastPhoto = imagesFolder.toString() + "/";
        // We create the image file (empty)
        File image = File.createTempFile(namePhoto, ".jpg", imagesFolder);
        // Real name of photo
        nameLastPhoto = image.getName();
        return image;
    }


    /**
     * Open Activity Gallery
     *
     * @param view View
     */
    public void openGallery(View view) {
        // Open gallery for location
        Intent i = new Intent(this, LocationGalleryActivity.class);
        i.putExtra("idLocation", idLocation);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.newPhoto:
                takePhoto();
                return true;
            case R.id.about:
                i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.settings:
                i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.deleteLocation:
                FragmentManager fragmentManager = getSupportFragmentManager();
                DeleteLocationDialog deleteLocationDialog = new DeleteLocationDialog();
                deleteLocationDialog.setIdLocation(idLocation);
                deleteLocationDialog.show(fragmentManager, "tagAlert");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Notify system gallery that we have added a photo to the file system
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pathLastPhoto + nameLastPhoto);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Get array with the current date and time
     *
     * @return array with the current date and time
     */
    private String[] getCurrentDateArray() {
        Calendar currentDateCalendar = new GregorianCalendar();
        /*
         * Current date array: [0]=Year | [1]=Month | [2]=Day
         * | [3]=Hours| [4]=Minutes | [5]=Seconds
         */
        String[] currentDateArray = new String[6];
        currentDateArray[0] = String.format(Locale.ENGLISH, "%04d", currentDateCalendar.get(Calendar.YEAR));
        currentDateArray[1] = String.format(Locale.ENGLISH, "%02d", currentDateCalendar.get(Calendar.MONTH) + 1);
        currentDateArray[2] = String.format(Locale.ENGLISH, "%02d", currentDateCalendar.get(Calendar.DAY_OF_MONTH));
        currentDateArray[3] = String.format(Locale.ENGLISH, "%02d", currentDateCalendar.get(Calendar.HOUR_OF_DAY));
        currentDateArray[4] = String.format(Locale.ENGLISH, "%02d", currentDateCalendar.get(Calendar.MINUTE));
        currentDateArray[5] = String.format(Locale.ENGLISH, "%02d", currentDateCalendar.get(Calendar.SECOND));
        return currentDateArray;
    }

    /**
     * Show toast
     *
     * @param message Message
     */
    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }
}