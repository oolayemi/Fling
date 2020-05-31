package com.stylet.fling.Activities.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stylet.fling.Model.BlogPost;
import com.stylet.fling.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private CircleImageView usersImage;
    private TextView usersEmail;
    private TextView usersName;
    TextView followingcount, postcount;
    TextView followerscount;

    String profile_id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        usersImage = root.findViewById(R.id.usersImage);
        usersEmail = root.findViewById(R.id.usersEmail);
        usersName = root.findViewById(R.id.usersName);
        followerscount = root.findViewById(R.id.followercount);
        followingcount = root.findViewById(R.id.followingcount);
        postcount = root.findViewById(R.id.postcount);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        usersName.setText(user.getDisplayName());
        usersEmail.setText(user.getEmail());

        Glide.with(getActivity())
                .load(user.getPhotoUrl())
                .into(usersImage);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profile_id = prefs.getString("profileid", "none");

        getFollowers();
        getMyPosts();


        return root;

    }

    private void getFollowers(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(user.getUid()).child("Followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followerscount.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(user.getUid()).child("Following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingcount.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getMyPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BlogPost blogPost = snapshot.getValue(BlogPost.class);
                    if (blogPost.getUser_id().equals(user.getUid())){
                        i++;
                    }
                }
                postcount.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}