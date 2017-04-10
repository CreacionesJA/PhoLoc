package com.jad.pholoc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import com.jad.pholoc.model.LocationModel;
import com.jad.pholoc.model.PhotoModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class that handles the Locations Database
 *
 * @author Jorge Alvarado
 */
public class LocationsSQLite extends SQLiteOpenHelper {

    public final String TAG = this.getClass().getSimpleName();

    // SQL statement to create the table of locations and photos
    String tableLocations = "CREATE TABLE locations (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT,address TEXT, notes TEXT, latitude DOUBLE, longitude DOUBLE, path TEXT)";
    String tablePhotos = "CREATE TABLE photos (id INTEGER PRIMARY KEY AUTOINCREMENT, idLocation INTEGER, name TEXT, path TEXT, date DATE)";

    public LocationsSQLite(Context context, String name,
                           CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // It executes the SQL statements of creation of the tables
        db.execSQL(tableLocations);
        db.execSQL(tablePhotos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    /**
     * Add location to the database
     *
     * @param loc location
     * @return 0 if there are no errors, -1 if there is a location with the same name
     */
    public int addLocation(LocationModel loc) {
        // Open the location database
        SQLiteDatabase db = getWritableDatabase();
        // Verify that there is no location with the same name
        Cursor c = db.rawQuery(
                " SELECT * FROM locations WHERE name='" + loc.getName()
                        + "'", null);
        // If there is no location with the same name
        if (!c.moveToFirst()) {
            // Insert the new location in the database
            db.execSQL("INSERT INTO locations VALUES (null,'" + loc.getName()
                    + "','" + loc.getAddress() + "','" + loc.getNotes()
                    + "'," + loc.getLatitude() + "," + loc.getLongitude() + ",'" + loc.getPath() + "')");
            c.close();
            db.close();
            return 0;
        } else {
            // If there is a location with the same name return -1
            c.close();
            db.close();
            return -1;
        }
    }

    /**
     * Delete location and all associated photos from the database
     *
     * @param id Location Id
     */
    public void deleteLocation(int id) {
        // Open the location database
        SQLiteDatabase db = getWritableDatabase();
        // Removed the location of the database and associated photos
        db.execSQL("DELETE FROM locations WHERE id=" + id);
        db.execSQL("DELETE FROM photos WHERE idLocation=" + id);
        db.close();
    }

    /**
     * Get all locations
     *
     * @return Vector with all locations
     */
    public Vector<LocationModel> getAllLocations() {
        Vector<LocationModel> vLoc = new Vector<LocationModel>();
        // Open the location database
        SQLiteDatabase db = getWritableDatabase();
        // Get all locations (if any)
        Cursor c = db.rawQuery(" SELECT * FROM locations", null);
        if (c.moveToFirst()) {
            do {
                // Create the Localization and add it to the vector
                LocationModel l = new LocationModel(Integer.parseInt(c
                        .getString(0)), c.getString(1), c.getString(2),
                        c.getString(3), Double.parseDouble(c.getString(4)),
                        Double.parseDouble(c.getString(5)), c.getString(6));
                vLoc.add(l);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return vLoc;
    }

    /**
     * Get localized by id
     *
     * @param id Location id
     * @return Location
     */
    public LocationModel getLocalizacion(int id) {
        LocationModel loc = null;
        // Open the location database
        SQLiteDatabase db = getWritableDatabase();
        // Now try to get the location with the specified id
        Cursor c = db.rawQuery(" SELECT * FROM locations WHERE id=" + id, null);
        // If the location exists
        if (c.moveToFirst()) {
            // Return location
            loc = new LocationModel(Integer.parseInt(c.getString(0)),
                    c.getString(1), c.getString(2), c.getString(3),
                    Double.parseDouble(c.getString(4)), Double.parseDouble(c
                    .getString(5)), c.getString(6));

        }
        c.close();
        db.close();
        return loc;
    }

    /**
     * Add photo to the database
     *
     * @param pho Photo
     */
    public void addPhoto(PhotoModel pho) {
        // Open the location database
        SQLiteDatabase db = getWritableDatabase();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));
        // Insert the photo in the database
        db.execSQL("INSERT INTO photos VALUES (null," + pho.getIdLocation()
                + ",'" + pho.getName() + "','" + pho.getPath() + "','"
                + sdf.format(pho.getDate()) + "')");
        db.close();
    }

    /**
     * Delete photo from the database
     *
     * @param id Id of the photo
     */
    public void deletePhoto(int id) {
        // Open the location database
        SQLiteDatabase db = getWritableDatabase();
        // Delete the photo from the database
        db.execSQL("DELETE FROM photos WHERE id=" + id);
        db.close();
    }

    /**
     * Get all the photos of a location
     *
     * @param idLocation Location Id
     * @return Vector with Photos
     */
    public Vector<PhotoModel> getPhotosByLocation(int idLocation) {
        Vector<PhotoModel> vphotos = new Vector<PhotoModel>();
        // Open the location database
        SQLiteDatabase db = getWritableDatabase();
        //Now Get all location photos
        Cursor c = db.rawQuery(" SELECT * FROM photos WHERE idLocation=" + idLocation, null);
        // If there are photos
        if (c.moveToFirst()) {
            do {
                try {
                    // Format the date
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss", new Locale("en", "US"));
                    Date date;
                    date = sdf.parse(c.getString(4));
                    // Add the photo in the vector
                    PhotoModel p = new PhotoModel(Integer.parseInt(c
                            .getString(0)), Integer.parseInt(c.getString(1)),
                            c.getString(2), c.getString(3), date);
                    vphotos.add(p);
                } catch (ParseException e) {
                    Log.e(TAG, "Error getting date");
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return vphotos;
    }

    /**
     * Get all the photos
     *
     * @return Vector with Photos
     */
    public Vector<PhotoModel> getAllPhotos() {
        Vector<PhotoModel> vphotos = new Vector<PhotoModel>();
        // Open the location database
        SQLiteDatabase db = getWritableDatabase();
        // Now get all the photos
        Cursor c = db.rawQuery(" SELECT * FROM photos", null);
        // If there are photos
        if (c.moveToFirst()) {
            do {
                try {
                    // Format the date
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss", new Locale("es", "ES"));
                    Date date;
                    date = sdf.parse(c.getString(4));
                    // Add the photo in the vector
                    PhotoModel p = new PhotoModel(Integer.parseInt(c
                            .getString(0)), Integer.parseInt(c.getString(1)),
                            c.getString(2), c.getString(3), date);
                    vphotos.add(p);
                } catch (ParseException e) {
                    Log.e(TAG, "Error getting date");
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return vphotos;
    }

}
