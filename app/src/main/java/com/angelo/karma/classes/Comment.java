package com.angelo.karma.classes;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import androidx.databinding.BindingAdapter;

public class Comment {

    private long id;
    private int postNum;
    private long postTime;
    private String commentAuthor;
    private String commentAuthorPic;


    @BindingAdapter("commentAuthorPic")
    public static void loadPostAuthorImage(ImageView view, String imageUrl){
        Glide
                .with(view.getContext())
                .load(imageUrl)
                .circleCrop()
                .fitCenter()
                .override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

    public String getCommentAuthorPic() {
        return commentAuthorPic;
    }

    public void setCommentAuthorPic(String commentAuthorPic) {
        this.commentAuthorPic = commentAuthorPic;
    }

    private String commentString;
    private String commentPic;
    private boolean hasPic;

    public String getCommentPic() {
        return commentPic;
    }

    public void setCommentPic(String commentPic) {
        this.commentPic = commentPic;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    private boolean hasImage;
    private long commentTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getCommentAuthor() {
        return commentAuthor;
    }

    public void setCommentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
    }

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }
}
