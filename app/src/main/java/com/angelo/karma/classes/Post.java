package com.angelo.karma.classes;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.angelo.karma.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.palette.graphics.Palette;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Post extends BaseObservable  {

    @PrimaryKey
    private long postId;
    private String postAuthorName;
    private String postAuthorImage;
    private long postTime;
    private int postNum;
    private int postCommentsNum;

    public int getPostCommentsNum() {
        return postCommentsNum;
    }

    public void setPostCommentsNum(int postCommentsNum) {
        this.postCommentsNum = postCommentsNum;
    }

    private String postImage;
    private String postDesc;
    private int likes;
    private int dislikes;
    private boolean liked;
    private boolean disliked;
    private boolean hasImage;

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    private List<String> likes_list;
    private List<String> dislikes_list;

    @BindingAdapter("postAuthorImage")
    public static void loadPostAuthorImage(ImageView view, String imageUrl){
        Glide
                .with(view.getContext())
                .load(imageUrl)
                .override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

    @BindingAdapter("postImage")
    public static void loadPostImage(ImageView view, String imageUrl){



        Glide
                .with(view.getContext())
                .asBitmap()
                .load(imageUrl)
                .override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null){
                            Palette p = Palette.from(resource).generate();
                            int mutedColor = p.getMutedColor(view.getContext().getColor(R.color.primary_dark));

                        }
                        return false;
                    }
                })
                .into(view);
    }

    public List<String> getLikes_list() {
        return likes_list;
    }

    public void setLikes_list(List<String> likes_list) {
        this.likes_list = likes_list;
    }

    public List<String> getDislikes_list() {
        return dislikes_list;
    }

    public void setDislikes_list(List<String> dislikes_list) {
        this.dislikes_list = dislikes_list;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isDisliked() {
        return disliked;
    }

    public void setDisliked(boolean disliked) {
        this.disliked = disliked;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getPostAuthorName() {
        return postAuthorName;
    }

    public void setPostAuthorName(String postAuthorName) {
        this.postAuthorName = postAuthorName;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    @Bindable
    public String getPostImage() {
        return postImage;
    }

    @Bindable
    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }
    @Bindable
    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }
    @Bindable
    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getPostNum() {
        return postNum;
    }

    public void setPostNum(int postNum) {
        this.postNum = postNum;
    }

    public String getPostAuthorImage() {
        return postAuthorImage;
    }

    public void setPostAuthorImage(String postAuthorImage) {
        this.postAuthorImage = postAuthorImage;
    }
}
