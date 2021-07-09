package com.codepath.instagramclone.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.codepath.instagramclone.R;
import com.codepath.instagramclone.models.Post;
import com.parse.GetDataCallback;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class PostDetailsFragment extends DialogFragment {

    ImageView ivImageDetails;
    TextView tvUsernameDetails;
    TextView tvDescriptionDetails;
    TextView tvTimeDeltaDetails;
    private Post post;

    public PostDetailsFragment() { }

    public static PostDetailsFragment newInstance(Post post) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("post", post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ivImageDetails = view.findViewById(R.id.ivImageDetails);
        tvUsernameDetails = view.findViewById(R.id.tvUsernameDetails);
        tvTimeDeltaDetails = view.findViewById(R.id.tvTimeDeltaDetails);
        tvDescriptionDetails = view.findViewById(R.id.tvDescriptionDetails);
        //get post
        Post post = this.getArguments().getParcelable("post");

        tvUsernameDetails.setText(post.getUser().getUsername());
        tvTimeDeltaDetails.setText(calculateTimeAgo(post.getCreatedAt()));
        tvDescriptionDetails.setText(post.getDescription());
        //Display image
        Glide.with(getContext())
                .load(post.getImage().getUrl())
                .into(ivImageDetails);
        Log.d("PostDetailsFragment", post.getDescription());

    }

    //utility function to get time delta from creation date of post
    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }

}