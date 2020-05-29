package com.stylet.fling.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stylet.fling.Activities.Fragment.ProfileFragment;
import com.stylet.fling.Activities.MainActivity;
import com.stylet.fling.Model.User;
import com.stylet.fling.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;

    private Boolean isFragment;

    public UserAdapter(Context context, List<User> mUsers, Boolean isFragment) {
        this.context = context;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getUseremail());

        Glide.with(context).load(user.getUserimage()).into(holder.image_profile);
        isFollowing(user.getUserId(), holder.btn_follow);

        if (user.getUserId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragment){
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getUserId());
                editor.apply();


                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }else {
                    Intent toMain = new Intent(context, MainActivity.class);
                    toMain.putExtra("userid", user.getUserId());
                    context.startActivity(toMain);
                }

            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_follow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getUserId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserId()).child("Followers").child(firebaseUser.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getUserId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserId()).child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView username, fullname;
        private Button btn_follow;
        private CircleImageView image_profile;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            username = mView.findViewById(R.id.username);
            fullname = mView.findViewById(R.id.fullname);
            btn_follow = mView.findViewById(R.id.btn_follow);
            image_profile = mView.findViewById(R.id.image_profile);

        }
    }

    private void isFollowing(final String userid, final Button button){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    button.setText("Following");
                }else {
                    button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
