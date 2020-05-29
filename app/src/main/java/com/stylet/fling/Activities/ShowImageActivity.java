package com.stylet.fling.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.stylet.fling.R;

public class ShowImageActivity extends AppCompatActivity {

    String blog_image;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        ImageView show_image = findViewById(R.id.showImage);
        ImageView backPress = findViewById(R.id.backpress);
        progressBar = findViewById(R.id.image_progressbar);


        progressBar.setVisibility(View.VISIBLE);
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Animatoo.animateSwipeLeft(ShowImageActivity.this);
            }
        });

            blog_image = getIntent().getStringExtra("blog_image");
            Glide.with(this)
                    .load(blog_image)
                    .into(show_image);

            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSwipeLeft(ShowImageActivity.this);
    }
}
