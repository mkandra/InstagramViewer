package com.mbkandra16.apps.instagramviewer;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class PhotosActivity extends Activity {

	public static final String CLIENT_ID = "d3783f2ec25f4cf0bdc22dd012f37c6c";
	private ArrayList<InstagramPhoto> photos;
	private InstagramPhotosAdapter aPhotos;
	private SwipeRefreshLayout swipeContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photos);
		getActionBar().setTitle(R.string.popular_photos_title);
		fetchPopularPhotos();

		swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
		// Setup refresh listener which triggers new data loading
		swipeContainer.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Your code to refresh the list here.
				// Make sure you call swipeContainer.setRefreshing(false)
				// once the network request has completed successfully.
				aSyncFetchPhotos();
				swipeContainer.setRefreshing(false);
			}
		});
	}

	private void aSyncFetchPhotos() {
		// https://api.instagram.com/v1/media/popular?client_id=d3783f2ec25f4cf0bdc22dd012f37c6c
		// { "data" => [x] => "images" => "standard_resolution" => "url" }
		// Setup pupular url endpoint
		String popularUrl = "https://api.instagram.com/v1/media/popular?client_id="
				+ CLIENT_ID;

		// Create the network client
		AsyncHttpClient client = new AsyncHttpClient();

		// Trigger the network request
		client.get(popularUrl, new JsonHttpResponseHandler() {
			// define success and failure callbacks
			// Handle the successful response (popular photos JSON)
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// fired once the successful response back
				// response is == popular photos json
				// url, height, username, caption
				// { "data" => [x] => "user" => "username" }
				// { "data" => [x] => "caption" => "text" }
				// { "data" => [x] => "images" => "standard_resolution" => "url"
				// }
				// { "data" => [x] => "images" => "standard_resolution" =>
				// "height" }
				JSONArray photosJSON = null;
				try {
					photos.clear();
					photosJSON = response.getJSONArray("data");
					for (int i = 0; i < photosJSON.length(); i++) {
						JSONObject photoJSON = photosJSON.getJSONObject(i);
						InstagramPhoto photo = new InstagramPhoto();
						photo.username = photoJSON.getJSONObject("user")
								.getString("username");
						photo.profileUrl = photoJSON.getJSONObject("user")
								.getString("profile_picture");
						if (photoJSON.optJSONObject("caption") != null)
							photo.caption = photoJSON.getJSONObject("caption")
									.getString("text");
						photo.imageUrl = photoJSON.getJSONObject("images")
								.getJSONObject("standard_resolution")
								.getString("url");
						photo.imageHeight = photoJSON.getJSONObject("images")
								.getJSONObject("standard_resolution")
								.getInt("height");
						photo.likesCount = photoJSON.getJSONObject("likes")
								.getInt("count");
						photos.add(photo);
					}
					// Notify the adapter that it should populate new changes
					// into the listView
					aPhotos.notifyDataSetChanged();
				} catch (JSONException e) {
					// Fire if things fail, json parsing is invalid
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});

	}

	private void fetchPopularPhotos() {
		// TODO Auto-generated method stub
		photos = new ArrayList<InstagramPhoto>(); // initialize the arraylist
		// Create adapter and bind it to the data in arrayList
		aPhotos = new InstagramPhotosAdapter(this, photos);
		// populate the data into the listView
		ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
		// Set the adapter to the listview (population of items)
		lvPhotos.setAdapter(aPhotos);

		aSyncFetchPhotos();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
