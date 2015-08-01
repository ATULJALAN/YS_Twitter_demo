/*
 * Created by Atul Jalan
 * Date:: 1/8/2015
 * 
 * Main activity to host all the Fragments 
 * 1. Login Fragment
 * 2. Profile Fragment
 * 3. TweetImage Fragment
 */
package com.example.ysdemo;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;

public class Main extends Activity {

	// to store important data, we don't have that much data to store, no need
	// for a database
	SharedPreferences pref;

	// Defining YSDemo app autho keys
	private static String CONSUMER_KEY = "1kq8VWEV9O6VQH2YZT3NwlReK";
	private static String CONSUMER_SECRET = "aqhKbx5UifPKXfrpJ3LcaK4EeZE2TyRXxBAQek8YV6YiBKilgP";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		pref = getPreferences(0);
		SharedPreferences.Editor edit = pref.edit();
		edit.putString("CONSUMER_KEY", CONSUMER_KEY);
		edit.putString("CONSUMER_SECRET", CONSUMER_SECRET);
		edit.commit();

		// Creating the first fragment for user authentication
		Fragment login = new LoginFragment();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_frame, login);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.addToBackStack(null);
		ft.commit();
	}

}
