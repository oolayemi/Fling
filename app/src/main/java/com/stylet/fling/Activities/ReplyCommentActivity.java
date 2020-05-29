package com.stylet.fling.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stylet.fling.Adapters.RepliedCommentsRecyclerAdapter;
import com.stylet.fling.Model.RepliedComments;
import com.stylet.fling.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyCommentActivity extends AppCompatActivity {

    EditText editText;
    CircleImageView circleImageView;

    FirebaseUser user;
    private String blog_post_id;

    private RepliedCommentsRecyclerAdapter repliedCommentsRecyclerAdapter;
    private List<RepliedComments> repliedCommentsList;
    String comment_id;


    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_comment);

        comment_id = getIntent().getStringExtra("comment_id");
        String comment_username = getIntent().getStringExtra("comment_user_name");

        firebaseAuth = FirebaseAuth.getInstance();

        editText = findViewById(R.id.reply_comment_field);
        circleImageView = findViewById(R.id.reply_comment_post_btn);

        blog_post_id = getIntent().getStringExtra("blog_post_id");

        user = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Posts").child(blog_post_id).child("Comments").child(comment_id).child("RepliedComments");

        final RecyclerView replied_comment_list = findViewById(R.id.replied_comment_list);



        repliedCommentsRecyclerAdapter = new RepliedCommentsRecyclerAdapter(repliedCommentsList);
        replied_comment_list.setHasFixedSize(true);
        replied_comment_list.setLayoutManager(new LinearLayoutManager(this));
        replied_comment_list.setAdapter(repliedCommentsRecyclerAdapter);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                repliedCommentsList = new ArrayList<>();

                if (dataSnapshot.exists()) {

                    for (DataSnapshot postMap : dataSnapshot.getChildren()) {
                        RepliedComments repliedComments = postMap.getValue(RepliedComments.class);
                        repliedCommentsList.add(repliedComments);
                        repliedCommentsRecyclerAdapter.notifyDataSetChanged();
                    }

                    repliedCommentsRecyclerAdapter = new RepliedCommentsRecyclerAdapter(repliedCommentsList);
                    replied_comment_list.setAdapter(repliedCommentsRecyclerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        

        String userImage = Objects.requireNonNull(user.getPhotoUrl()).toString();
        setUserImage(userImage);





        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editvalue = editText.getText().toString();

                
                RepliedComments repliedComments = new RepliedComments(editvalue, user.getUid(), user.getDisplayName(), user.getPhotoUrl().toString());
                
                addRepliedComment(repliedComments);

            }
        });
    }

    private void addRepliedComment(RepliedComments repliedComments) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Posts").child(blog_post_id).child("Comments").child(comment_id).child("RepliedComments").push();

        String key = myRef.getKey();

        repliedComments.setRepliedCommentKey(key);

        myRef.setValue(repliedComments).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ReplyCommentActivity.this, "Comment added", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReplyCommentActivity.this, "Error: "+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void setUserImage(String image){

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_placeholder);
        Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions).load(image).into(circleImageView);

    }
}