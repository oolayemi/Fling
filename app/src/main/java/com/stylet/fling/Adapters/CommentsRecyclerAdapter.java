package com.stylet.fling.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stylet.fling.Activities.ReplyCommentActivity;
import com.stylet.fling.Model.Comments;
import com.stylet.fling.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    FirebaseAuth mAuth;
    FirebaseUser user;


    public CommentsRecyclerAdapter(List<Comments> commentsList){

        this.commentsList = commentsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.setIsRecyclable(false);

        final String commentMessage = commentsList.get(position).getMessage();
        final String post_id = commentsList.get(position).getBlog_id();
        final String comment_id = commentsList.get(position).getCommentKey();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyComment = new Intent(context, ReplyCommentActivity.class);
                replyComment.putExtra("blog_post_id",commentsList.get(position).getBlog_id());
                replyComment.putExtra("comment_id", commentsList.get(position).getCommentKey());
                replyComment.putExtra("comment_user_name", commentsList.get(position).getName());

                context.startActivity(replyComment);
            }
        });

        isLikes(post_id, holder.like, comment_id);
        nrLikes(holder.likes_count, post_id, comment_id);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id).child("Comments").child(comment_id).child("Likes")
                            .child(user.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id).child("Comments").child(comment_id).child("Likes")
                            .child(user.getUid()).removeValue();
                }
            }
        });

        nrComment(holder.comment_count, commentsList.get(position).getBlog_id(), commentsList.get(position).getCommentKey());

        holder.setUserData(commentMessage, commentsList.get(position).getName(), commentsList.get(position).getImage(), commentsList.get(position).getUser_email());
    }


    @Override
    public int getItemCount() {

        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;



        private ImageView like;
        private TextView comment_count, likes_count;
        private TextView comment_message;
        private TextView comment_username;
        private CircleImageView comment_image;
        private TextView comment_email;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            comment_count = mView.findViewById(R.id.cmt_replyCount);
            likes_count = mView.findViewById(R.id.cmt_likesCount);
            like = mView.findViewById(R.id.cmt_like_btn);
        }

        public void setUserData(String message,String name, String image, String email ){

            comment_email = mView.findViewById(R.id.cmt_useremail);
            comment_email.setText(email);

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

            comment_username = mView.findViewById(R.id.comment_username);
            comment_username.setText(name);

            comment_image = mView.findViewById(R.id.comment_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image).into(comment_image);
        }
    }
    private void nrComment(final TextView commentholder, String postid, String comment_id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(postid)
                .child("Comments")
                .child(comment_id)
                .child("RepliedComments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentholder.setText("" +dataSnapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void isLikes(String postId, final ImageView imageView, String comment_id){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(postId)
                .child("Comments")
                .child(comment_id)
                .child("Likes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getUid()).exists()){
                    imageView.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));
                    imageView.setTag("liked");
                }else {
                    imageView.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(final TextView likes, String postid, String comment_id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(postid)
                .child("Comments")
                .child(comment_id)
                .child("Likes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    likes.setText("" +dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}