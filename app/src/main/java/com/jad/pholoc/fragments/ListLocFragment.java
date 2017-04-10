package com.jad.pholoc.fragments;

import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.jad.pholoc.InfoLocationActivity;
import com.jad.pholoc.R;
import com.jad.pholoc.model.LocationModel;
import com.jad.pholoc.util.AdapterLocations;
import com.jad.pholoc.util.LocationsSQLite;

/**
 * Fragment list of locations
 *
 * @author Jorge Alvarado
 **/
public class ListLocFragment extends Fragment {

    public final String TAG = this.getClass().getSimpleName();
    private ListView listaloc;
    private Vector<LocationModel> vloc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.flistaloc, container, false);
        listaloc = (ListView) v.findViewById(R.id.listaLocations);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Open the location database
        LocationsSQLite dbloc = new LocationsSQLite(getActivity(),
                "DBLocations.db", null, 1);
        // We get the locations of the database
        vloc = dbloc.getAllLocations();
        // Refill the list again
        refillList();
        Log.i(TAG, "Location list updated");
    }

    /**
     * Fill list with locations
     */
    public void refillList() {

        // If there are locations
        if (vloc.size() > 0) {
            // Create an instance of the Custom Adapter for the list
            AdapterLocations adapter = new AdapterLocations(getActivity(), vloc);
            listaloc.setAdapter(adapter);
            listaloc.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    // Open Activity with location information
                    LocationModel loc = vloc.get(position);
                    Intent i = new Intent(getActivity(),
                            InfoLocationActivity.class);
                    i.putExtra("idLocation", loc.getId());
                    startActivity(i);
                }
            });
        } else {
            // If there are no locations we show a Toast indicating it
            mostrarToast(getResources().getString(R.string.txt_locations_nothing));
            Log.i(TAG, "There are no locations in the DB");
            // Close the Activity
            getActivity().finish();
        }
    }

    /**
     * Show toast
     *
     * @param message Message
     */
    public void mostrarToast(String message) {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

}