/*
 * Created by Atul Jalan
 * Date:: 1/8/2015
 * Fragment for Profile View and various options
 */
package com.example.ysdemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {
	TextView prof_name;
	SharedPreferences pref;
	Bitmap bitmap;
	ImageView prof_img;
	Button signout, post_tweet, tweet, search;
	EditText tweet_text, city, hashtag;
	ProgressDialog progress;
	Dialog tDialog;
	Twitter twitter;
	static double latitude;
	static double longitude;
	String tweetText;
	String hashText;
	String cityadd;
	String url;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle args) {
		View view = inflater.inflate(R.layout.profile_fragment, container,
				false);
		prof_name = (TextView) view.findViewById(R.id.prof_name);
		city = (EditText) view.findViewById(R.id.city);
		hashtag = (EditText) view.findViewById(R.id.hashtag);
		prof_img = (ImageView) view.findViewById(R.id.prof_image);
		tweet = (Button) view.findViewById(R.id.tweet);
		signout = (Button) view.findViewById(R.id.signout);
		search = (Button) view.findViewById(R.id.search);

		pref = getActivity().getPreferences(0);

		search.setOnClickListener(new search());
		signout.setOnClickListener(new SignOut());
		tweet.setOnClickListener(new Tweet());

		// to load profile details
		new LoadProfile().execute();

		// to get gps coordinates
		GPSTracker gps = new GPSTracker(getActivity());
		if (gps.canGetLocation()) {

			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			while (latitude == 0 && longitude == 0) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
			}

			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(getActivity(), Locale.getDefault());

			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
				cityadd = addresses.get(0).getLocality();
				city.setText(cityadd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}

		return view;
	}

	// To search Images using hashtags
	private class search implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			// Check if lat and long value is faulty or user have changed the
			// city name
			// both the cases will cause app to look for new lat long
			String addr = String.valueOf(city.getText());
			if ((latitude == 0 && longitude == 0) || addr.equals("")
					|| !(cityadd.equals(addr))) {

				url = "http://maps.google.com/maps/api/geocode/json?address="
						+ addr + "&sensor=false";
				double oldlat = latitude;

				getGeoPoint(url);
				while (oldlat == latitude) {

				}

			}

			SharedPreferences.Editor edit = pref.edit();
			hashText = String.valueOf(hashtag.getText());
			edit.putString("hashText", hashText);
			edit.putString("latitude", String.valueOf(latitude));
			edit.putString("longitude", String.valueOf(longitude));
			edit.commit();

			Fragment TweetImage = new TweetImageFragment();
			FragmentTransaction ft = getActivity().getFragmentManager()
					.beginTransaction();
			ft.replace(R.id.content_frame, TweetImage);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.addToBackStack(null);
			ft.commit();

			// String url =
			// "https://api.twitter.com/1.1/search/tweets.json?q=%23"+hashText+"&geocode="+latitude+","+longitude+",1mi";

		}

	}

	// Clearing auth token from sharedpreferences
	private class SignOut implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			SharedPreferences.Editor edit = pref.edit();
			edit.putString("ACCESS_TOKEN", "");
			edit.putString("ACCESS_TOKEN_SECRET", "");
			edit.commit();

			Fragment login = new LoginFragment();
			FragmentTransaction ft = getActivity().getFragmentManager()
					.beginTransaction();
			ft.replace(R.id.content_frame, login);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
			ft.commit();

		}

	}

	// to tweet
	private class Tweet implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			tDialog = new Dialog(getActivity());
			tDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			tDialog.setContentView(R.layout.tweet_dialog);
			tweet_text = (EditText) tDialog.findViewById(R.id.tweet_text);
			post_tweet = (Button) tDialog.findViewById(R.id.post_tweet);
			post_tweet.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new PostTweet().execute();
				}
			});

			tDialog.show();

		}
	}

	private class PostTweet extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(getActivity());
			progress.setMessage("Posting tweet ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			tweetText = tweet_text.getText().toString();
			progress.show();

		}

		protected String doInBackground(String... args) {

			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(pref.getString("CONSUMER_KEY", ""));
			builder.setOAuthConsumerSecret(pref
					.getString("CONSUMER_SECRET", ""));

			AccessToken accessToken = new AccessToken(pref.getString(
					"ACCESS_TOKEN", ""), pref.getString("ACCESS_TOKEN_SECRET",
					""));
			Twitter twitter = new TwitterFactory(builder.build())
					.getInstance(accessToken);

			try {
				twitter4j.Status response = twitter.updateStatus(tweetText
						+ " @learn2crack ");
				return response.toString();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String res) {
			if (res != null) {
				progress.dismiss();
				Toast.makeText(getActivity(), "Tweet Sucessfully Posted",
						Toast.LENGTH_SHORT).show();
				tDialog.dismiss();
			} else {
				progress.dismiss();
				Toast.makeText(getActivity(), "Error while tweeting !",
						Toast.LENGTH_SHORT).show();
				tDialog.dismiss();
			}

		}
	}

	// Load various elements of profile

	private class LoadProfile extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(getActivity());
			progress.setMessage("Loading Profile ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
			progress.show();

		}

		protected Bitmap doInBackground(String... args) {
			try {
				bitmap = BitmapFactory.decodeStream((InputStream) new URL(pref
						.getString("IMAGE_URL", "")).getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {
			Bitmap image_circle = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Bitmap.Config.ARGB_8888);

			BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
					TileMode.CLAMP);
			Paint paint = new Paint();
			paint.setShader(shader);
			Canvas c = new Canvas(image_circle);
			c.drawCircle(image.getWidth() / 2, image.getHeight() / 2,
					image.getWidth() / 2, paint);
			prof_img.setImageBitmap(image_circle);
			prof_name.setText("Welcome " + pref.getString("NAME", ""));

			progress.hide();

		}
	}

	public void getGeoPoint(final String urls) {
		// TODO Auto-generated method stub

		new Thread(new Runnable() {

			@Override
			public void run() {

				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				DefaultHttpClient httpClient = new DefaultHttpClient();
				Json json = new Json();
				String response = json.makeHttpRequest(urls, "POST", params,
						httpClient);

				try {
					JSONObject jObj = new JSONObject(response);
					longitude = ((JSONArray) jObj.get("results"))
							.getJSONObject(0).getJSONObject("geometry")
							.getJSONObject("location").getDouble("lng");
					latitude = ((JSONArray) jObj.get("results"))
							.getJSONObject(0).getJSONObject("geometry")
							.getJSONObject("location").getDouble("lat");

				}

				catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}

}