package com.teenscribblers.bubble_todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.parse.ParseUser;

public class UserActivityChooser extends SherlockFragmentActivity {
	public UserActivityChooser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Check if there is current user info
		if (ParseUser.getCurrentUser() != null) {
			// Start an intent for the logged in activity
			final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				buildAlertMessageNoGps();
			} else
				startActivity(new Intent(this, MainActivity.class));
		} else {
			// Start and intent for the logged out activity
			startActivity(new Intent(this, SignUpOrLogInActivity.class));
		}
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									@SuppressWarnings("unused") final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}
}
