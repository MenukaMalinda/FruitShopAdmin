package com.ciberprotech.finalprojectadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ciberprotech.finalprojectadmin.adapter.OrderAdapter;
import com.ciberprotech.finalprojectadmin.listner.OrderSelectListner;
import com.ciberprotech.finalprojectadmin.model.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageOrdersActivity extends AppCompatActivity implements OrderSelectListner {

    private FirebaseFirestore firestore;
    private ArrayList<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

        firestore = FirebaseFirestore.getInstance();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        orders = new ArrayList<>();

        RecyclerView orderView = findViewById(R.id.loadOrders);
        OrderAdapter orderAdapter = new OrderAdapter(orders, ManageOrdersActivity.this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        orderView.setLayoutManager(linearLayoutManager);
        orderView.setAdapter(orderAdapter);

        firestore.collection("orders").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Order order = snapshot.toObject(Order.class);
                                orders.add(order);
                            }
                            orderAdapter.notifyDataSetChanged();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {

                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                            findViewById(R.id.loader).setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });

    }

    @Override
    public void selectOrder(Order order) {
        startActivity(new Intent(ManageOrdersActivity.this,OrderItemsActivity.class)
                .putExtra("orderId",order.getId()));
    }

    @Override
    public void setDelivered(Order order) {
        firestore.collection("orders")
                .whereEqualTo("id",order.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                            Map<String,Object> map= new HashMap<>();
                            map.put("deliver_status",1);
                            firestore.document("orders/"+snapshot.getId()).update(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(),"Order has been Delivered",Toast.LENGTH_LONG).show();
                                        }
                                    });
                            break;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Try again later.",Toast.LENGTH_LONG).show();
                    }
                });
    }
}