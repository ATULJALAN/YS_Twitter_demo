/*
 * Created by Atul Jalan
 * Date:: 1/8/2015
 *  Fragment to search tweet images and show them
 */
package com.example.ysdemo;

import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

public class TweetImageFragment extends Fragment {

	final int count = 20; // no. of images to retrieve
	final int radius = 100; // radius around place where to look (in kilometers)
	SharedPreferences pref;
	Bitmap bitmap;
	ImageView prof_img;
	ListView l2;

	ProgressDialog progress;
	Dialog tDialog;
	Twitter twitter;
	double latitude;
	double longitude;

	String tweetText;
	String hashText;
	List<twitter4j.Status> tweets;
	CustomList adapter;
	String[] values;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle args) {
		View view = inflater.inflate(R.layout.tweet_image_fragment, container,
				false);
		pref = getActivity().getPreferences(0);
		l2 = (ListView) view.findViewById(R.id.list2);
		hashText = pref.getString("hashText", "");
		latitude = Double.parseDouble(pref.getString("latitude", "0.0"));
		longitude = Double.parseDouble(pref.getString("longitude", "0.0"));
		new searchTweet().execute();
		return view;

	}

	// to search tweets

	private class searchTweet extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(getActivity());
			progress.setMessage("Searching tweet ...");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setIndeterminate(true);
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
				GeoLocation location = new GeoLocation(latitude, longitude);

				Query query = new Query(hashText);
				values = new String[count];
				query.setCount(10);
				// temp value
				boolean flag = false;
				int tempUSerInput = 0;
				query.setGeoCode(location, radius, Query.KILOMETERS);

				QueryResult result;
				do {
					result = twitter.search(query);
					tweets = result.getTweets();
					for (twitter4j.Status tweet : tweets) {

						String tt = tweet.getText();
						if (tt.contains("https")) {
							String tt2 = tt.substring(tt.indexOf("https"),
									tt.length());
							Log.e("check",tt2);
							values[tempUSerInput] = tt2;
							tempUSerInput = tempUSerInput + 1;
						}

					}
					if (tempUSerInput >= count) // you have already matched the
												// number
					{
						flag = true;
					}

				} while ((query = result.nextQuery()) != null && !flag);

			} catch (TwitterException te) {
				te.printStackTrace();
				Log.e("check", "Failed to search tweets: " + te.getMessage());

			}

			return null;
		}

		protected void onPostExecute(String res) {

			progress.dismiss();
			adapter = new CustomList(getActivity(), values);
			l2.setAdapter(adapter);
		}
	}

}
