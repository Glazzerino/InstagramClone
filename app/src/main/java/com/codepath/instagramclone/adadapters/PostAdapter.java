package com.codepath.instagramclone.adadapters;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.codepath.instagramclone.R;
import com.codepath.instagramclone.fragments.PostDetailsFragment;
import com.codepath.instagramclone.models.Post;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> posts;
    Context context;
    public final String TAG = "PostAdapter";
    public PostAdapter(Context context) {
        this.context = context;
        this.posts = new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This prevents a crash since the image could still be getting loaded by Glide
                if (holder.ivImage.getDrawable() != null) {
                    AppCompatActivity parent = (AppCompatActivity) context;
                    FragmentManager fm = parent.getSupportFragmentManager();

                    PostDetailsFragment fragment = PostDetailsFragment.newInstance(post);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvDescription;
        ImageView ivImage;
        ImageButton btnHeart;
        TextView tvLikes;
        public int likes;
        boolean liked;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            btnHeart = itemView.findViewById(R.id.btnHeart);
            tvLikes = itemView.findViewById(R.id.tvLikes);
        }

        public void bind(Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            likes = post.getLikes();
            tvLikes.setText(String.valueOf(likes));
            //If post was liked during session then change drawable to active
            //TODO: Fix cloud function to also record if user has liked a particular post
            if (post.isLiked()) {
                btnHeart.setImageResource(R.drawable.ufi_heart_active);
                btnHeart.setClickable(false);
            } else {
                btnHeart.setClickable(true);
                btnHeart.setImageResource(R.drawable.ufi_heart);
            }
            //Get image
            ParseFile imageFile = post.getImage();
            if (imageFile != null) {
                Glide.with(context)
                        .load(imageFile.getUrl())
                        .into(ivImage);
            }
            Log.d("ViewHolder", "Bind!");
            btnHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    post.setLiked(false);
                    btnHeart.setClickable(false);
                    btnHeart.setImageResource(R.drawable.ufi_heart_active);
                    likes++;
                    tvLikes.setText(String.valueOf(likes));
                    HashMap<String, Object> parameters = new HashMap<>();
                    parameters.put("postId", post.getObjectId());
                    parameters.put("user", ParseUser.getCurrentUser().getObjectId());
                    ParseCloud.callFunctionInBackground("userLikesPost", parameters, new FunctionCallback<Float>() {
                        @Override
                        public void done(Float object, ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Post liked!");
                                post.setLikes(likes);
                            } else {
                                Log.e(TAG, "Error liking post: " + e.toString());
                            }
                        }
                    });
                }
            });
        }
    }
}
