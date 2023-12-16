package com.ciberprotech.finalprojectadmin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ciberprotech.finalprojectadmin.R;
import com.ciberprotech.finalprojectadmin.listner.UserSelectListener;
import com.ciberprotech.finalprojectadmin.model.User;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> users;
    private Context context;
    private FirebaseStorage storage;
    private UserSelectListener selectListener;

    public UserAdapter(ArrayList<User> users, Context context, UserSelectListener selectListener) {
        this.users = users;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = users.get(position);
        holder.firstName.setText(user.getFirstName()+" "+user.getLastName());
        holder.email.setText(user.getEmail());
        holder.mobile.setText(user.getMobile());

//        holder.deleteUserBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                selectListener.deleteUser(users.get(position));
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstName;
        TextView email;
        TextView mobile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.userEmail);
            mobile = itemView.findViewById(R.id.userMobile);

        }
    }

}
