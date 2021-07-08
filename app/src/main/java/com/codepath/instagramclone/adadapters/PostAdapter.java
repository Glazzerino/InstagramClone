package com.codepath.instagramclone.adadapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.instagramclone.R;
import com.codepath.instagramclone.models.Post;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {

    List<Post> posts;
    Context context;

    public PostAdapter(Context context,List<Post> posts) {
        this.context = context;
        this.posts = posts;
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
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
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
