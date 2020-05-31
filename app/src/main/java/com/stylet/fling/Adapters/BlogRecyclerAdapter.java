package com.stylet.fling.Adapters;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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
import com.stylet.fling.Activities.CommentsActivity;
import com.stylet.fling.Activities.ShowImageActivity;
import com.stylet.fling.Model.BlogPost;
import com.stylet.fling.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {


    private List<BlogPost> blog_list;
    public Context context;
    private boolean isDark = false;




    public BlogRecyclerAdapter( Context context, List<BlogPost> blog_list, boolean isDark) {
        this.blog_list = blog_list;
        this.context = context;
        this.isDark = isDark;
    }

    public BlogRecyclerAdapter(List<BlogPost> blog_list){

        this.blog_list = blog_list;

    }

    private FirebaseAuth firebaseAuth;

    private TextView popupsave, popupsave_nonuser, popupdelete, popupaddfav, popupaddfav_nonuser, popupcancel, popupcancel_nonuser;
    private View popupviewdelete;

    private Dialog popupOption, popupOption2;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();

        popupOption = new Dialog(context);
        popupOption.setContentView(R.layout.options_popup_user);
        popupOption.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupOption.getWindow().setLayout(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupOption.getWindow().getAttributes().gravity = Gravity.CENTER;

        popupviewdelete = popupOption.findViewById(R.id.popupview_delete);
        popupsave = popupOption.findViewById(R.id.popup_save);
        popupdelete = popupOption.findViewById(R.id.popup_delete);
        popupaddfav = popupOption.findViewById(R.id.popup_addfav);
        popupcancel = popupOption.findViewById(R.id.popupcancel);

        popupOption2 = new Dialog(context);
        popupOption2.setContentView(R.layout.options_popup_user);
        popupOption2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupOption2.getWindow().setLayout(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popupOption2.getWindow().getAttributes().gravity = Gravity.CENTER;

        popupsave_nonuser = popupOption.findViewById(R.id.popup_save_nonuser);
        popupaddfav_nonuser = popupOption.findViewById(R.id.popup_addfav_nonuser);
        popupcancel_nonuser = popupOption.findViewById(R.id.popupcancel_nonuser);

        firebaseAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (firebaseAuth.getCurrentUser() != null){

            holder.setIsRecyclable(false);

            final String blogPostId = blog_list.get(position).getPostKey();
            final String currentUserId = firebaseAuth.getCurrentUser().getUid();

            String desc_data = blog_list.get(position).getDesc();
            holder.setDescText(desc_data);

            final String image_url = blog_list.get(position).getImage_url();
            String thumbUri = blog_list.get(position).getImage_thumb();
            holder.setBlogImage(image_url, thumbUri);

            final String blog_user_id = blog_list.get(position).getUser_id();

            String userName = blog_list.get(position).getUsername();
            final String userImage = blog_list.get(position).getUser_image();

            holder.setUserData(userName, userImage);

            try {

                long timestamp = (long) blog_list.get(position).getTimestamp();


                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                calendar.setTimeInMillis(timestamp);

                Date past = calendar.getTime();


                Date now = new Date();

                long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

                String time;

                if(seconds<60) {
                    time = "Just now";
                } else if (seconds >60 && seconds < 120){
                    time = "About a minute ago";
                } else if(minutes<60) {
                    time = minutes+" minutes ago";
                } else if(hours<24) {
                    time = hours+" hours ago";
                } else {
                    time = days+" days ago";
                }

                holder.setTime(time);

            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();

            }

            holder.blogImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent toShowImage = new Intent(context, ShowImageActivity.class);
                    toShowImage.putExtra("blog_image", blog_list.get(position).getImage_url());
                    context.startActivity(toShowImage);

                    Animatoo.animateSwipeRight(context);


                }
            });



            holder.optPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (blog_user_id.equals(currentUserId)){


                        final String image_link = blog_list.get(position).getImage_thumb();
                        final String usename = blog_list.get(position).getUsername();
                        if (image_link == null){
                            popupsave.setVisibility(View.GONE);
                            Toast.makeText(context, "no image link", Toast.LENGTH_SHORT).show();
                            popupOption.show();
                        }

                        Toast.makeText(context, "My post", Toast.LENGTH_SHORT).show();
                        popupOption.show();

                    }else {

                        final String image_link = blog_list.get(position).getImage_thumb();
                        final String usename = blog_list.get(position).getUsername();

                        if (image_link == null){
                            Toast.makeText(context, "no image link from user", Toast.LENGTH_SHORT).show();
                            popupsave.setVisibility(View.GONE);
                            popupOption.show();
                        }

                        Toast.makeText(context, "Not mine", Toast.LENGTH_SHORT).show();
                        popupOption.show();

                    }

                    holder.optPopup.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_up));
                    //Display Option menu

                    if (blog_user_id.equals(currentUserId)) {

                        popupOption.show();

                        final String image_url = blog_list.get(position).getImage_url();
                        final String filename = blog_list.get(position).getUsername();

                        if (image_url == null){

                            popupsave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    popupOption.dismiss();
                                    holder.optPopup.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));

                                    downloadFile(context, filename, DIRECTORY_DOWNLOADS, image_url);

                                }
                            });


                        }else {
                            popupsave.setVisibility(View.GONE);
                        }


                        popupdelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                popupOption.dismiss();
                                holder.optPopup.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child("Posts")
                                        .child(blogPostId);
                                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        blog_list.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Post not deleted: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });



                            }
                        });

                        popupaddfav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }

                        });

                        popupcancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                popupOption.dismiss();

                            }
                        });

                    }else {

                        popupOption.show();

                        final String image_url = blog_list.get(position).getImage_url();
                        final String filename = blog_list.get(position).getUsername();

                        if (image_url == null){
                            popupsave.setVisibility(View.GONE);
                        }
                        popupsave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                popupOption.dismiss();
                                holder.optPopup.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));


                                downloadFile(context, filename, DIRECTORY_DOWNLOADS, image_url);

                            }
                        });

                        popupdelete.setVisibility(View.GONE);
                        popupviewdelete.setVisibility(View.GONE);

                        popupaddfav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        popupcancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.optPopup.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_down));
                                popupOption.dismiss();

                            }
                        });

                    }
                }


            });


            //Get Likes Count


            //Set Likes

            isLikes(blogPostId, holder.blogLikeBtn);
            nrLikes(holder.blogLikeCount, blogPostId);
            nrComment(holder.blogCommentCount, blogPostId);


            holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.blogLikeBtn.getTag().equals("like")){
                        FirebaseDatabase.getInstance().getReference().child("Posts").child(blogPostId).child("Likes")
                                .child(currentUserId).setValue(true);
                    }else {
                        FirebaseDatabase.getInstance().getReference().child("Posts").child(blogPostId).child("Likes")
                                .child(currentUserId).removeValue();
                    }
                }
            });




            //Get Comments Count



            holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent commentIntent = new Intent(context, CommentsActivity.class);
                    commentIntent.putExtra("blog_post_id", blogPostId);
                    commentIntent.putExtra("post_user_image", blog_list.get(position).getUser_image());
                    commentIntent.putExtra("post_user_name", blog_list.get(position).getUsername());
                    commentIntent.putExtra("postImage", blog_list.get(position).getImage_thumb());
                    commentIntent.putExtra("description", blog_list.get(position).getDesc());
                    commentIntent.putExtra("userPhoto", blog_list.get(position).getUser_image());
                    commentIntent.putExtra("userName", blog_list.get(position).getUsername());

                    context.startActivity(commentIntent);
                        Animatoo.animateSlideUp(context);

                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView blogDate, descView, blogUserName;
        private ImageView optPopup;

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;


        private ImageView blogCommentBtn;

        private TextView blogCommentCount;

        private ImageView blogImageView;

        private Dialog popupOption;

        private CircleImageView blogUserImage;

        private CardView main_blog_post;




        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            descView = mView.findViewById(R.id.post_detail_desc);
            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);
            optPopup = mView.findViewById(R.id.arrow_down);
            blogDate = mView.findViewById(R.id.blog_date);

            blogImageView = mView.findViewById(R.id.post_detail_img);

            blogCommentCount  = mView.findViewById(R.id.blog_comment_count);

            main_blog_post = mView.findViewById(R.id.main_blog_post);

            blogUserName  = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.post_detail_user_img);

            boolean isDark = getThemeStatePref();
            if (isDark){
                setDarkTheme();
            }



        }

        private void setDarkTheme (){

            descView.setTextColor(context.getResources().getColor(R.color.text_color_white));
            blogCommentCount.setTextColor(context.getResources().getColor(R.color.text_color_white));
            blogLikeCount.setTextColor(context.getResources().getColor(R.color.text_color_white));
            blogDate.setTextColor(context.getResources().getColor(R.color.text_color_white));
            blogUserName.setTextColor(context.getResources().getColor(R.color.text_color_white));
            main_blog_post.setBackgroundColor(context.getResources().getColor(R.color.card_bg_color_dark));
        }

        private void setDescText(String descText){

            if (descText == null){
                descView.setVisibility(View.GONE);
            }
            descView.setText(descText);
        }

        private void setBlogImage(String downloadUri, String thumbUri){
            CardView blog_image_card = mView.findViewById(R.id.blog_image_card);
            if (downloadUri == null || thumbUri == null){
                blog_image_card.setVisibility(View.GONE);
            }
            Glide.with(context).load(thumbUri).into(blogImageView);
        }

        private void setTime(String date) {
            blogDate.setText(date);
        }

        private void setUserData(String name, String image){

            blogUserName.setText(name);

            Glide.with(context).load(image).into(blogUserImage);
        }
    }

    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("MMM dd, yyyy",calendar).toString();
        return date;

    }

    private void isLikes(String postId, final ImageView imageView){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(postId)
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

    private boolean getThemeStatePref(){
        SharedPreferences pref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
        boolean isDark = pref.getBoolean("isDark", false);
        return isDark;
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
    private void downloadFile(Context context, String fileName, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + ".jpg");

        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }



}
