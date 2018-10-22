package com.swati.passport.mobilechallenge;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    private List<UserVo> mDataset;
    private Context mContext;
    public static final String USER_ID = "USER_ID";

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView userImage;
        public TextView userName,age,gender,hobbies;

        public MyViewHolder(View view) {
            super(view);
            userImage = view.findViewById(R.id.userImage);
            userName = view.findViewById(R.id.userName);
            age = view.findViewById(R.id.age);
            gender = view.findViewById(R.id.gender);
            hobbies = view.findViewById(R.id.hobbies);
        }
    }

    public UserListAdapter(List<UserVo> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public UserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_user_row, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replacing the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final UserVo user = mDataset.get(position);
        if(user.getGender().equalsIgnoreCase("Female")){
            holder.itemView.setBackgroundResource(R.drawable.female);
        }else{
            holder.itemView.setBackgroundResource(R.drawable.male);
        }
        holder.userName.setText(user.getUserName());
        holder.age.setText(user.getAge()+"");
        holder.gender.setText(user.getGender());
        holder.hobbies.setText(user.getHobbies());
        Picasso.with(holder.userImage.getContext()).load(user.getUserImage()).transform(new Utils.CircleTransform()).into(holder.userImage);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ProfileActivity.class);
                intent.putExtra(USER_ID,String.valueOf(user.get_id()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}