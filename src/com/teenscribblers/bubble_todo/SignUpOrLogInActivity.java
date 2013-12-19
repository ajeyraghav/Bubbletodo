package com.teenscribblers.bubble_todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.teenscribblers.here_post.R;

/**
 * Activity which displays a registration screen to the user.
 */
public class SignUpOrLogInActivity extends SherlockFragmentActivity {
	Button login, signup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_or_log_in);
		login = (Button) findViewById(R.id.logInButton);
		signup = (Button) findViewById(R.id.signUpButton);
		// Log in button click handler
		login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Starts an intent of the log in activity
				startActivity(new Intent(SignUpOrLogInActivity.this,
						LoginActivity.class));
			}
		});

		// Sign up button click handler
		signup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Starts an intent for the sign up activity
				startActivity(new Intent(SignUpOrLogInActivity.this,
						SignUpActivity.class));
			}
		});
	}
}
