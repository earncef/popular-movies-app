package com.earncef.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {

    protected JSONObject movie = null;
    protected String imageUrl = "http://image.tmdb.org/t/p/w185/";
    protected String title = "";
    protected String image = "";
    protected String synopsis = "";
    protected String releaseDate = "";
    protected double rating = 0;
    DecimalFormat df = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        try {
            movie = new JSONObject(intent.getStringExtra("movie"));
            title = movie.getString("title");
            image = movie.getString("poster_path");
            synopsis = movie.getString("overview");
            releaseDate = movie.getString("release_date");
            rating = movie.getDouble("vote_average");
        } catch (Exception e) {
            finish();
        }

        TextView txtTitle = (TextView) findViewById(R.id.title);
        txtTitle.setText(title);

        TextView txtSynopsis = (TextView) findViewById(R.id.synopsis);
        txtSynopsis.setText(synopsis);

        TextView txtReleaseDate = (TextView) findViewById(R.id.releaseDate);
        txtReleaseDate.setText(releaseDate);

        ImageView imgImageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(DetailActivity.this).load(imageUrl + image).into(imgImageView);

        TextView txtRating = (TextView) findViewById(R.id.rating);
        txtRating.setText(df.format(rating) + " / 10");
    }

}
