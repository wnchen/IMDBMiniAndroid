package com.wenbchen.android.imdb.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wenbchen.android.imdb.R;
import com.wenbchen.android.imdb.adater.MovieListAdapter;
import com.wenbchen.android.imdb.database.WatchedMoviesDataSource;
import com.wenbchen.android.imdb.model.Media;
import com.wenbchen.android.imdb.util.UtilsString;
import com.wenbchen.android.imdb.volleysingleton.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BaseListViewActivity extends AppCompatActivity {
    // Log tag
    private static final String TAG = "BaseListViewActivity";

    private ProgressDialog pDialog;
    private List<Media> movieList = new ArrayList<Media>();
    private RecyclerView.Adapter adapter;
    private TextView mNoMoviesTextView;
    private WatchedMoviesDataSource dataSource;
    protected String title;
    protected String year;

    public List<Media> getMovieList() {
        return movieList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        //set a Toolbar to replace the ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpPageTitle();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        title = extras.getString(UtilsString.TITLE_KEY);
        year = extras.getString(UtilsString.YEAR_KEY);

        dataSource = new WatchedMoviesDataSource(this);
        dataSource.open();

        mNoMoviesTextView = (TextView) findViewById(R.id.no_movies);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieListAdapter(this, movieList, dataSource);
        recyclerView.setAdapter(adapter);

        StringBuffer mUrlStringBuffer = new StringBuffer();
        mUrlStringBuffer = buildSearchRequest(title, year);


        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage(getResources().getString(R.string.load_dialog_msg));
        pDialog.show();

        // Creating volley request obj
        JsonObjectRequest movieReq = new JsonObjectRequest(mUrlStringBuffer.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();
                        parse(response);
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mNoMoviesTextView.setVisibility(View.VISIBLE);
                hidePDialog();
            }
        });

        // Adding request to request queue
        VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(movieReq, UtilsString.MOVIE_LIST_TAG);
    }


    @Override
    protected void onStop() {
        super.onStop();
        hidePDialog();
        VolleySingleton.getInstance(this.getApplicationContext()).cancelPendingRequests(UtilsString.MOVIE_LIST_TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void setUpPageTitle() {
        return;
    }

    protected StringBuffer buildSearchRequest(String title, String year) {
        return new StringBuffer();
    }

    private void parse(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(UtilsString.SEARCH_KEY);
            for (int i = 0; i < jsonArray.length(); i++) {
                String movieString = jsonArray.getJSONObject(i).toString();
                Media movie = Media.movieFromJson(movieString);
                getMovieList().add(movie);
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            mNoMoviesTextView.setVisibility(View.VISIBLE);
        }
    }
}
