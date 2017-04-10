package com.jad.pholoc.fragments;

import java.util.Vector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jad.pholoc.R;
import com.jad.pholoc.model.LocationModel;
import com.jad.pholoc.util.LocationsSQLite;

/**
 * Fragment Locations map
 **/
public class MapLocFragment extends Fragment implements OnMapReadyCallback {

    public final String TAG = this.getClass().getSimpleName();

    private MapView mMapView;
    private Vector<LocationModel> vLoc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fmapaloc, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapLocations);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            // Initialize Map
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Map initialization failed");
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        // If there are localizations
        if (vLoc.size() > 0) {
            // Get locations
            for (int i = 0; i < vLoc.size(); i++) {
                // Clean the map
                map.clear();
                // Get location
                LocationModel loc = vLoc.get(i);
                // If the location has GPS coordinates
                if (!(loc.getLatitude() == 0 && loc.getLongitude() == 0)) {
                    // Get the coordinates of the location
                    LatLng coordinates = new LatLng(loc.getLatitude(),
                            loc.getLongitude());
                    // Mark the location and its name on the map
                    map.addMarker(new MarkerOptions().position(coordinates)
                            .title(loc.getName()));

					/*
                     * Marker marker = map.addMarker(new
					 * MarkerOptions().position(
					 * coordinates).title(loc.getName()));
					 */
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        // Open the location database
        LocationsSQLite dbloc = new LocationsSQLite(getActivity(),
                "DBLocations.db", null, 1);
        Log.i("MapLoc", "Lista de localizaciones actualizadas");
        // Get the locations of the database
        vLoc = dbloc.getAllLocations();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
