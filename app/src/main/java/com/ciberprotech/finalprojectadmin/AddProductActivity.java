package com.ciberprotech.finalprojectadmin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.ciberprotech.finalprojectadmin.model.Category;
import com.ciberprotech.finalprojectadmin.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = AddProductActivity.class.getName();
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private Uri imagePath1;
    private Uri imagePath2;
    private Uri imagePath3;
    private String categorySelected;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

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
                Toast.makeText(AddProductActivity.this, "logout Success", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                startActivity(new Intent(AddProductActivity.this, LoginActivity.class));
            }
        });

        arrayList = new ArrayList<String>();
        Spinner spinner = findViewById(R.id.spinner);

        firestore.collection("Categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            arrayList.add("Select Category");
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Category item = snapshot.toObject(Category.class);
                                arrayList.add(item.getCategoryName());
                            }

                            // Move the ArrayAdapter and setAdapter inside onComplete
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_spinner_item, arrayList);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(spinnerArrayAdapter);
                        } else {
                            // Handle errors here
                        }
                    }
                });

        spinner.setOnItemSelectedListener(this);

        //Add Product
        imageButton1 = findViewById(R.id.imageButton1);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher1.launch(Intent.createChooser(intent,"Select Image"));
            }
        });

        imageButton2 = findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher2.launch(Intent.createChooser(intent,"Select Image"));
            }
        });

        imageButton3 = findViewById(R.id.imageButton3);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher3.launch(Intent.createChooser(intent,"Select Image"));
            }
        });

        findViewById(R.id.addProductBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText productNameText = findViewById(R.id.editTextProductName);
                EditText priceText = findViewById(R.id.editTextPrice);
                EditText qtyText = findViewById(R.id.editTextQty);
                EditText descriptionText = findViewById(R.id.editTextDescription);

                String id = String.valueOf(System.currentTimeMillis());
                String productName = productNameText.getText().toString();
                String price = priceText.getText().toString();
                String qty = qtyText.getText().toString();
                String description = descriptionText.getText().toString();

                String image1Id = UUID.randomUUID().toString();
                String image2Id = UUID.randomUUID().toString();
                String image3Id = UUID.randomUUID().toString();

                if(imagePath1 == null){
                    Toast.makeText(AddProductActivity.this,"Please Select image1",Toast.LENGTH_SHORT).show();
                }else if(imagePath2 == null){
                    Toast.makeText(AddProductActivity.this,"Please Select image2",Toast.LENGTH_SHORT).show();
                }else if(imagePath3 == null){
                    Toast.makeText(AddProductActivity.this,"Please Select image3",Toast.LENGTH_SHORT).show();
                }else if(productName.isEmpty()){
                    Toast.makeText(AddProductActivity.this,"Please enter Product Name",Toast.LENGTH_SHORT).show();
                }else if(categorySelected == "Select Category"){
                    Toast.makeText(AddProductActivity.this,"Please Select Category",Toast.LENGTH_SHORT).show();
                } else if (price.isEmpty()) {
                    Toast.makeText(AddProductActivity.this,"Please enter Price",Toast.LENGTH_SHORT).show();
                }else if (qty.isEmpty()) {
                    Toast.makeText(AddProductActivity.this,"Please enter Qty",Toast.LENGTH_SHORT).show();
                }else if (description.isEmpty()) {
                    Toast.makeText(AddProductActivity.this,"Please enter Description",Toast.LENGTH_SHORT).show();
                }else {
                    firestore.collection("Items").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                boolean existsTitle = false;

                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        Product exitsItem = snapshot.toObject(Product.class);
                                        if (productName.equals(exitsItem.getTitle())) {
                                            existsTitle = true;
                                        }
                                    }
                                    if (existsTitle) {
                                        Toast.makeText(AddProductActivity.this, "Product Already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Product item = new Product(id,productName,categorySelected, price, qty,description, image1Id, image2Id, image3Id,"true");

                                        ProgressDialog dialog = new ProgressDialog(AddProductActivity.this);
                                        dialog.setMessage("Adding new product...");
                                        dialog.setCancelable(false);
                                        dialog.show();

                                        firestore.collection("Items").add(item)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        dialog.setMessage("Uploading image one...");

                                                        StorageReference reference = storage.getReference("item_img")
                                                                .child(image1Id);
                                                        reference.putFile(imagePath1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                dialog.setMessage("Uploading image two...");

                                                                StorageReference reference2 = storage.getReference("item_img")
                                                                        .child(image2Id);
                                                                reference2.putFile(imagePath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                        dialog.setMessage("Uploading image three...");

                                                                        StorageReference reference3 = storage.getReference("item_img")
                                                                                .child(image3Id);
                                                                        reference3.putFile(imagePath3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                dialog.dismiss();
                                                                                Toast.makeText(AddProductActivity.this, "Product add success", Toast.LENGTH_SHORT).show();
                                                                                startActivity(new Intent(AddProductActivity.this,DashboardActivity.class));
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
                                                                                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                                dialog.setMessage("Image three uploading " + (int) progress + "%");
                                                                            }
                                                                        });
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
                                                                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                        dialog.setMessage("Image two uploading " + (int) progress + "%");
                                                                    }
                                                                });
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
                                                                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                dialog.setMessage("Image one uploading " + (int) progress + "%");
                                                            }
                                                        });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });

    }

    ActivityResultLauncher<Intent> activityResultLauncher1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath1 = result.getData().getData();
                        Log.i(TAG,"Image Path: "+imagePath1.getPath());

                        Picasso.get()
                                .load(imagePath1)
                                .fit()
                                .centerCrop()
                                .into(imageButton1);
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> activityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath2 = result.getData().getData();
                        Log.i(TAG,"Image Path: "+imagePath2.getPath());
                        Picasso.get()
                                .load(imagePath2)
                                .fit()
                                .centerCrop()
                                .into(imageButton2);
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> activityResultLauncher3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath3 = result.getData().getData();
                        Log.i(TAG,"Image Path: "+imagePath3.getPath());
                        Picasso.get()
                                .load(imagePath3)
                                .fit()
                                .centerCrop()
                                .into(imageButton3);
                    }
                }
            }
    );

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(AddProductActivity.this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        categorySelected = parent.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}