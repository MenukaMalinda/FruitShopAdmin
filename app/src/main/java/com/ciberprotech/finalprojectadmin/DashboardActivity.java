package com.ciberprotech.finalprojectadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();

        //Animation
        CardView addProduct = findViewById(R.id.cardViewAddProduct);
        CardView manageProduct = findViewById(R.id.cardViewManageProduct);
        CardView manageUsers = findViewById(R.id.cardViewManageUsers);
        CardView addCategory = findViewById(R.id.cardViewAddCategory);
        CardView manageOrders = findViewById(R.id.cardViewManageOrders);

        Animation downFlip = AnimationUtils.loadAnimation(DashboardActivity.this,R.anim.downflip);
        downFlip.setDuration(4000);
        downFlip.setFillAfter(true);
        Animation upFlip = AnimationUtils.loadAnimation(DashboardActivity.this,R.anim.upflip);
        upFlip.setDuration(4000);
        upFlip.setFillAfter(true);
        Animation rightFlip = AnimationUtils.loadAnimation(DashboardActivity.this,R.anim.rightflip);
        rightFlip.setDuration(4000);
        rightFlip.setFillAfter(true);
        Animation leftFlip = AnimationUtils.loadAnimation(DashboardActivity.this,R.anim.leftflip);
        leftFlip.setDuration(4000);
        leftFlip.setFillAfter(true);

        addProduct.startAnimation(leftFlip);
        manageProduct.startAnimation(rightFlip);
        manageUsers.startAnimation(leftFlip);
        addCategory.startAnimation(rightFlip);
        manageOrders.startAnimation(upFlip);
        //Animation

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "logout Success", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            }
        });

        findViewById(R.id.cardViewAddProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this,AddProductActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.cardViewManageProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this,ManageProductActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cardViewManageUsers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this,ManageUsersActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cardViewAddCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this,AddCategoryActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cardViewManageOrders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this,ManageOrdersActivity.class);
                startActivity(intent);
            }
        });
    }
}