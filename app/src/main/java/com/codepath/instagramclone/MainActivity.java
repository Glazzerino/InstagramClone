package com.codepath.instagramclone;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.codepath.instagramclone.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton btnLogout;
    ImageView ivTitle;
    ImageButton btnAdd;
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnLogout = findViewById(R.id.btnLogout);
        ivTitle = findViewById(R.id.ivTitle);
        btnAdd = findViewById(R.id.btnAdd);

        //set custom toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to post composing activity
                goToAddPostActivity();
            }
        });

        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error getting posts: " + e.toString());
                } else {
                    // Success
                    int i=0;
                    //Leaving as range-based loop to take advantage of iterator performance
                    for (Post post : objects) {
                        Log.d(TAG, String.format("Post %s description: %s", String.valueOf(i++), post.getDescription()));
                    }
                }
            }
        });
    }

    private void goToAddPostActivity() {
        Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
        startActivity(intent);
    }
}