package com.earncef.popularmoviesapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.View;
import android.widget.GridView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.AdapterView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String url = "http://api.themoviedb.org/3/discover/movie?api_key=APIKEYHERE&sort_by=";
    private String thumbnailUrl = "http://image.tmdb.org/t/p/w185/";
    private String sortBy = "popularity.desc";
    private JSONObject movieData = null;
    public static String[] thumbnails = {};
    protected ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreInstanceState(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageAdapter = new ImageAdapter(this);
        GridView gridView = (GridView) findViewById(R.id.gridThumbnail);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                openMovie(position);
            }
        });


        if (null == movieData) {
            new LoadMoviesTask().execute(url + sortBy);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("sortBy", sortBy);
        outState.putString("movieData", movieData.toString());
        outState.putStringArray("thumbnails", thumbnails);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_high_rating) {
            sortBy = "vote_average.desc";
            new LoadMoviesTask().execute(url + sortBy);
            return true;
        }

        if (id == R.id.action_sort_popular) {
            sortBy = "popularity.desc";
            new LoadMoviesTask().execute(url + sortBy);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void restoreInstanceState(Bundle savedInstanceState) {
        try {
            sortBy = savedInstanceState.getString("sortBy");
            thumbnails = savedInstanceState.getStringArray("thumbnails");
            movieData = new JSONObject(savedInstanceState.getString("movieData"));
        } catch (Exception e) {
        }
    }

    protected void openMovie(int position) {
        JSONArray movies = new JSONArray();
        try {
            movies = movieData.getJSONArray("results");
        } catch (Exception e) {
        }

        try {
            JSONObject movie = movies.getJSONObject(position);
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("movie", movie.toString());
            startActivity(intent);
        } catch (Exception e) {
        }
    }

    protected void update() {
        List<String> tThumbnails = new ArrayList<>();
        JSONArray movies = new JSONArray();
        try {
            movies = movieData.getJSONArray("results");
        } catch (Exception e) {
        }

        for (int i = 0, size = movies.length(); i < size; i++) {
            try {
                JSONObject movie = movies.getJSONObject(i);
                tThumbnails.add(thumbnailUrl + movie.getString("poster_path"));
            } catch (Exception e) {
                Log.d(TAG, "There was an error in accessing movie data.");
            }
        }
        thumbnails = tThumbnails.toArray(new String[tThumbnails.size()]);

        imageAdapter.notifyDataSetChanged();
    }

    private class LoadMoviesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (Exception e) {
                return "Unable to retrieve response.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                movieData = new JSONObject(result);
                update();
            } catch (Exception e) {
                Log.d(TAG, "There was an error in parsing JSON response.");
            }
        }

        protected String downloadUrl(String url) throws Exception {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();
            Log.d(TAG, "Sending 'GET' request to URL : " + url);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                sb.append(responseLine).append("\n");
            }
            in.close();
            con.disconnect();

            return sb.toString();
        }
    }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return thumbnails.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (null == convertView) {
                imageView = new ImageView(mContext);
                imageView.setMinimumWidth(60);
                imageView.setMinimumHeight(80);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(mContext).load(thumbnails[position]).into(imageView);

            return imageView;
        }
    }
}
