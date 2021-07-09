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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.instagramclone.R;
import com.codepath.instagramclone.models.Post;
import com.parse.GetDataCallback;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

public class PostDetailsFragment extends DialogFragment {

    ImageView ivImageDetails;
    private Post post;

    public PostDetailsFragment() { }

    public static PostDetailsFragment newInstance(Post post, Bitmap imageBitmap) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("post", post);
        args.putParcelable("imageBitmap", imageBitmap);
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

        Post post = this.getArguments().getParcelable("post");
        //Display image
        Bitmap imageBitmap = this.getArguments().getParcelable("imageBitmap");
        ivImageDetails.setImageBitmap(imageBitmap);
        Log.d("PostDetailsFragment", post.getDescription());

    }


}