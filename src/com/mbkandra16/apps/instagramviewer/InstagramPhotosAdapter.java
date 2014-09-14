package com.mbkandra16.apps.instagramviewer;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {
	public InstagramPhotosAdapter(Context context, List<InstagramPhoto> photos) {
		super(context, android.R.layout.simple_list_item_1, photos);
	}

	private static class ViewHolder {
		TextView tvCaption;
		ImageView imgPhoto;
		ImageView imgProfile;
		TextView tvLikesCount;
	}

	// Takes a data item at a position, converts it to a row in the listView
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Take the data source at position
		// Get the data item
		InstagramPhoto photo = getItem(position);
		// Check if we are using a recycled view
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_photo, parent, false);
			viewHolder.tvCaption = (TextView) convertView
					.findViewById(R.id.tvCaption);
			viewHolder.imgPhoto = (ImageView) convertView
					.findViewById(R.id.imgPhoto);
			viewHolder.imgProfile = (ImageView) convertView
					.findViewById(R.id.imgProfile);
			viewHolder.tvLikesCount = (TextView) convertView
					.findViewById(R.id.tvLikesCount);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// set the likes count
		viewHolder.tvLikesCount.setText(photo.likesCount + " likes");
		// populate the subviews (textfiled, imageview) with the correct data
		viewHolder.tvCaption.setText(Html.fromHtml("<font color=\"blue\">"
				+ "<b>" + photo.username + "</b>" + "</font>" + " -- "
				+ photo.caption));
		viewHolder.imgProfile.getLayoutParams().height = 32;
		// Set the image height before loading
		viewHolder.imgPhoto.getLayoutParams().height = photo.imageHeight;
		// Reset the image from the recycled view
		viewHolder.imgPhoto.setImageResource(0);
		// Asl for the photo to be added to the imageView based on the photo url
		// Background: Send a network request to the url, download the image
		// bytes, convert into bitmap, resizing the image, insert bitmap into
		// the imageview

		Transformation transformation_photo = new Transformation() {

			@Override
			public String key() {
				// TODO Auto-generated method stub
				return "photo";
			}

			@Override
			public Bitmap transform(Bitmap arg0) {
				DisplayMetrics displayMetrics = getContext().getResources()
						.getDisplayMetrics();
				int deviceWidth = displayMetrics.widthPixels;
				double aspectRatio = (double) arg0.getHeight()
						/ (double) arg0.getWidth();
				int targetHeight = (int) (deviceWidth * aspectRatio);
				viewHolder.imgPhoto.getLayoutParams().height = targetHeight;
				Bitmap scaled = Bitmap.createScaledBitmap(arg0, deviceWidth,
						targetHeight, false);
				if (scaled != arg0) {
					arg0.recycle();
				}

				return scaled;
			}
		};

		Picasso.with(getContext()).load(photo.imageUrl)
				.transform(transformation_photo).into(viewHolder.imgPhoto);

		Transformation transformation_profile = new Transformation() {

			@Override
			public String key() {
				// TODO Auto-generated method stub
				return "profile";
			}

			@Override
			public Bitmap transform(Bitmap arg0) {
				int targetWidth = 32; // for profile image
				double aspectRatio = (double) arg0.getHeight()
						/ (double) arg0.getWidth();
				int targetHeight = (int) (targetWidth * aspectRatio);
				viewHolder.imgProfile.getLayoutParams().height = targetHeight;
				Bitmap scaled = Bitmap.createScaledBitmap(arg0, targetWidth,
						targetHeight, false);
				if (scaled != arg0) {
					arg0.recycle();
				}
				Bitmap bitmap = Bitmap.createBitmap(32, 32, arg0.getConfig());
				Canvas canvas = new Canvas(bitmap);
				Paint paint = new Paint();
				BitmapShader shader = new BitmapShader(scaled,
						BitmapShader.TileMode.CLAMP,
						BitmapShader.TileMode.CLAMP);
				paint.setShader(shader);
				paint.setAntiAlias(true);
				canvas.drawCircle(16, 16, 16, paint);
				scaled.recycle();
				return bitmap;
			}
		};

		Picasso.with(getContext()).load(photo.profileUrl)
				.transform(transformation_profile).into(viewHolder.imgProfile);

		// Picasso.with(getContext()).load(photo.profileUrl).into(imgProfile);
		// Picasso.with(getContext()).load(photo.imageUrl).into(viewHolder.imgPhoto);
		// Return the view for that data item
		return convertView;
	}

	// getView method (int position)
	// Default, takes the model (InstagramPhoto) toString()

}
