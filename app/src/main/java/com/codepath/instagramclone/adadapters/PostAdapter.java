package com.codepath.instagramclone.adadapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.codepath.instagramclone.R;
import com.codepath.instagramclone.fragments.PostDetailsFragment;
import com.codepath.instagramclone.models.Post;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {



    List<Post> posts;
    Context context;

    public PostAdapter(Context context) {
        this.context = context;
        this.posts = new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new Viewholder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.Viewholder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This prevents a crash since the image could still be getting loaded by Glide
                if (holder.ivImage.getDrawable() != null) {
                    AppCompatActivity parent = (AppCompatActivity) context;
                    FragmentManager fm = parent.getSupportFragmentManager();
                    //Get image bitmap directly from the already-loaded image view since doing otherwise
                    //would force the fragment code to fetch the image if it was not taken on the device
                    Drawable drawable = holder.ivImage.getDrawable();
                    Bitmap imageBitmap = ((BitmapDrawable) drawable).getBitmap();
                    PostDetailsFragment fragment = PostDetailsFragment.newInstance(post, imageBitmap);
                    fragment.show(fm, "post_details");
                } else {
                    Toast.makeText(context, "Please wait until image loads", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public List<Post> getPosts() {
        return this.posts;
    }


    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public class Viewholder extends ViewHolder {
        TextView tvUsername;
        TextView tvDescription;
        ImageView ivImage;
        ImageButton btnHeart;

        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            btnHeart = itemView.findViewById(R.id.btnHeart);

        }

        public void bind(Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            //Get image
            ParseFile imageFile = post.getImage();
            if (imageFile != null) {
                Glide.with(context)
                        .load(imageFile.getUrl())
                        .into(ivImage);
            }
            Log.d("Viewholder", "Bind!");

        }
    }
}
