package com.jad.pholoc;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * About Activity
 * 
 * @author Jorge Alvarado
 * 
 */
public class AboutActivity extends AppCompatActivity{

	public final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Put the back arrow on the action bar
		ActionBar actionBar = getSupportActionBar();
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}

		// Get the current version of the app
		String version;
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			version = pInfo.versionName;
		} catch (Exception e) {
			version = "1.0";
		}
		// Show current version of the app
		TextView txtVersion = (TextView) findViewById(R.id.txtVersion);
		txtVersion.setText(String.format(getResources().getString(R.string.app_version), version));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (item.getItemId() == android.R.id.home){
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
