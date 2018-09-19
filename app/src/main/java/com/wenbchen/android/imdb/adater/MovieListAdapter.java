package com.wenbchen.android.imdb.adater;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.wenbchen.android.imdb.R;
import com.wenbchen.android.imdb.activities.MovieDetailActivity;
import com.wenbchen.android.imdb.activities.MovieListViewActivity;
import com.wenbchen.android.imdb.activities.TVDetailActivity;
import com.wenbchen.android.imdb.database.WatchedMoviesDataSource;
import com.wenbchen.android.imdb.model.Media;
import com.wenbchen.android.imdb.util.UtilsString;
import com.wenbchen.android.imdb.volleysingleton.VolleySingleton;

import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
	public static final String TAG = "CustomListAdapter";
	
	private AppCompatActivity activity;
	private LayoutInflater inflater;
	private List<Media> movieItems;
	ImageLoader imageLoader;
	private WatchedMoviesDataSource dataSource;
	
	public MovieListAdapter(AppCompatActivity activity, List<Media> movieItems, WatchedMoviesDataSource dataSource) {
		this.activity = activity;
		this.movieItems = movieItems;
		this.dataSource = dataSource;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = VolleySingleton.getInstance(activity.getApplicationContext()).getImageLoader();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		NetworkImageView thumbNail;
		TextView title;
		TextView type;
		TextView year;
		TextView watched;
		Button details;
		public ViewHolder(View view) {
			super(view);
			this.type = (TextView) view.findViewById(R.id.type);
			this.thumbNail = (NetworkImageView) view.findViewById(R.id.thumbnail);
			this.title = (TextView) view.findViewById(R.id.title);
			this.year = (TextView) view.findViewById(R.id.releaseYear);
			this.watched = (TextView) view.findViewById(R.id.viewed);
			this.details = (Button) view.findViewById(R.id.detailButton);
		}
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
		ViewHolder vh = new ViewHolder(view);
		return vh;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		Media m = movieItems.get(position);
		final String uuid = m.getUuid();

		// thumbnail image
		Log.i(TAG, "POS " + position + " thumb url " + m.getThumbnailUrl());
		if (m.getThumbnailUrl().length() > 0 &&!m.getThumbnailUrl().equalsIgnoreCase(UtilsString.NA_STRING)) {
			holder.thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
		} else {
			holder.thumbNail.setImageUrl(null, imageLoader);
			holder.thumbNail.setDefaultImageResId(R.drawable.default_thumb);
		}
		// title
		holder.title.setText(m.getTitle());
		// rating
		holder.type.setText(activity.getResources().getString(R.string.type_detail) + m.getType());
		// release year
		holder.year.setText(m.getYear());
		// watched history
		if (dataSource.exists(uuid)) {
			holder.watched.setText(activity.getResources().getString(R.string.viewed));
		} else {
			holder.watched.setText("");
		}
		//details button
		holder.details.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
						dataSource.insertMovie(uuid, 1);
						Intent intent;
						if (activity instanceof MovieListViewActivity) {
							intent = new Intent(activity, MovieDetailActivity.class);
						} else {
							intent = new Intent(activity, TVDetailActivity.class);
						}
						intent.putExtra(UtilsString.UUID_KEY, uuid);
						activity.startActivity(intent);
						holder.watched.setText(activity.getResources().getString(R.string.viewed));
					}
		});
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return movieItems.size();
	}

}