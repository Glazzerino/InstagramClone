package com.codepath.instagramclone.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.instagramclone.EndlessRecyclerViewScrollListener;
import com.codepath.instagramclone.R;
import com.codepath.instagramclone.adadapters.PostAdapter;
import com.codepath.instagramclone.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    public static final int POST_ACTIVITY_CODE = 1337;
    Context context;
    RecyclerView rvFeed;
    PostAdapter adapter;
    SwipeRefreshLayout swipeLayout;

    //Stores oldest post in memory
    Post oldestPost;
    private EndlessRecyclerViewScrollListener scrollListener;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(Context context) {
        HomeFragment fragment = new HomeFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeLayout = view.findViewById(R.id.swipeLayout);
        //Recyclerview setup
        rvFeed = view.findViewById(R.id.rvFeed);
        adapter = new PostAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvFeed.setLayoutManager(linearLayoutManager);
        rvFeed.setAdapter(adapter);
        rvFeed.addItemDecoration(new DividerItemDecoration(rvFeed.getContext(), DividerItemDecoration.VERTICAL));
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

    private void loadPosts(boolean isInitialLoad) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(7);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
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