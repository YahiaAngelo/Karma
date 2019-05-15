package com.angelo.karma.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angelo.karma.R;
import com.angelo.karma.adapter.PostAdapter;
import com.angelo.karma.classes.Post;
import com.angelo.karma.classes.User;
import com.angelo.karma.database.PostDatabase;
import com.angelo.karma.databinding.FragmentMainBinding;
import com.angelo.karma.interfaces.OnFetchUserListener;
import com.angelo.karma.query.QueryUtils;
import com.angelo.karma.viewmodel.PostViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private PostAdapter postAdapter = new PostAdapter(new ArrayList<Post>());
    SwipeRefreshLayout swipeLayout;
    private FirebaseAuth mAuth;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMainBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        View view = binding.getRoot();



        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        PostViewModel postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        mAuth = FirebaseAuth.getInstance();




        final PostDatabase postDatabase = PostDatabase.getInstance(getContext());


        RecyclerView recyclerView = (RecyclerView) binding.list;

        recyclerView.setAdapter(postAdapter);

        postAdapter.setData(postDatabase.postDao().findAll());

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                QueryUtils.fetchUserInfo(mAuth.getCurrentUser().getDisplayName(), new OnFetchUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        for (String following: user.following_list){
                            QueryUtils.fetchPosts(getContext(), following);
                        }
                        postDatabase.postDao().deleteAll();
                        postAdapter.setData(postDatabase.postDao().findAll());
                        swipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure() {

                    }
                });


            }
        });


        postViewModel.getAllPosts().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                postAdapter.updateData(posts);

            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        //Reverse the layout to get latest post first
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setItemAnimator(new AlphaCrossFadeAnimator());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(true);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onRefresh() {

    }
}
