package com.jad.pholoc;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Main Activity
 * 
 * @author Jorge Alvarado
 * 
 */
public class MainActivity extends AppCompatActivity {

	public final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Quitar....
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Intent i;
		int id = item.getItemId();
		switch (id){
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
	 * Open NewLocation Activity
	 * 
	 * @param view View
	 */
	public void openNewLocation(View view) {
		Intent i = new Intent(this, NewLocationActivity.class);
		startActivity(i);
	}

	/**
	 * Open Locations Activity
	 * 
	 * @param view View
	 */
	public void openLocations(View view) {
		Intent i = new Intent(this, LocationsActivity.class);
		startActivity(i);
	}

	/**
	 * Open Photo Gallery Activity
	 * 
	 * @param view View
	 */
	public void openPhotoGallery(View view) {
		Intent i = new Intent(this, LocationGalleryActivity.class);
		i.putExtra("idLocation", 0);
		startActivity(i);
	}
}
