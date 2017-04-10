package com.jad.pholoc;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jad.pholoc.model.LocationModel;
import com.jad.pholoc.util.HttpClient;
import com.jad.pholoc.util.LocationsSQLite;
import com.jad.pholoc.util.RequestMethod;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * New Location Activity
 *
 * @author Jorge
 */
public class NewLocationActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();

    private boolean locFound;
    private String provider;
    private String providerSettings;
    private String address;
    private double latitude;
    private double longitude;
    private TextView lblStatus;
    private EditText tfdNameLoc;
    private EditText tdfNotesLoc;
    private Button btAddLoc;
    private LocationManager locManager;
    private LocationListener locListener;
    private SharedPreferences prefs;
    private getLocationCity getLocationCity;
    private NewLocationActivity.waitGPS waitGPS;
    final Activity mActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlocation);

        // Put the back arrow on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Get the preferences
        prefs = getSharedPreferences("com.jad.pholoc_preferences",
                Context.MODE_PRIVATE);
        // Set default values for a location without GPS coordinates
        locFound = false;
        address = "Sin datos";
        latitude = 0;
        longitude = 0;

        // Initialize views
        initializeViews();
    }

    /**
     * Initialize views
     */
    private void initializeViews() {
        lblStatus = (TextView) findViewById(R.id.lbl_estadogps);
        tfdNameLoc = (EditText) findViewById(R.id.tfd_nomloc);
        tdfNotesLoc = (EditText) findViewById(R.id.tfd_notasloc);
        btAddLoc = (Button) findViewById(R.id.bt_addloc);

        // Text Changed Listener
        tfdNameLoc.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Check that the location starts with a letter or number
                Pattern pat = Pattern.compile("^[\\w].*");
                Matcher mat = pat.matcher(tfdNameLoc.getText().toString());
                if (mat.matches()) {
                    // Activate the add button (name of location is valid)
                    btAddLoc.setEnabled(true);
                } else {
                    // If not, disable it
                    btAddLoc.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocalizacion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Si el provider es valido (no es null ni es el provider pasivo)
        if (provider != null && !provider.equals(LocationManager.PASSIVE_PROVIDER)) {
            // Paramos notificaciones de localizacion
            locManager.removeUpdates(locListener);
            // SI la espera gps esta activa
            if (waitGPS != null) {
                // La paramos
                waitGPS.cancel(true);
            }
        }
    }

    /**
     * Add new localizacion
     *
     * @param view View
     */
    public void addLocalizacion(View view) {
        // Open the location database
        LocationsSQLite dbLoc = new LocationsSQLite(this, "DBLocations.db",
                null, 1);
        /* The path (folder where the photos of the location will be stored) is the name of the
         * location but without tildes, without spaces or special characters.
         */
        String path = prepareDirectoryName(tfdNameLoc.getText().toString());
        /*
         * Make the operation to add location and we obtain the result
         * of the operation in the resultCode variable
         */
        LocationModel loc = new LocationModel(1,
                tfdNameLoc.getText().toString(), address, tdfNotesLoc
                .getText().toString(), latitude, longitude, path);
        int resultCode = dbLoc.addLocation(loc);
        switch (resultCode) {
            case 0:
                // Result Code 0 -> Add the location
                showToast(String.format(
                        getResources().getString(R.string.txt_newlocation_add_ok),
                        loc.getName()));
                Log.i(TAG, "Localization added");
                this.finish();
                break;
            case -1:
                // Result Code -1 -> There is already a location with the same name
                showToast(getResources().getString(
                        R.string.txt_newlocation_exists));
                break;
            default:
                // Unspecified error
        }
    }

    // ===============================================================

    /**
     * Begin the localization process
     */
    private void startLocalizacion() {
        // Xheck if we have location permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Location Started");
            // Get a reference to LocationManager
            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Get the localization method to use from the preferences
            providerSettings = prefs.getString("mLoc", "auto");
            switch (providerSettings) {
                case "auto":
                    // Method: Auto
                    // Establish criteria to select the best location provider
                    Criteria criteria = new Criteria();
                    criteria.setCostAllowed(false);
                    criteria.setAltitudeRequired(false);
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    // Get the best location provider
                    provider = locManager.getBestProvider(criteria, true);
                    Log.i(TAG, "Location Method: AUTO");
                    break;
                case "gps":
                    // Method: AGPS
                    provider = LocationManager.GPS_PROVIDER;
                    Log.i(TAG, "Location Method: GPS");
                    break;
                default:
                    // Method: Network
                    provider = LocationManager.NETWORK_PROVIDER;
                    Log.i(TAG, "Location Method: RED");
                    break;
            }

            // If the provider is valid (not null and not the passive provider)
            if (provider != null && !provider.equals(LocationManager.PASSIVE_PROVIDER)) {
                // Get the last known position
                Location loc = locManager.getLastKnownLocation(provider);
                // Show the last known position
                showLocation(loc);
                // Listener to receive position updates
                locListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        showLocation(location);
                    }

                    public void onProviderDisabled(String provider) {

                        if (providerSettings.equals("gps")) {
                            // If the GPS is deactivated, notify it (method location: GPS)
                            lblStatus.setText(R.string.txt_newlocation_gps_off);
                        } else {
                            // If we do not notify that the location is disabled
                            lblStatus.setText(R.string.txt_newlocation_loc_off);
                        }
                    }

                    public void onProviderEnabled(String provider) {

                    }

                    public void onStatusChanged(String provider, int status,
                                                Bundle extras) {
                        Log.i(TAG, "Provider Status: " + status);
                    }
                };

                // Request Location Updates every 30 seconds
                locManager.requestLocationUpdates(provider, 30000, 0, locListener);

                // If the current provider is GPS (location method: AUTO) and there is network provider
                if (provider.equals(LocationManager.GPS_PROVIDER)
                        && providerSettings.equals("auto") && isOnline()) {
                    waitGPS = new waitGPS();
                    List<String> providers = locManager.getProviders(true);
                    for (String p : providers) {
                        if (p.equals(LocationManager.NETWORK_PROVIDER)) {
                            // We start the GPS wait
                            waitGPS.execute();
                        }
                    }
                }
            } else {
                // If it is null or passive, we advise that the location is disabled
                showToast(getResources().getString(R.string.txt_newlocation_loc_off));
                // And close the activity
                this.finish();
            }
        } else {
            // Request permission for access to the location
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    /**
     * Show Location
     *
     * @param loc Location
     */
    private void showLocation(Location loc) {
        // Localization found
        if (loc != null) {
            Log.i(TAG, "Localization found");
            locFound = true;
            // Get latitude and longitude
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();

            // We launch the asynchronous task that is in charge of obtaining the location city
            getLocationCity = new getLocationCity();
            getLocationCity.execute();
            Log.i(TAG,
                    "Coordinates: "
                            + String.valueOf(loc.getLatitude() + " - "
                            + String.valueOf(loc.getLongitude())));
        } else {
            lblStatus.setText(R.string.txt_newlocation_statusloc);
        }
    }

    /**
     * Asynchronous task to obtain the population given the coordinates
     */
    private class getLocationCity extends AsyncTask<Void, Void, Void> {
        // City name
        private String city;
        // Indicator if there is any error in the process
        private boolean error;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Inicializamos las variables
            city = "";
            address = "No se han econtrado resultados";
            error = false;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Google Geocoding API
            String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + latitude + "," + longitude + "&sensor=true";

            // Get JSON Google Geocoding API
            String jsonStr = HttpClient.openURL(url, RequestMethod.GET);

            //Log.d("Response: ", "> " + jsonStr);

            // Response received
            if (jsonStr != null) {
                try {
                    boolean locFound = false;
                    // Get the JSON object
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Get the name of the city from the JSON object
                    JSONArray results = jsonObj.getJSONArray("results");
                    JSONObject dir = results.getJSONObject(0);
                    address = dir.getString("formatted_address");
                    for (int i = 0; i < results.length() && !locFound; i++) {
                        JSONArray componentes = results.getJSONObject(i)
                                .getJSONArray("address_components");
                        for (int j = 0; j < componentes.length()
                                && !locFound; j++) {
                            JSONObject c = componentes.getJSONObject(j);
                            if (c.getString("types").equals(
                                    "[\"locality\",\"political\"]")) {
                                // Get the name of the city
                                city = c.getString("short_name");
                                locFound = true;
                            }
                        }
                    }

                } catch (JSONException e) {
                    error = true;
                    Log.e(TAG, "Error reading JSON");
                }
            } else {
                // If no response has been received from the server
                error = true;
                Log.e(TAG, "Unable to get JSON");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            // If there are no errors in the process
            if (!error) {
                // Show the name of the city
                tfdNameLoc.setText(city);
                lblStatus.setText(String.format(
                        getResources().getString(
                                R.string.txt_newlocation_loccoordinates),
                        address));
            } else {
                // If there were errors, it indicates
                showToast(getResources().getString(
                        R.string.txt_newlocation_errorjson));
                lblStatus.setText(R.string.txt_newlocation_loc_error);
            }
            // And finally stop location updates to save battery
            locManager.removeUpdates(locListener);
        }
    }

    /**
     * Asynchronous task to change the location provider to Network if the GPS does not respond
     * in the seconds established in the preferences
     */
    private class waitGPS extends AsyncTask<Void, Void, Void> {
        int seconds;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                // We get the seconds set in the preferences
                seconds = Integer.parseInt(prefs.getString("tGps", "60"));
            } catch (NumberFormatException e) {
                seconds = 60;
            }
            Log.i(TAG, seconds
                    + "-second wait for GPS started");
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                // Wait x seconds for the gps to respond
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
                Log.w(TAG, "GPS Wait Canceled");
            } catch (Exception e) {
                Log.e(TAG, "GPS wait failed");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            // If we can not find the location and we have location permissions
            if (!locFound &&
                    (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                // Switch to Network provider
                provider = LocationManager.NETWORK_PROVIDER;
                // We assign it to locListener
                locManager.requestLocationUpdates(provider, 30000, 0,
                        locListener);
                Log.i(TAG, "Location Provider changed to Network");
            } else {
                Log.i(TAG, "Unchanged location provider");
            }
        }
    }

    /**
     * A method that removes special characters, substitutes spaces for _ and removes possible accents
     * from a string and returns the string only with alphanumeric characters and hyphens - and _ to n
     * ame the location directory
     *
     * @param directory Directory name
     * @return Name of the directory without tildes, neither spaces nor special characters
     */
    public String prepareDirectoryName(String directory) {
        // Remove spaces
        directory = directory.replace(' ', '_');
        // Normalize in the form NFD (Canonical decomposition)
        directory = java.text.Normalizer.normalize(directory,
                java.text.Normalizer.Form.NFD);
        // We replace the accents with a regular expression of Unicode Block
        String directoryName = directory.replaceAll(
                "\\p{InCombiningDiacriticalMarks}+", "");
        // And finally we only have the characters A-Z, a-z, 0-9, - and _
        directoryName = directoryName.replaceAll("[^A-Za-z0-9-_]", "");
        return directoryName;
    }

    /**
     * Check for internet connection
     *
     * @return True if there is connection or false if there is not
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_location, menu);
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
