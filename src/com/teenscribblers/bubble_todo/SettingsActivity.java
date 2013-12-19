package com.teenscribblers.bubble_todo;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.maps.GoogleMap;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teenscribblers.here_post.R;

/**
 * Activity that displays the settings screen.
 */
public class SettingsActivity extends SherlockFragmentActivity {

	private RadioGroup searchDistanceGroup, maptypegroup;
	ProgressBar pb;
	ParseUser user;
	private ParseImageView currentuserdp;
	ParseFile photoFile;
	byte[] inputData = null;
	private static final int CAMERA_PIC_REQUEST = 1337;
	ProgressDialog pDialog;
	TextView currentuser, currentemail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		currentuserdp = (ParseImageView) findViewById(R.id.currentuserdp);
		pb = (ProgressBar) findViewById(R.id.pbondp);
		user = new ParseUser();
		//
		ParseUser user = new ParseUser();
		user = ParseUser.getCurrentUser();
		// putting user details
		currentuser = (TextView) findViewById(R.id.userinsettings);
		currentuser.setText(user.getUsername());
		currentemail = (TextView) findViewById(R.id.emailinsettings);
		currentemail.setText(user.getEmail());
		//
		ParseFile photoFile = user.getParseFile("displaypic");
		if (photoFile != null) {
			currentuserdp.setParseFile(photoFile);
			currentuserdp.loadInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] data, ParseException e) {
					// TODO Auto-generated method stub
					pb.setVisibility(View.GONE);
				}
			});
		}

		//
		// The search distance choices
		searchDistanceGroup = (RadioGroup) findViewById(R.id.searchDistanceGroup);
		maptypegroup = (RadioGroup) findViewById(R.id.radioGroupmap);
		float searchDistance = Application.getSearchDistance();
		int typem = Application.getMaptype();
		if (searchDistance > 1500f) {
			searchDistanceGroup.check(R.id.feet1500Button);
		} else if (searchDistance > 1000f) {
			searchDistanceGroup.check(R.id.feet1000Button);
		} else {
			searchDistanceGroup.check(R.id.feet100Button);
		}
		// checking condition for map type
		if (typem == GoogleMap.MAP_TYPE_NORMAL) {
			maptypegroup.check(R.id.normal);
		} else if (typem == GoogleMap.MAP_TYPE_HYBRID) {
			maptypegroup.check(R.id.hybrid);
		} else if (typem == GoogleMap.MAP_TYPE_SATELLITE) {
			maptypegroup.check(R.id.satellite);
		} else {
			maptypegroup.check(R.id.terrain);
		}
		// Set up the selection handler to save the selection to the application
		searchDistanceGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.feet1500Button:
							Application.setSearchDistance(4000);
							break;
						case R.id.feet1000Button:
							Application.setSearchDistance(1000);
							break;
						case R.id.feet100Button:
							Application.setSearchDistance(250);
							break;
						}
					}
				});

		// set up selection handler to save the selection of map type
		maptypegroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.normal:
					Application.setmaptype(GoogleMap.MAP_TYPE_NORMAL);
					break;
				case R.id.hybrid:
					Application.setmaptype(GoogleMap.MAP_TYPE_HYBRID);
					break;
				case R.id.satellite:
					Application.setmaptype(GoogleMap.MAP_TYPE_SATELLITE);
					break;
				case R.id.terrain:
					Application.setmaptype(GoogleMap.MAP_TYPE_TERRAIN);
					break;
				}
			}
		});
		currentuserdp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setDp();
			}
		});
		// Set up the log out button click handler
		((Button) findViewById(R.id.logOutButton))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// Call the Parse log out method
						ParseUser.logOut();
						// Start and intent for the dispatch activity
						Intent intent = new Intent(SettingsActivity.this,
								UserActivityChooser.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
		currentuser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Create the builder where the new post is entered
				AlertDialog.Builder alert = new AlertDialog.Builder(
						SettingsActivity.this);
				alert.setTitle("Change Username");
				final EditText input = new EditText(SettingsActivity.this);
				input.setInputType(InputType.TYPE_CLASS_TEXT);
				alert.setView(input);
				// Handle the dialog input
				alert.setPositiveButton("Set",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								ParseUser user = ParseUser.getCurrentUser();
								user.setUsername(input.getText().toString());
							}
						});
				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(getBaseContext(), "No Change",
										Toast.LENGTH_SHORT).show();
							}
						});
				alert.create().show();

			}
		});
	}

	protected void setDp() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, CAMERA_PIC_REQUEST);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {

				pDialog = new ProgressDialog(this);
				pDialog.setMessage("Wait,Boiling Maggi!");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
				Uri uri = data.getData();
				InputStream iStream = null;
				try {
					iStream = getContentResolver().openInputStream(uri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					inputData = getBytes(iStream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					saveScaledPhoto(inputData);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private byte[] getBytes(InputStream iStream) throws IOException {
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		int len = 0;
		while ((len = iStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}
		return byteBuffer.toByteArray();
	}

	/*
	 * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
	 * they are saved. Since we never need a full-size image in our app, we'll
	 * save a scaled one right away.
	 */
	private void saveScaledPhoto(byte[] data) throws IOException {

		// Resize photo from camera byte array
		Bitmap mealImage = BitmapFactory.decodeByteArray(data, 0, data.length);
		Bitmap mealImageScaled = Bitmap.createScaledBitmap(mealImage, 400, 400
				* mealImage.getHeight() / mealImage.getWidth(), false);
		Bitmap rotatedScaledMealImage = mealImageScaled;

		if (mealImageScaled.getWidth() < mealImageScaled.getHeight()) {
			// Override Android default landscape orientation and save portrait
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			rotatedScaledMealImage = Bitmap.createBitmap(mealImageScaled, 0, 0,
					mealImageScaled.getWidth(), mealImageScaled.getHeight(),
					matrix, true);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

		byte[] scaledData = bos.toByteArray();

		// Save the scaled image to Parse
		photoFile = new ParseFile("dp_"
				+ ParseUser.getCurrentUser().getUsername() + ".jpg", scaledData);
		photoFile.saveInBackground(new SaveCallback() {

			public void done(ParseException e) {
				if (e != null) {
					Toast.makeText(getBaseContext(),
							"Error saving: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
					pDialog.dismiss();
				} else {

					user = ParseUser.getCurrentUser();
					user.put("displaypic", photoFile);
					user.saveInBackground();
					pDialog.dismiss();
					currentuserdp.setParseFile(photoFile);
					currentuserdp.loadInBackground();

				}
			}
		});
	}

}
