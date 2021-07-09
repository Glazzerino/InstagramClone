package com.codepath.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.instagramclone.adadapters.PostAdapter;
import com.codepath.instagramclone.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomMenu;

    ImageButton btnLogout;
    ImageView ivTitle;
    ImageButton btnAdd;
    RecyclerView rvFeed;
    PostAdapter adapter;
    SwipeRefreshLayout swipeLayout;

    //Stores oldest post in memory
    Post oldestPost;

    private EndlessRecyclerViewScrollListener scrollListener;

    public static final int POST_ACTIVITY_CODE = 1337;
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        btnLogout = findViewById(R.id.btnLogout);
        ivTitle = findViewById(R.id.ivTitle);
        swipeLayout = findViewById(R.id.swipeLayout);
        
        bottomMenu = findViewById(R.id.bottomMenu);

        //Recyclerview setup
        rvFeed = findViewById(R.id.rvFeed);
        adapter = new PostAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFeed.setLayoutManager(linearLayoutManager);
        rvFeed.setAdapter(adapter);
        rvFeed.addItemDecoration(new DividerItemDecoration(rvFeed.getContext(), DividerItemDecoration.VERTICAL));

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                finish();
            }
        });

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_home:
                        break;
                    case R.id.item_create:
                        break;
                }
                return true;
            }

        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                loadPosts(true);
                scrollListener.resetState();
            }
        });
        // Set up endless pagination
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadPosts(false);
            }
        };
        rvFeed.addOnScrollListener(scrollListener);
        loadPosts(true);
    }

    private void goToAddPostActivity() {
        Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
        startActivityForResult(intent, POST_ACTIVITY_CODE);
    }

    private void loadPosts(boolean isInitialLoad) {
    ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
    query.include(Post.KEY_USER);
    query.setLimit(20);
    //Set sorting criteria
    query.addDescendingOrder("createdAt");

    if (!isInitialLoad) {
        if (oldestPost != null) {
            query.whereLessThan("createdAt", oldestPost.getCreatedAt());
        } else {
          Log.e(TAG, "Error: tried to paginate older posts than null");
        }
    } else {
        // Signal a process if it is the initial load since infinite pagination should
        // not require the user to wait
        swipeLayout.setRefreshing(true);
    }

    query.findInBackground(new FindCallback<Post>() {
        @Override
        public void done(List<Post> objects, ParseException e) {
            if (e != null) {
                Log.e(TAG, "Error retrieving posts from database: " + e.toString());
                return;
            } else {
                //Success
                adapter.getPosts().addAll(objects);
                if (isInitialLoad) {
                    adapter.notifyDataSetChanged();
                } else {
                    //If we are loading for infinite pagination purposes
                    int size = adapter.getPosts().size();
                    adapter.notifyItemRangeChanged(size - objects.size() - 1, objects.size());
                }
                swipeLayout.setRefreshing(false);
                // Record oldest post
                oldestPost = adapter.getPosts().get(adapter.getPosts().size() - 1);
            }
        }
    });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == POST_ACTIVITY_CODE) {
            Log.d(TAG, "AddPostActivity success!");
            Post newPost = data.getExtras().getParcelable("post");
            adapter.getPosts().add(0, newPost);
            adapter.notifyItemInserted(0);
            rvFeed.scrollToPosition(0);
        }
    }
}