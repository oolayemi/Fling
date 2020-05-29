package com.stylet.fling.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stylet.fling.Model.RepliedComments;
import com.stylet.fling.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RepliedCommentsRecyclerAdapter extends RecyclerView.Adapter<RepliedCommentsRecyclerAdapter.ViewHolder> {

    public List<RepliedComments> repliedCommentsList;
    public Context context;

    public RepliedCommentsRecyclerAdapter(List<RepliedComments> repliedCommentsList){

        this.repliedCommentsList = repliedCommentsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.replied_comment_list_item, parent, false);
        context = parent.getContext();

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String commentMessage = repliedCommentsList.get(position).getMessage();


        String userName = repliedCommentsList.get(position).getName();
        String userImage = repliedCommentsList.get(position).getImage();


        holder.setUserData(commentMessage, userName, userImage);

    }

    @Override
    public int getItemCount() {

        if(repliedCommentsList != null) {

            return repliedCommentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;



        private TextView comment_message;
        private TextView comment_username;
        private CircleImageView comment_image;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUserData(String message,String name, String image ){

            comment_message = mView.findViewById(R.id.replied_comment_message);
            comment_message.setText(message);

            comment_username = mView.findViewById(R.id.replied_comment_username);
            comment_username.setText(name);

            comment_image = mView.findViewById(R.id.replied_comment_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image).into(comment_image);
        }
    }
}
