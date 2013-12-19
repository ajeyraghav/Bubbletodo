package com.teenscribblers.bubble_todo;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.teenscribblers.here_post.R;

/**
 * Activity which displays a login screen to the user.
 */
public class SignUpActivity extends SherlockFragmentActivity {
	// UI references.
	private EditText usernameView;
	private EditText passwordView;
	private EditText passwordAgainView, email;
	ParseUser user;
	private ParseImageView currentuserdp;
	ParseFile photoFile;
	byte[] inputData = null;
	private static final int CAMERA_PIC_REQUEST = 1337;
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_signup);

		// Set up the signup form.
		usernameView = (EditText) findViewById(R.id.username);
		passwordView = (EditText) findViewById(R.id.password);
		passwordAgainView = (EditText) findViewById(R.id.passwordAgain);
		email = (EditText) findViewById(R.id.email);
		currentuserdp = (ParseImageView) findViewById(R.id.default_user_pic);

		// Set up the submit button click handler
		findViewById(R.id.action_button).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {

						// Validate the sign up data
						boolean validationError = false;
						StringBuilder validationErrorMessage = new StringBuilder(
								getResources().getString(R.string.error_intro));
						if (isEmpty(usernameView)) {
							validationError = true;
							validationErrorMessage.append(getResources()
									.getString(R.string.error_blank_username));
						}
						if (isEmpty(passwordView)) {
							if (validationError) {
								validationErrorMessage.append(getResources()
										.getString(R.string.error_join));
							}
							validationError = true;
							validationErrorMessage.append(getResources()
									.getString(R.string.error_blank_password));
						}
						if (isEmpty(email)) {
							if (validationError) {
								validationErrorMessage.append(getResources()
										.getString(R.string.error_join));
							}
							validationError = true;
							validationErrorMessage.append("Blank Email");
						}
						if (!isMatching(passwordView, passwordAgainView)) {
							if (validationError) {
								validationErrorMessage.append(getResources()
										.getString(R.string.error_join));
							}
							validationError = true;
							validationErrorMessage
									.append(getResources()
											.getString(
													R.string.error_mismatched_passwords));
						}
						validationErrorMessage.append(getResources().getString(
								R.string.error_end));

						// If there is a validation error, display the error
						if (validationError) {
							Toast.makeText(SignUpActivity.this,
									validationErrorMessage.toString(),
									Toast.LENGTH_LONG).show();
							return;
						}

						// Set up a progress dialog
						final ProgressDialog dlg = new ProgressDialog(
								SignUpActivity.this);
						dlg.setTitle("Please wait.");
						dlg.setMessage("Signing up.  Please wait...");
						dlg.show();

						// Set up a new Parse user
						ParseUser user = new ParseUser();
						user.setUsername(usernameView.getText().toString());
						user.setPassword(passwordView.getText().toString());
						user.setEmail(email.getText().toString());
						user.put("displaypic", photoFile);
			
						// Call the Parse signup method
						user.signUpInBackground(new SignUpCallback() {

							@Override
							public void done(ParseException e) {
								dlg.dismiss();
								if (e != null) {
									// Show the error message
									Toast.makeText(SignUpActivity.this,
											e.getMessage(), Toast.LENGTH_LONG)
											.show();
								} else {
									// Start an intent for the home activity
									Intent intent = new Intent(
											SignUpActivity.this,
											UserActivityChooser.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent);
								}
							}
						});
					}
				});
		currentuserdp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, CAMERA_PIC_REQUEST);
			}
		});
	}

	private boolean isEmpty(EditText etText) {
		if (etText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isMatching(EditText etText1, EditText etText2) {
		if (etText1.getText().toString().equals(etText2.getText().toString())) {
			return true;
		} else {
			return false;
		}
	}

	// lvpslvpslvsdzvbs,vbl,bl'sd,bl;s,b;lsd,b
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {

				pDialog = new ProgressDialog(this);
				pDialog.setMessage("Wait,Boiling Maggi!");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
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
				Toast.makeText(this, "Cancelled!Change profile pic later..",
						Toast.LENGTH_SHORT).show();
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
		photoFile = new ParseFile("dp_" + usernameView.getText().toString()
				+ ".jpg", scaledData);
		photoFile.saveInBackground(new SaveCallback() {

			public void done(ParseException e) {
				if (e != null) {
					Toast.makeText(getBaseContext(),
							"Error saving: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
					pDialog.dismiss();
				} else {

					pDialog.dismiss();
					currentuserdp.setParseFile(photoFile);
					currentuserdp.loadInBackground();

				}
			}
		});
	}

}
