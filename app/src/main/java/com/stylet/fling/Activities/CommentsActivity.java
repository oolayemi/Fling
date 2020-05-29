package com.stylet.fling.Activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stylet.fling.Adapters.CommentsRecyclerAdapter;
import com.stylet.fling.Model.Comments;
import com.stylet.fling.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    ImageView imgPost;
    CircleImageView imgPostUser, currentuser_img;
    TextView txtPostDesc, txtPostDateName, blog_user_name, blog_like_count, blog_comment_count;
    EditText editTextComment;
    Button btnAddComment;
    CardView mainpost;

    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String blog_post_id, post_user_image, post_user_name;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        imgPost = findViewById(R.id.post_detail_img);
        imgPostUser = findViewById(R.id.post_detail_user_img);
        currentuser_img = findViewById(R.id.currentuser_img);

        txtPostDesc = findViewById(R.id.post_detail_desc);
        //txtPostDateName = findViewById(R.id.post_detail_date_name);

        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);

        comment_list = findViewById(R.id.rv_comment);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        blog_post_id = getIntent().getStringExtra("blog_post_id");
        post_user_image = getIntent().getStringExtra("post_user_image");
        post_user_name = getIntent().getStringExtra("post_user_name");
        blog_user_name = findViewById(R.id.blog_user_name);
        blog_comment_count = findViewById(R.id.blog_comment_count);
        blog_like_count = findViewById(R.id.blog_like_count);

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Posts").child(blog_post_id).child("Comments");

        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);


        String postImage = getIntent().getExtras().getString("postImage");
        mainpost = findViewById(R.id.post_detail_img_card);
        if (postImage == null){

            mainpost.setVisibility(View.GONE);
        }
        Glide.with(getApplicationContext())
                .load(firebaseUser.getPhotoUrl())
                .into(currentuser_img);
        Glide.with(this).load(postImage).into(imgPost);


        String postDescription = getIntent().getExtras().getString("description");
        if (postDescription == null){
            txtPostDesc.setVisibility(View.GONE);
        }
        txtPostDesc.setText(postDescription);
        //blog_user_name.setText(""+ blog_user_name);


        Glide.with(this).load(post_user_image).into(imgPostUser);

        nrLikes(blog_like_count, blog_post_id);
        nrComment(blog_comment_count, blog_post_id);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                commentsList = new ArrayList<>();

                if (dataSnapshot.exists()) {

                    for (DataSnapshot postMap : dataSnapshot.getChildren()) {
                        Comments comments = postMap.getValue(Comments.class);
                        commentsList.add(comments);
                        commentsRecyclerAdapter.notifyDataSetChanged();
                    }

                    commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
                    comment_list.setAdapter(commentsRecyclerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message = editTextComment.getText().toString();

                if (!comment_message.isEmpty()) {


                    Comments comments = new Comments(comment_message, current_user_id, blog_post_id, firebaseUser.getDisplayName(), firebaseUser.getPhotoUrl().toString(), firebaseUser.getEmail());

                    addComment(comments);


                }else {
                    Toast.makeText(CommentsActivity.this, "Pls add text to fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addComment(Comments comments) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Posts").child(blog_post_id).child("Comments").push();

        String key = myRef.getKey();

        comments.setCommentKey(key);

        myRef.setValue(comments).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CommentsActivity.this, "Comment added", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentsActivity.this, "Database Error: "+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void nrLikes(final TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(postid)
                .child("Likes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 1){

                    String likes_count = dataSnapshot.getChildrenCount() + " Like";
                    likes.setText(likes_count);

                }
                else {
                    String likes_count = dataSnapshot.getChildrenCount() + " Likes";
                    likes.setText(likes_count);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrComment(final TextView commentholder, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(postid)
                .child("Comments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 1){
                    commentholder.setText(dataSnapshot.getChildrenCount() + " Comment");
                }
                else {
                    commentholder.setText(dataSnapshot.getChildrenCount() + " Comments");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}