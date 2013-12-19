package com.teenscribblers.bubble_todo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.teenscribblers.here_post.R;

public class Userpostlistadapter extends ParseQueryAdapter<Todolist> {
	static String ids;
	Todolist t;

	public Userpostlistadapter(final Context context, final String id) {
		super(context, new ParseQueryAdapter.QueryFactory<Todolist>() {
			public ParseQuery<Todolist> create() {
				// Here we can configure a ParseQuery to display
				// only top-rated newss.
				ids = id;

				ParseQuery<Todolist> query = ParseQuery.getQuery("Todolist");

				try {
					query.whereEqualTo("user", ParseUser.getQuery().get(id));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return query;
			}
		});
	}

	@Override
	public View getItemView(Todolist todo, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.ts_post_item, null);
		}

		super.getItemView(todo, v, parent);

		TextView titleTextView = (TextView) v.findViewById(R.id.contentView);
		titleTextView.setText(todo.getText());
		TextView ratingTextView = (TextView) v.findViewById(R.id.usernameView);
		ratingTextView.setText(todo.getTail());
		return v;
	}

}
