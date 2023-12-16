package com.ciberprotech.finalprojectadmin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ciberprotech.finalprojectadmin.adapter.UserAdapter;
import com.ciberprotech.finalprojectadmin.listner.UserSelectListener;
import com.ciberprotech.finalprojectadmin.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity implements UserSelectListener {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth  = FirebaseAuth.getInstance();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManageUsersActivity.this, "logout Success", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                startActivity(new Intent(ManageUsersActivity.this, LoginActivity.class));
            }
        });

        //View Users
        users = new ArrayList<>();

        RecyclerView userView = findViewById(R.id.loadUsers);

        UserAdapter userAdapter = new UserAdapter(users, ManageUsersActivity.this,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        userView.setLayoutManager(linearLayoutManager);

        userView.setAdapter(userAdapter);

        firestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange change: value.getDocumentChanges()){
                    User user = change.getDocument().toObject(User.class);
                    switch (change.getType()){
                        case ADDED:
                            users.add(user);
                        case MODIFIED:
                            User old = users.stream().filter(i->i.getFirstName().equals(user.getFirstName())).findFirst().orElse(null);
                            if(old!=null){
                                old.setFirstName(user.getFirstName());
                                old.setLastName(user.getLastName());
                                old.setEmail(user.getEmail());
                                old.setMobile(user.getMobile());
                                old.setImagePath(user.getImagePath());
                            }
                            break;
                        case REMOVED:
                            users.remove(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void deleteUser(User user) {

    }
}