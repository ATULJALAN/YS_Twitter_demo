/*
 * Created by Atul Jalan
 * Date:: 1/8/2015
 * Welcome Class
 */
package com.example.ysdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

	// Splash Screen timer
	private static int SPLASH_TIME_OUT = 1000;

	// *************Splash Screen Starts*****************//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// This method will be executed once the timer is over

				// Starting your app main activity
				Intent i = new Intent(Splash.this, Main.class);
				startActivity(i);

				// close this activity
				finish();

			}
		}, SPLASH_TIME_OUT);
	}

}
