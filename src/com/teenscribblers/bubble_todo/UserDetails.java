package com.teenscribblers.bubble_todo;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teenscribblers.here_post.R;

public class UserDetails extends SherlockFragmentActivity {
	TextView user, currentnote, tailtext, datetime;
	Todolist note = new Todolist();
	String id = null, user_id = null, created_date = null;
	ParseImageView dp;
	ProgressBar p;
	ParseUser useruser;
	ParseFile photoFile;
	Button up, down;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);
		user = (TextView) findViewById(R.id.user);
		currentnote = (TextView) findViewById(R.id.detailnote);
		tailtext = (TextView) findViewById(R.id.tailtext);
		datetime = (TextView) findViewById(R.id.datetime);
		p = (ProgressBar) findViewById(R.id.progressBardp);
		up = (Button) findViewById(R.id.buttonup);
		down = (Button) findViewById(R.id.buttondown);
		String[] s = getIntent().getStringArrayExtra("oid");
		id = s[2];
		user_id = s[3];
		created_date = s[4];
		user.setText(s[1]);
		currentnote.setText(s[0]);
		tailtext.setText(s[5]);
		datetime.setText(created_date);
		querylist(user_id);
		photoquery(user_id);
		up.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ratequery("likes");
			}
		});
		down.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ratequery("dislikes");
			}
		});
	}

	protected void ratequery(final String column) {
		// TODO Auto-generated method stub
		ParseQuery<Todolist> trying = Todolist.getQuery();
		trying.getInBackground(id, new GetCallback<Todolist>() {

			@Override
			public void done(Todolist object, ParseException e) {
				// TODO Auto-generated method stub
				JSONArray ab = object.getJSONArray(column);
				int length = 0;
				int find = 0;

				if (ab == null) {
					length = 0;
				} else {
					length = ab.length();
					Log.e("length", String.valueOf(length));
				}

				for (int i = 0; i < length; i++) {
					try {
						if (ab.getString(i).equals(
								ParseUser.getCurrentUser().getObjectId())) {
							find = 1;
							Log.e("inside_if", ab.getString(i));
							break;
						} else {
							find = 0;
							Log.e("inside_else", ab.getString(i));
						}

					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if (find == 0) {
					object.saveInBackground();
					object.addUnique(column, ParseUser.getCurrentUser()
							.getObjectId());
					object.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException e) {
							// TODO Auto-generated method stub
							if (e == null) {
								Toast.makeText(getBaseContext(), "You did it",
										Toast.LENGTH_SHORT).show();
								up.setVisibility(View.GONE);
								down.setVisibility(View.GONE);
							} else {
								Toast.makeText(getBaseContext(), e.toString(),
										Toast.LENGTH_LONG).show();
							}
						}

					});
				} else {
					Toast.makeText(getBaseContext(), "You already did it",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	protected void photoquery(String id2) {
		// starting doing things with image
		dp = (ParseImageView) findViewById(R.id.userdp);

		ParseQuery<ParseUser> query1 = ParseUser.getQuery();

		query1.getInBackground(id2, new GetCallback<ParseUser>() {

			@Override
			public void done(ParseUser object, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					useruser = object;
					photoFile = useruser.getParseFile("displaypic");
					if (photoFile != null) {
						dp.setParseFile(photoFile);
						dp.loadInBackground(new GetDataCallback() {

							@Override
							public void done(byte[] data, ParseException e) {
								// TODO Auto-generated method stub
								p.setVisibility(View.GONE);
							}
						});
					} else {
						p.setVisibility(View.GONE);
						dp.setBackgroundResource(android.R.drawable.presence_offline);
					}
				} else
					Log.i("else", e.getMessage());
			}
		});

	}

	protected void querylist(final String id) {
		// TODO Auto-generated method stub
		Userpostlistadapter b = new Userpostlistadapter(getBaseContext(),
				user_id);
		ListView list = (ListView) findViewById(R.id.usernotes);
		list.setAdapter(b);

	}
}
