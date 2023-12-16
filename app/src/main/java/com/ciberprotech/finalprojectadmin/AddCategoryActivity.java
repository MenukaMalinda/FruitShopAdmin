package com.ciberprotech.finalprojectadmin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ciberprotech.finalprojectadmin.adapter.CategoryAdapter;
import com.ciberprotech.finalprojectadmin.listner.CategorySelectListener;
import com.ciberprotech.finalprojectadmin.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class AddCategoryActivity extends AppCompatActivity implements CategorySelectListener {

    public static final String TAG = AddProductActivity.class.getName();
    private ImageButton imageButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private ArrayList<Category> categories;
    private Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

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
                Toast.makeText(AddCategoryActivity.this, "logout Success", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                startActivity(new Intent(AddCategoryActivity.this, LoginActivity.class));
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {

                //View Category
                categories = new ArrayList<>();

                RecyclerView categoryView = findViewById(R.id.loadCategoties);

                CategoryAdapter categoryAdapter = new CategoryAdapter(categories, AddCategoryActivity.this,AddCategoryActivity.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                categoryView.setLayoutManager(linearLayoutManager);

                categoryView.setAdapter(categoryAdapter);

                firestore.collection("Categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentChange change: value.getDocumentChanges()){
                            Category category =change.getDocument().toObject(Category.class);
                            switch (change.getType()){
                                case ADDED:
                                    categories.add(category);
                                case MODIFIED:

                                    Category old = categories.stream().filter(i->i.getCategoryName().equals(category.getCategoryName())).findFirst().orElse(null);
                                    if(old!=null){
                                        old.setCategoryName(category.getCategoryName());
                                        old.setCategoryImage(category.getCategoryImage());
                                    }
                                    break;
                                case REMOVED:
                                    categories.remove(category);
                            }
                        }
                        categoryAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

        //Add Category
        imageButton = findViewById(R.id.addImageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(Intent.createChooser(intent,"Select Image"));
            }
        });

        findViewById(R.id.add_category_btn).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   EditText categoryNameText = findViewById(R.id.editText_category_name);

                   String imageId = UUID.randomUUID().toString();

                   String categoryName = categoryNameText.getText().toString();

                   if (imagePath == null){
                       Toast.makeText(AddCategoryActivity.this,"Please Select Image",Toast.LENGTH_SHORT).show();
                   }else if(categoryName.isEmpty()){
                       Toast.makeText(AddCategoryActivity.this,"Please enter Category Name",Toast.LENGTH_SHORT).show();
                   } else {
                       firestore.collection("Categories").get()
                               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                   boolean existsCat = false;
                                         @Override
                                         public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                             for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                 Category existscategory = snapshot.toObject(Category.class);

                                                 if (categoryName.equals(existscategory.getCategoryName())) {
                                                     existsCat = true;
                                                 }
                                             }
                                             if (existsCat) {
                                                 Toast.makeText(getApplicationContext(), "Category already exists", Toast.LENGTH_SHORT).show();
                                             } else {
                                                 Category category = new Category(categoryName,imageId);

                                                 ProgressDialog dialog = new ProgressDialog(AddCategoryActivity.this);
                                                 dialog.setMessage("Adding new category...");
                                                 dialog.setCancelable(false);
                                                 dialog.show();

                                                 firestore.collection("Categories").add(category)
                                                         .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                             @Override
                                                             public void onSuccess(DocumentReference documentReference) {

                                                                 if(imagePath!=null){
                                                                     dialog.setMessage("Uploading image...");

                                                                     StorageReference reference= storage.getReference("CategoryImage")
                                                                             .child(imageId);
                                                                     reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                         @Override
                                                                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                                                                             Picasso.get()
//                                                                                     .load(R.drawable.image_add_icon)
//                                                                                     .fit()
//                                                                                     .centerCrop()
//                                                                                     .into(imageButton);
//                                                                             dialog.dismiss();
//                                                                             categoryNameText.setText("");

                                                                             startActivity(new Intent(AddCategoryActivity.this,DashboardActivity.class));


                                                                         }
                                                                     }).addOnFailureListener(new OnFailureListener() {
                                                                         @Override
                                                                         public void onFailure(@NonNull Exception e) {
                                                                             dialog.dismiss();
                                                                             Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                         }
                                                                     }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                         @Override
                                                                         public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                             double progress = (100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                                                                             dialog.setMessage("Uploading "+(int)progress+"%");
                                                                         }
                                                                     });
                                                                 }
                                                             }
                                                         }).addOnFailureListener(new OnFailureListener() {
                                                             @Override
                                                             public void onFailure(@NonNull Exception e) {
                                                                 dialog.dismiss();
                                                                 Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                             }
                                                         });
                                             }}

                                     });
                        }
               }
        });

    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath = result.getData().getData();
                        Log.i(TAG,"Image Path: "+imagePath.getPath());
                        Picasso.get()
                                .load(imagePath)
                                .fit()
                                .centerCrop()
                                .into(imageButton);
                    }
                }
            }
    );

    @Override
    public void deleteCategory(Category category) {

        firestore.collection("Categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Category item = snapshot.toObject(Category.class);

                            if (item.getCategoryName() == category.getCategoryName()) {

                                StorageReference storageRef = storage.getReference();
                                StorageReference desertRef = storageRef.child("CategoryImage/"+category.getCategoryImage());

                                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        firestore.document("Categories/"+snapshot.getId()).delete();
                                        Toast.makeText(getApplicationContext(), category.getCategoryName()+" category has been deleted", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getApplicationContext(), "Please Try again later.", Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                            }

                        }
                    }
                });

    }

}