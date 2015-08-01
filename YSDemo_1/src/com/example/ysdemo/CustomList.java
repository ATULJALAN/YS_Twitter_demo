/*
 * Created by Atul Jalan
 * Date:: 1/8/2015
 * Custom List to show tweet image using hashtag
 */
package com.example.ysdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;

public class CustomList extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private final String[] web;
	Bitmap bitmap;
	int position2;

	public CustomList(Context context, String[] web) {
		mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.web = web;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return web.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View grid;

		if (convertView == null) {

			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			grid = mInflater.inflate(R.layout.list_images, parent, false);

		} else {
			grid = (View) convertView;
		}
		WebView browser = (WebView) grid.findViewById(R.id.webView1);
		WebSettings webSettings = browser.getSettings();
		webSettings.setJavaScriptEnabled(true);
		browser.setWebViewClient(new WebViewClient());
		browser.loadUrl(web[position]);
		position2 = position;
		return grid;

	}

}
