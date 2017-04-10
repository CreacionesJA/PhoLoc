package com.jad.pholoc;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import com.jad.pholoc.model.ImageItem;
import com.jad.pholoc.model.PhotoModel;
import com.jad.pholoc.util.BitmapOperations;
import com.jad.pholoc.util.GridViewAdapter;
import com.jad.pholoc.util.LocationsSQLite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

/**
 * PhoLoc Gallery
 *
 * @author Jorge Alvarado
 */
public class LocationGalleryActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();

    private Vector<PhotoModel> vPhotos;
    private LocationsSQLite dbLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_gallery);

        // Put the back arrow on the action bar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        // Get the id of the location
        Bundle bundle = getIntent().getExtras();
        int idLocation = bundle.getInt("idLocation");

        // Open the location database
        dbLoc = new LocationsSQLite(this, "DBLocations.db", null, 1);
        // If the id of the location is 0 (All Locations)
        if (idLocation == 0) {
            // Get all images
            vPhotos = dbLoc.getAllPhotos();
        } else {
            // Get all images of the location
            vPhotos = dbLoc.getPhotosByLocation(idLocation);
        }
        Log.i(TAG, "All photos loaded");
        GridView gridView = (GridView) findViewById(R.id.gridView);
        GridViewAdapter customGridAdapter = new GridViewAdapter(this, R.layout.row_grid_image,
                getData());
        gridView.setAdapter(customGridAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                v.setSelected(true);
                // We get the file of the image
                File f = new File(vPhotos.get(position).getPath()
                        + vPhotos.get(position).getName());
                Log.i("Image",f.getAbsolutePath());
                // If it exists (it may be the case that the user deleted it with another app)
                if (f.exists()) {
                    // Open the system gallery
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(),
                            getApplicationContext().getPackageName() + ".provider", f), "image/*");
                    startActivity(intent);
                } else {
                    // If it does not exist, we indicate it
                    showToast(getResources().getString(
                            R.string.txt_gallery_photonoexits));
                    Log.e(TAG, "Photo "
                            + vPhotos.get(position).getName() + " does not exist.");
                }
                v.setSelected(false);
            }

        });
    }

    /**
     * Get the images to display in the GridLayout
     *
     * @return ImageItems Array (Image and image name)
     */
    private ArrayList<ImageItem> getData() {
        BitmapOperations bp = new BitmapOperations();
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        // Getting the images of the array
        for (int i = 0; i < vPhotos.size(); i++) {
            PhotoModel p = vPhotos.get(i);
            File photo = new File(p.getPath() + p.getName());
            // If the photo exists
            if (photo.exists()) {
                File thumbnail = new File(p.getPath() + ".thumbnails/"
                        + p.getName());
                // If the thumbnail has been deleted
                if (!thumbnail.exists()) {
                    try {
                        // Generate the image thumbnail again
                        bp.generateThumbnail(p.getPath(), p.getName(),
                                getResources().getDisplayMetrics());
                        Log.i(TAG, "Thumbnail " + p.getName()
                                + " generated");
                    } catch (Exception e) {
                        Log.e(TAG, "Error generating deleted "
                                + p.getName() + " thumbnail");

                    }
                }
                // Get the thumbnail of the image
                Bitmap bitmap = BitmapFactory.decodeFile(p.getPath()
                        + ".thumbnails/" + p.getName());
                // Add it to the array to show it in the gallery
                imageItems.add(new ImageItem(bitmap, p.getName()));
            } else {
                // If the photo has been deleted
                Log.e("LocationGallery", "Photo " + p.getName() + " deleted from the db.");
                dbLoc.deletePhoto(p.getId());
                // And delete the thumbnail if it exists
                bp.deleteMiniatura(p.getPath(), p.getName());
            }
        }
        return imageItems;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location_gallery, menu);
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
                this.finish();
                return true;
            case R.id.about:
                i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.settings:
                i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
