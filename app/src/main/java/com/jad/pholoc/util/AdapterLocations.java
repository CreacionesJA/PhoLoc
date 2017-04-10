package com.jad.pholoc.util;

import java.text.DecimalFormat;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jad.pholoc.R;
import com.jad.pholoc.model.LocationModel;

/**
 * Adapter for custom location list
 * 
 * @author Jorge Alvarado
 * 
 **/
public class AdapterLocations extends BaseAdapter {
	protected Activity activity;
	protected Vector<LocationModel> items;

	public AdapterLocations(Activity activity, Vector<LocationModel> items) {
		this.activity = activity;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Generate a convertView for efficiency reasons
		View v = convertView;

		// We associate the layout of the list that we have created
		if (convertView == null) {
			LayoutInflater inf = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inf.inflate(R.layout.locationlistelement, null);
		}

		LocationModel loc = items.get(position);

		// Fill in the name in the large text of the item in the list
		TextView nombre = (TextView) v.findViewById(R.id.txt_nombreloc);
		nombre.setText(loc.getName());
		// Fill in the coordinates
		DecimalFormat formato = new DecimalFormat("0.000000");
		String coordenadas = "";
		// If the location has no coordinates
		if (loc.getLatitude() == 0 && loc.getLongitude() == 0) {
			// Indicate that it has no coordinates
			coordenadas = activity.getResources().getString(
					R.string.txt_infolocation_withoutcoordinates);
		} else {
			// Put the coordinates (latitude | longitude)
			coordenadas = formato.format(loc.getLatitude()) + " | "
					+ formato.format(loc.getLongitude());
		}
		// Show the coordinates in the small text of the item in the list
		TextView descripcion = (TextView) v.findViewById(R.id.txt_descloc);
		descripcion.setText(coordenadas);

		return v;
	}
}
