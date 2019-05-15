package com.angelo.karma.adapter;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.angelo.karma.R;
import com.angelo.karma.activity.CommentsActivity;
import com.angelo.karma.activity.ProfileActivity;
import com.angelo.karma.classes.Post;
import com.angelo.karma.databinding.PostListBinding;
import com.angelo.karma.interfaces.OnFetchCommentsCountListener;
import com.angelo.karma.query.QueryUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>{

    private List<Post> postList;
    private FirebaseAuth mAuth;
    private LayoutInflater layoutInflater;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final PostListBinding binding;




        public MyViewHolder(final PostListBinding postListBinding){
            super(postListBinding.getRoot());

            this.binding = postListBinding;

        }


    }

    public PostAdapter(List<Post> postList){
        this.postList = postList;
        setHasStableIds(true);
        notifyDataSetChanged();

    }

    public void clear(){
        this.postList.clear();
        notifyDataSetChanged();
    }

    public void setData(List<Post> newData) {
        this.postList = newData;
        notifyDataSetChanged();
    }

    public void updateData(List<Post> newData){

        final PostDiffCallback diffCallback = new PostDiffCallback(this.postList, newData);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);


        this.postList.clear();
        this.postList.addAll(newData);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setPost(postList.get(position));
        setFadeAnimation(holder.itemView);
        this.mAuth = FirebaseAuth.getInstance();
        final Post post = postList.get(position);

        Post.loadPostAuthorImage(holder.binding.authorPic, post.getPostAuthorImage());
        holder.binding.authorPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
                intent.putExtra("username", post.getPostAuthorName());
                View sharedView = holder.binding.authorPicCircle;
                String transName = "splash_anim";
                Activity activity = (Activity) holder.itemView.getContext();
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, sharedView, transName);
                holder.itemView.getContext().startActivity(intent, transitionActivityOptions.toBundle());
            }
        });

        holder.binding.postAuthorUsername.setText(post.getPostAuthorName());
        holder.binding.postTime.setText(QueryUtils.getTimeAgo(post.getPostTime()));

        if (post.isHasImage()){

          Post.loadPostImage(holder.binding.postImg, post.getPostImage());

            holder.binding.postImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] resources = new String[]{post.getPostImage()};

                    new StfalconImageViewer.Builder<>(holder.itemView.getContext(), resources, new ImageLoader<String>() {
                        @Override
                        public void loadImage(ImageView imageView, String image) {
                            Glide
                                    .with(holder.itemView.getContext())
                                    .load(post.getPostImage())
                                    .into(imageView);
                        }
                    }).withTransitionFrom(holder.binding.postImg).show();
                }
            });
            holder.binding.postMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(), holder.binding.postMenuButton);
                    popupMenu.getMenuInflater().inflate(R.menu.post_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.post_download:
                                    Glide.with(holder.itemView.getContext())
                                            .asBitmap()
                                            .load(post.getPostImage())
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    saveImage(resource, String.valueOf(post.getPostId()), holder.itemView.getContext());

                                                }
                                            });
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
        }else {
        }


        holder.binding.descText.setText(post.getPostDesc());

        holder.binding.likeButton.setText(String.valueOf(post.getLikes()));
        holder.binding.dislikeButton.setText(String.valueOf(post.getDislikes()));
        QueryUtils.fetchCommentsCount(post.getPostAuthorName(), post.getPostNum(), new OnFetchCommentsCountListener() {
            @Override
            public void onSuccess(int count) {
                holder.binding.commentButton.setText(String.valueOf(count));
            }
        });


        String username = mAuth.getCurrentUser().getDisplayName();




        if (post.getLikes_list().contains(username) && !post.getDislikes_list().contains(username)){
            holder.binding.likeButton.setIconTintResource(R.color.colorAccentDark);
            holder.binding.likeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentDark));
            holder.binding.dislikeButton.setIconTintResource(R.color.colorAccentBlueDark_50);
            holder.binding.dislikeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));

            holder.binding.dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.binding.dislikeButton.setIconTintResource(R.color.colorAccentDark);
                    holder.binding.dislikeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentDark));
                    holder.binding.likeButton.setIconTintResource(R.color.colorAccentBlueDark_50);
                    holder.binding.likeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));
                    holder.binding.likeButton.setText(String.valueOf(post.getLikes() - 1));
                    holder.binding.dislikeButton.setText(String.valueOf(post.getDislikes() + 1));
                    QueryUtils.submitReact(false, post.getPostAuthorName(), post.getPostNum(), mAuth.getCurrentUser().getDisplayName());


                }
            });

            holder.binding.likeButton.setOnClickListener(null);
        }else if (!post.getLikes_list().contains(username) && post.getDislikes_list().contains(username)){
            holder.binding.dislikeButton.setIconTintResource(R.color.colorAccentDark);
            holder.binding.dislikeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentDark));
            holder.binding.likeButton.setIconTintResource(R.color.colorAccentBlueDark_50);
            holder.binding.likeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));

            holder.binding.likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.binding.likeButton.setIconTintResource(R.color.colorAccentDark);
                    holder.binding.likeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentDark));
                    holder.binding.dislikeButton.setIconTintResource(R.color.colorAccentBlueDark_50);
                    holder.binding.dislikeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));
                    holder.binding.likeButton.setText(String.valueOf(post.getLikes() + 1));
                    holder.binding.dislikeButton.setText(String.valueOf(post.getDislikes() - 1));
                    QueryUtils.submitReact(true, post.getPostAuthorName(), post.getPostNum(), mAuth.getCurrentUser().getDisplayName());

                }
            });

            holder.binding.dislikeButton.setOnClickListener(null);
        } else if (!post.getLikes_list().contains(username) && !post.getDislikes_list().contains(username)){
            holder.binding.likeButton.setIconTintResource(R.color.colorAccentBlueDark_50);
            holder.binding.likeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));
            holder.binding.dislikeButton.setIconTintResource(R.color.colorAccentBlueDark_50);
            holder.binding.dislikeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));

            holder.binding.likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.binding.likeButton.setIconTintResource(R.color.colorAccentDark);
                    holder.binding.likeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentDark));
                    holder.binding.dislikeButton.setIconTintResource(R.color.colorAccentBlueDark_50);
                    holder.binding.dislikeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));
                    holder.binding.likeButton.setText(String.valueOf(post.getLikes() + 1));
                    QueryUtils.submitReact(true, post.getPostAuthorName(), post.getPostNum(), mAuth.getCurrentUser().getDisplayName());

                }
            });

            holder.binding.dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.binding.dislikeButton.setIconTintResource(R.color.colorAccentDark);
                    holder.binding.dislikeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentDark));
                    holder.binding.likeButton.setIconTintResource(R.color.colorAccentBlueDark_50);
                    holder.binding.likeButton.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));
                    holder.binding.dislikeButton.setText(String.valueOf(post.getDislikes() + 1));
                    QueryUtils.submitReact(false, post.getPostAuthorName(), post.getPostNum(), mAuth.getCurrentUser().getDisplayName());

                }
            });
        }

        holder.binding.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), CommentsActivity.class);
                intent.putExtra("postAuthor", post.getPostAuthorName());
                intent.putExtra("postNum", post.getPostNum());
                intent.putExtra("postAuthorPic", post.getPostAuthorImage());
                holder.itemView.getContext().startActivity(intent);
            }
        });



        holder.binding.setPost(post);


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        PostListBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.post_list, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public long getItemId(int position) {
        Post post = postList.get(position);
        return post.getPostId();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.clearAnimation();
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

    private String saveImage(Bitmap image, String imgName, Context context) {
        String savedImagePath = null;

        String imageFileName = "KARMA_IMG_" + imgName + ".jpg";
        File storageDir = new File(            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + "/Karma");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            galleryAddPic(savedImagePath, context);
            Toast.makeText(context, "Image downloaded", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


}
