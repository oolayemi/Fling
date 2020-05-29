package com.stylet.fling.Activities.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stylet.fling.Activities.MainActivity;
import com.stylet.fling.Activities.NewPostActivity;
import com.stylet.fling.Adapters.BlogRecyclerAdapter;
import com.stylet.fling.Adapters.StatusRecyclerAdapter;
import com.stylet.fling.Model.BlogPost;
import com.stylet.fling.Model.StatusPost;
import com.stylet.fling.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private List<BlogPost> blog_list;
    private List<StatusPost> status_list;

    FloatingActionButton fabSwitcher;
    boolean isDark = false;

    ConstraintLayout home_layout;


    private RecyclerView blog_list_view;

    List<String> followingList;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    private BlogRecyclerAdapter blogRecyclerAdapter;

    private StatusRecyclerAdapter statusRecyclerAdapter;

    private Boolean isFirstPageFirstLoad = true;
    private Boolean isFirstStatusFirstLoad = true;

    private String currentUserId;

    private RecyclerView status_list_view;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        enablePersistence();
        CircleImageView userImage = view.findViewById(R.id.toolbar_userimage);

        fabSwitcher = view.findViewById(R.id.fab_switcher);
        home_layout = view.findViewById(R.id.home_layout);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newPost = new Intent(getContext(), NewPostActivity.class);
                startActivity(newPost);

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        currentUserId = user.getUid();

        databaseReference = firebaseDatabase.getReference("Status");

        blog_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.blog_list_view);

        status_list = new ArrayList<>();
        status_list_view = view.findViewById(R.id.status_list_view);

        progressBar = view.findViewById(R.id.pro);


        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setReverseLayout(true);
        blog_list_view.setLayoutManager(linearLayoutManager);
        /*blog_list_view.setAdapter(blogRecyclerAdapter);*/
        blog_list_view.setHasFixedSize(true);

        /*blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);*/

        statusRecyclerAdapter = new StatusRecyclerAdapter(getContext(), status_list);
        status_list_view.setLayoutManager(new LinearLayoutManager (container.getContext(), LinearLayoutManager.HORIZONTAL, false));
        status_list_view.setAdapter(statusRecyclerAdapter);
        status_list_view.setHasFixedSize(true);

        Glide.with(container.getContext()).load(user.getPhotoUrl()).into(userImage);




        checkFollowing();

        fabSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDark = !isDark;
                if (isDark){

                    home_layout.setBackgroundColor(getResources().getColor(R.color.black));

                }else {
                    home_layout.setBackgroundColor(getResources().getColor(R.color.white));
                }

                blogRecyclerAdapter = new BlogRecyclerAdapter(container.getContext(), blog_list, isDark);
                blog_list_view.setAdapter(blogRecyclerAdapter);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                status_list = new ArrayList<>();

                for (DataSnapshot statusMap: dataSnapshot.getChildren()){
                    StatusPost statusPost = statusMap.getValue(StatusPost.class);

                    status_list.add(statusPost);
                }

                statusRecyclerAdapter = new StatusRecyclerAdapter(getContext(), status_list);
                if (status_list.size() == 0){
                    status_list_view.setVisibility(View.GONE);
                }
                status_list_view.setAdapter(statusRecyclerAdapter);
                progressBar.setVisibility(View.GONE);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference.keepSynced(true);

        return view;

    }

    private void checkFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                readPost();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPost(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = databaseReference.limitToLast(20).orderByChild("timestamp");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                blog_list = new ArrayList<>();

                for (DataSnapshot postMap: dataSnapshot.getChildren()){
                    BlogPost blogPost = postMap.getValue(BlogPost.class);
                    for (String id : followingList){
                        if (blogPost.getUser_id().equals(id)){
                            blog_list.add(blogPost);
                        }
                    }
                    if (blogPost.getUser_id().equals(currentUserId)){
                        blog_list.add(blogPost);
                    }
                }
                blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
                blog_list_view.setAdapter(blogRecyclerAdapter);
                blogRecyclerAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enablePersistence() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
