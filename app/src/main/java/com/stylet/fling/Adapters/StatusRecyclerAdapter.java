package com.stylet.fling.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stylet.fling.Model.StatusPost;
import com.stylet.fling.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusRecyclerAdapter extends RecyclerView.Adapter<StatusRecyclerAdapter.ViewHolder> {

    public Context context;
    public List<StatusPost> status_list;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    public StatusRecyclerAdapter(Context context, List<StatusPost> status_list) {
        this.context = context;
        this.status_list = status_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_items, parent, false);
        context = parent.getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);


        final String statusPostId = status_list.get(position).getStatusKey();
        String desc = status_list.get(position).getDesc();

        String username = status_list.get(position).getUsername();
        String image = status_list.get(position).getImage_url();
        holder.setStatusData(desc, image);

        long timestamp = (long) status_list.get(position).getTimeset();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        Date statusPostTime = calendar.getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(statusPostTime);
        Date timePosted = cal.getTime();
        Date now = new Date();

        System.out.println("*****________________------------------_______" + timePosted + "________________---------------_______*****");

        cal.add(Calendar.MINUTE, 10);
        Date timeToExpire = cal.getTime();
        System.out.println("________________------------------_______" + timeToExpire + "________________------------------_______");


        holder.statusview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.statusview);
                if (currentUser.getUid().equals(status_list.get(position).getUser_id())){

                    popupMenu.inflate(R.menu.status_option_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete_status:

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                            .child("Status")
                                            .child(statusPostId);
                                    reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            status_list.remove(position);
                                            notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    break;
                            }
                            return false;
                        }
                    });

                    popupMenu.show();
                }
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return status_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView title;
        private TextView timeSet;
        private TextView timeExp;
        private CircleImageView statusImage;
        private ConstraintLayout statusview;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            timeExp = mView.findViewById(R.id.status_timeexpire);
            statusImage = mView.findViewById(R.id.status_image);
            statusview = mView.findViewById(R.id.status_view);

        }

        private void setStatusData(String timeset, String image) {


            timeExp.setText(timeset);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(statusImage);

        }
    }
}

