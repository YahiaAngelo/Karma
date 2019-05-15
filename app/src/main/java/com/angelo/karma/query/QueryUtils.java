package com.angelo.karma.query;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.angelo.karma.R;
import com.angelo.karma.adapter.CommentAdapter;
import com.angelo.karma.classes.Comment;
import com.angelo.karma.classes.Post;
import com.angelo.karma.classes.User;
import com.angelo.karma.database.PostDatabase;
import com.angelo.karma.interfaces.OnFetchCommentsCountListener;
import com.angelo.karma.interfaces.OnFetchUserListener;
import com.angelo.karma.interfaces.OnFetchUserPostsListener;
import com.angelo.karma.interfaces.OnLatestUpdateCheckListener;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import cz.msebera.android.httpclient.Header;
import eu.amirs.JSON;


public class QueryUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final String githubReleasesUrl = "https://api.github.com/repos/YahiaAngelo/Karma/releases/latest";
    private static FirebaseAuth mAuth;



    private QueryUtils(){

    }


    public static void fetchUserInfo(String username, final OnFetchUserListener listener){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String username;
                            String profilePic;
                            String firstName;
                            String lastName;
                            String bio;
                            int following;
                            int followers;
                            List<String> followingList ;
                            List<String> followersList ;
                            username = documentSnapshot.get("username").toString();
                            firstName = documentSnapshot.get("first_name").toString();
                            lastName = documentSnapshot.get("last_name").toString();
                            profilePic = documentSnapshot.get("profilepic").toString();
                            bio = documentSnapshot.get("bio").toString();
                            following = Integer.parseInt(documentSnapshot.get("following").toString());
                            followers = Integer.parseInt(documentSnapshot.get("followers").toString());
                            followingList = (List<String>) documentSnapshot.get("following_array");
                            followersList = (List<String>) documentSnapshot.get("followers_array");
                            User user = new User();
                            user.setUsername(username);
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setProfilePic(profilePic);
                            user.setBio(bio);
                            user.setFollowing(following);
                            user.setFollowers(followers);
                            user.setFollowing_list(followingList);
                            user.setFollowers_list(followersList);
                            listener.onSuccess(user);
                        }else {
                            listener.onFailure();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });


    }

    public static void fetchPosts(Context context, String followingList){
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        PostDatabase postDatabase = PostDatabase.getInstance(context);

            CollectionReference documentReference =  db.collection("posts").document(followingList).collection("posts");

        documentReference.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (int p =0; p < queryDocumentSnapshots.size(); ++p){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(p);
                        if (documentSnapshot.exists()){
                            String postAuthor = followingList;
                            Post post = new Post();

                            String postAuthorImage = documentSnapshot.get("author_pic").toString();
                            String postDesc = documentSnapshot.get("desc").toString();
                            Long postTime = documentSnapshot.getLong("time");
                            int postNum = Integer.valueOf(documentSnapshot.get("post_num").toString());
                            String postImage = documentSnapshot.get("pic").toString();
                            String postLike = documentSnapshot.get("likes").toString();
                            String postDislike = documentSnapshot.get("dislikes").toString();
                            Boolean postLiked = documentSnapshot.getBoolean("liked");
                            Boolean hasPic = documentSnapshot.getBoolean("has_pic");
                            List<String> likesList = (List<String>) documentSnapshot.get("likes_array");
                            List<String> dislikesList = (List<String>) documentSnapshot.get("dislikes_array");


                            post.setPostId(postTime);
                            post.setPostAuthorName(postAuthor);
                            post.setPostAuthorImage(postAuthorImage);
                            post.setPostTime(postTime);
                            post.setPostNum(postNum);
                            post.setPostImage(postImage);
                            post.setPostDesc(postDesc);
                            post.setLikes(Integer.valueOf(postLike));
                            post.setDislikes(Integer.valueOf(postDislike));
                            post.setLiked(postLiked);
                            post.setHasImage(hasPic);
                            post.setLikes_list(likesList);
                            post.setDislikes_list(dislikesList);
                            postDatabase.postDao().save(post);


                        }


                    }
                }
            });



    }



    public static void addNewPost(String postText, String postImage, String authorPic, long postTime, Boolean hasPic, String userName){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> postDoc = new HashMap<>();
        postDoc.put("desc", postText);
        postDoc.put("dislikes", 0);
        postDoc.put("liked", true);
        postDoc.put("likes", 1);
        postDoc.put("pic", postImage);
        postDoc.put("time", postTime);
        postDoc.put("has_pic", hasPic);
        postDoc.put("author_pic", authorPic);
        postDoc.put("likes_array", Arrays.asList(userName));
        postDoc.put("dislikes_array", Arrays.asList());

        db.collection("posts").document(userName).collection("posts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int newPostNum = queryDocumentSnapshots.size();
                        postDoc.put("post_num", newPostNum);
                        db.collection("posts").document(userName).collection("posts").document(String.valueOf(newPostNum))
                                .set(postDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                    }
                                });
                    }
                });



    }

    public static void submitReact(boolean reactType, String postAuthor, int postId, String username){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference =  db.collection("posts").document(postAuthor).collection("posts").document(String.valueOf(postId));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> likesNames = (ArrayList<String>) documentSnapshot.get("likes_array");
                ArrayList<String> disLikesNames = (ArrayList<String>) documentSnapshot.get("dislikes_array");
                if (!likesNames.contains(username) && !disLikesNames.contains(username)){

                    if (reactType){

                        documentReference.update("likes", FieldValue.increment(1));
                        documentReference.update("liked", true);
                        ArrayList<String> newLikesNames = likesNames;
                        newLikesNames.add(username);
                        documentReference.update("likes_array", newLikesNames);



                    } else {
                        documentReference.update("dislikes", FieldValue.increment(1));
                        documentReference.update("liked", false);

                        ArrayList<String> newDislikesNames = disLikesNames;
                        newDislikesNames.add(username);
                        documentReference.update("dislikes_array", newDislikesNames);

                    }
                }else if (likesNames.contains(username) && !disLikesNames.contains(username)){

                    if (!reactType){

                        documentReference.update("likes", FieldValue.increment(-1));
                        documentReference.update("dislikes", FieldValue.increment(1));
                        documentReference.update("liked", false);
                        ArrayList<String> newLikedNames = likesNames;
                        newLikedNames.remove(likesNames.indexOf(username));
                        documentReference.update("likes_array", newLikedNames);
                        ArrayList<String> newDislikedNames = disLikesNames;
                        newDislikedNames.add(username);
                        documentReference.update("dislikes_array", newDislikedNames);

                    }

                }else if (!likesNames.contains(username) && disLikesNames.contains(username)){

                    if (reactType){
                        documentReference.update("likes", FieldValue.increment(1));
                        documentReference.update("dislikes", FieldValue.increment(-1));
                        documentReference.update("liked", true);
                        ArrayList<String> newLikedNames = likesNames;
                        newLikedNames.add(username);
                        documentReference.update("likes_array", newLikedNames);
                        ArrayList<String> newDislikedNames = disLikesNames;
                        newDislikedNames.remove(disLikesNames.indexOf(username));
                        documentReference.update("dislikes_array", newDislikedNames);
                    }
                }



            }
        });


    }


    public static void fetchComments(String postAuthor, int postNum, CommentAdapter commentAdapter, Activity activity ){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference documentReference =  db.collection("posts").document(postAuthor).collection("posts").document(String.valueOf(postNum)).collection("comments");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Comment> commentArrayList = new ArrayList<>();
                SpinKitView spinKitView = activity.findViewById(R.id.comment_progress);
                spinKitView.setVisibility(View.INVISIBLE);

                for (int c = 0; c < queryDocumentSnapshots.size(); c++){
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(c);

                    if (documentSnapshot.exists()){

                        Comment comment = new Comment();

                        comment.setCommentAuthor(documentSnapshot.get("author_name").toString());
                        comment.setCommentAuthorPic(documentSnapshot.get("author_pic").toString());
                        comment.setCommentPic(documentSnapshot.get("comment_pic").toString());
                        comment.setCommentString(documentSnapshot.get("comment_text").toString());
                        comment.setCommentTime(documentSnapshot.getLong("time"));
                        comment.setId(documentSnapshot.getLong("time"));
                        comment.setHasImage(documentSnapshot.getBoolean("hasPic"));

                        commentArrayList.add(comment);
                        commentAdapter.setCommentList(commentArrayList);


                    }else {
                        TextView noCommentsText = activity.findViewById(R.id.comment_empty_text);
                        noCommentsText.setText("No comments yet!");
                    }

                }

            }
        });


    }

    public static void fetchUserPosts(String username, final OnFetchUserPostsListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        CollectionReference documentReference =  db.collection("posts").document(username).collection("posts");
        documentReference.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                ArrayList<Post> postArrayList = new ArrayList<>();
                for (int p =0; p < queryDocumentSnapshots.size(); ++p){
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(p);
                    Post post = new Post();
                    if (documentSnapshot.exists()){
                        String postAuthor = username;
                        String postAuthorImage = documentSnapshot.get("author_pic").toString();
                        String postDesc = documentSnapshot.get("desc").toString();
                        Long postTime = documentSnapshot.getLong("time");
                        int postNum = Integer.valueOf(documentSnapshot.get("post_num").toString());
                        String postImage = documentSnapshot.get("pic").toString();
                        String postLike = documentSnapshot.get("likes").toString();
                        String postDislike = documentSnapshot.get("dislikes").toString();
                        Boolean postLiked = documentSnapshot.getBoolean("liked");
                        Boolean hasPic = documentSnapshot.getBoolean("has_pic");
                        List<String> likesList = (List<String>) documentSnapshot.get("likes_array");
                        List<String> dislikesList = (List<String>) documentSnapshot.get("dislikes_array");
                        post.setPostId(postTime);
                        post.setPostAuthorName(postAuthor);
                        post.setPostAuthorImage(postAuthorImage);
                        post.setPostTime(postTime);
                        post.setPostNum(postNum);
                        post.setPostImage(postImage);
                        post.setPostDesc(postDesc);
                        post.setLikes(Integer.valueOf(postLike));
                        post.setDislikes(Integer.valueOf(postDislike));
                        post.setLiked(postLiked);
                        post.setHasImage(hasPic);
                        post.setLikes_list(likesList);
                        post.setDislikes_list(dislikesList);

                        postArrayList.add(post);

                        listener.onSuccess(postArrayList);

                    }


                }
            }
        });
    }

    public static void fetchCommentsCount(String postAuthor, int postNum, final OnFetchCommentsCountListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference documentReference =  db.collection("posts").document(postAuthor).collection("posts").document(String.valueOf(postNum)).collection("comments");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                listener.onSuccess(queryDocumentSnapshots.size());
            }
        });
    }



    public static void addComment(String commentText,String commentPic ,long commentTime ,Boolean hasPic , String commentAuthor, String commentAuthorPic, String postAuthor, int postNum, CommentAdapter commentAdapter){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> commentDoc = new HashMap<>();
        commentDoc.put("author_name", commentAuthor);
        commentDoc.put("author_pic", commentAuthorPic);
        commentDoc.put("comment_pic", commentPic);
        commentDoc.put("comment_text", commentText);
        commentDoc.put("hasPic", hasPic);
        commentDoc.put("time", commentTime);
        Comment comment = new Comment();

        db.collection("posts").document(postAuthor).collection("posts").document(String.valueOf(postNum)).collection("comments").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int newCommentNum = queryDocumentSnapshots.size();
                        commentDoc.put("comment_num", newCommentNum);
                        db.collection("posts").document(postAuthor).collection("posts").document(String.valueOf(postNum)).collection("comments")
                                .document(String.valueOf(newCommentNum)).set(commentDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                comment.setId(commentTime);
                                comment.setHasImage(hasPic);
                                comment.setCommentTime(commentTime);
                                comment.setCommentString(commentText);
                                comment.setCommentPic(commentPic);
                                comment.setCommentAuthor(commentAuthor);
                                comment.setCommentAuthorPic(commentAuthorPic);
                                commentAdapter.addComment(comment);

                            }
                        });
                    }
                });

    }

    public static void submitFollow(boolean followType, String username, List<String> followingList, String followerName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userDoc = new HashMap<>();

        if (followType){
            followingList.add(followerName);
            userDoc.put("followers_array", followingList);
            userDoc.put("followers", FieldValue.increment(1));

        }else {
            followingList.remove(followerName);
            userDoc.put("followers_array", followingList);
            userDoc.put("followers", FieldValue.increment(-1));
        }

        db.collection("users").document(username)
                .update(userDoc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        QueryUtils.fetchUserInfo(followerName, new OnFetchUserListener() {
                            @Override
                            public void onSuccess(User user) {
                                List<String> followingList = user.following_list;
                                Map<String, Object> followerDoc = new HashMap<>();
                                if (followType){
                                    followingList.add(username);
                                    followerDoc.put("following_array", followingList);
                                    followerDoc.put("following", FieldValue.increment(1));
                                }else {
                                    followingList.remove(username);
                                    followerDoc.put("following_array", followingList);
                                    followerDoc.put("following", FieldValue.increment(-1));
                                }

                                db.collection("users").document(followerName)
                                        .update(followerDoc)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });

                            }

                            @Override
                            public void onFailure() {

                            }
                        });

                    }
                });
    }

    public static void getLatestUpdate(OnLatestUpdateCheckListener listener){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(githubReleasesUrl, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFailure();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JSON json =new JSON(responseString);
                String latestVersion = json.key("tag_name").stringValue();
                listener.onSuccess(latestVersion);
            }
        });
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
}
