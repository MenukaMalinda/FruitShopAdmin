package com.ciberprotech.finalprojectadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ciberprotech.finalprojectadmin.adapter.ProductAdapter;
import com.ciberprotech.finalprojectadmin.listner.ProductSelectListener;
import com.ciberprotech.finalprojectadmin.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageProductActivity extends AppCompatActivity implements ProductSelectListener {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ManageProductActivity.this, "logout Success", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                startActivity(new Intent(ManageProductActivity.this, LoginActivity.class));
            }
        });

        //View Product
        products = new ArrayList<>();

        RecyclerView productView = findViewById(R.id.loadProducts);

        ProductAdapter productAdapter = new ProductAdapter(products, ManageProductActivity.this,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        productView.setLayoutManager(linearLayoutManager);

        productView.setAdapter(productAdapter);

        firestore.collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange change: value.getDocumentChanges()){
                    Product product =change.getDocument().toObject(Product.class);
                    switch (change.getType()){
                        case ADDED:
                            products.add(product);
                        case MODIFIED:
                            Product old = products.stream().filter(i->i.getTitle().equals(product.getTitle())).findFirst().orElse(null);
                            if(old!=null){
                                old.setTitle(product.getTitle());
                                old.setCategoryName(product.getCategoryName());
                                old.setPrice(product.getPrice());
                                old.setQty(product.getQty());
                                old.setImagePath1(product.getImagePath1());
                            }
                            break;
                        case REMOVED:
                            products.remove(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.actionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageProductActivity.this,AddProductActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void changeStatus(Product product) {
        String status = product.getStatus().toString();
        Map<String, Object> updates = new HashMap<>();
        if(status.equals("true")){
            updates.put("status","false");
//            Toast.makeText(this, "deactive", Toast.LENGTH_SHORT).show();
        }else{
            updates.put("status","true");
//            Toast.makeText(this, "actice", Toast.LENGTH_SHORT).show();
        }

        firestore.collection("Items")
                .whereEqualTo("id", product.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<String> documentIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                firestore.collection("Items").document(documentId).update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                if(status.equals("true")){
                                                    Toast.makeText(ManageProductActivity.this, "Item deactive", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(ManageProductActivity.this, "Item active", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ManageProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    @Override
    public void editProduct(Product product) {
        Intent intent = new Intent(ManageProductActivity.this, EditProductActivity.class);
        intent.putExtra("itemId", product.getId());
        startActivity(intent);
    }
}