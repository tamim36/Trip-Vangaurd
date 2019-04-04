package com.example.tripvanguard.findTrekker;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tripvanguard.R;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter< FriendAdapter.FriendViewHolder >{

    private Context mCtx;
    private List<User> friendList;

    public FriendAdapter(Context mCtx, List<User> friendList){
        this.mCtx = mCtx;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater  = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout , null );
        FriendViewHolder holder = new FriendViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User user = friendList.get(position);

        holder.textViewName.setText( user.getName());
        holder.textViewEmail.setText(user.getEmail());
        holder.textViewKey.setText(user.getKey());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder{

        TextView textViewName,textViewEmail,textViewKey;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.friend_name);
            textViewEmail = itemView.findViewById(R.id.friend_email);
            textViewKey = itemView.findViewById(R.id.friend_key);
        }
    }
}
