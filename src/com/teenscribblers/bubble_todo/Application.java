package com.teenscribblers.bubble_todo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;
import com.parse.Parse;
import com.parse.ParseObject;

public class Application extends android.app.Application {
	// Debugging switch
	public static final boolean APPDEBUG = false;

	// Debugging tag for the application
	public static final String APPTAG = "Nearby Todo";

	// Key for saving the search distance preference
	private static final String KEY_SEARCH_DISTANCE = "searchDistance";
	private static final String KEY_MAP_TYPE = "maptype";
	private static SharedPreferences preferences, pref;

	public Application() {
	}

	@Override
	public void onCreate() {
		super.onCreate();

		ParseObject.registerSubclass(Todolist.class);
		Parse.initialize(this, "tMC4UpC7yBFbwZsqLnKK1hswoNXeHvdipVzHE58I",
				"SeW5Vffx3jtLpOdtmERkjKmIqpkmfN79SDSOhkoM");
		preferences = getSharedPreferences("com.teenscribblers.bubbletodo",
				Context.MODE_PRIVATE);
		pref = getSharedPreferences("com.teenscribblers.bubbletodo",
				Context.MODE_PRIVATE);
	}

	public static float getSearchDistance() {
		return preferences.getFloat(KEY_SEARCH_DISTANCE, 250);
	}

	public static void setSearchDistance(float value) {
		preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
	}

	public static int getMaptype() {
		return pref.getInt(KEY_MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL);
	}

	public static void setmaptype(int value) {
		pref.edit().putInt(KEY_MAP_TYPE, value).commit();
	}
}
