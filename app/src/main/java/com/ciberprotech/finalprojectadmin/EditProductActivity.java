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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ciberprotech.finalprojectadmin.model.Category;
import com.ciberprotech.finalprojectadmin.model.Product;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class EditProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ArrayList<String> arrayList;
    private ArrayList<String> categories;
    private int position = -1;
    private Product product;

    private ImageButton addImageButton1;
    private ImageButton addImageButton2;
    private ImageButton addImageButton3;

    private Uri imagePath1;
    private Uri imagePath2;
    private Uri imagePath3;

    boolean isFinished1 = true;
    boolean isFinished2 = true;
    boolean isFinished3 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

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
                Toast.makeText(EditProductActivity.this, "logout Success", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                startActivity(new Intent(EditProductActivity.this, LoginActivity.class));
            }
        });

        addImageButton1 = findViewById(R.id.editImageBtn1);
        addImageButton2 = findViewById(R.id.editImageBtn2);
        addImageButton3 = findViewById(R.id.editImageBtn3);

        addImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher1.launch(Intent.createChooser(intent, "Select Image"));
            }
        });

        addImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher2.launch(Intent.createChooser(intent, "Select Image"));
            }
        });

        addImageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher3.launch(Intent.createChooser(intent, "Select Image"));
            }
        });


        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Picasso.get()
                .load(R.drawable.image_add_icon)
                .resize(150, 150)
                .into((ImageView) findViewById(R.id.editImageBtn1));

        Picasso.get()
                .load(R.drawable.image_add_icon)
                .resize(150, 150)
                .into((ImageView) findViewById(R.id.editImageBtn2));

        Picasso.get()
                .load(R.drawable.image_add_icon)
                .resize(150, 150)
                .into((ImageView) findViewById(R.id.editImageBtn3));

        if (!getIntent().getExtras().getString("itemId").isEmpty()) {

            String id = getIntent().getExtras().getString("itemId");
            arrayList = new ArrayList<String>();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    categories = new ArrayList<String>();

                    //
                    firestore.collection("Items").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        product = snapshot.toObject(Product.class);

                                        if (product.getId().equals(id)) {

                                            TextView title = findViewById(R.id.editProductName);
                                            TextView price = findViewById(R.id.editPrice);
                                            TextView qty = findViewById(R.id.editQty);
                                            TextView desc = findViewById(R.id.editDescription);


                                            storage.getReference("item_img/" + product.getImagePath1())
                                                    .getDownloadUrl()
                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            Picasso.get()
                                                                    .load(uri)
                                                                    .resize(150, 150)
                                                                    .into(addImageButton1);

                                                        }
                                                    });

                                            storage.getReference("item_img/" + product.getImagePath2())
                                                    .getDownloadUrl()
                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            Picasso.get()
                                                                    .load(uri)
                                                                    .resize(150, 150)
                                                                    .into(addImageButton2);

                                                        }
                                                    });

                                            storage.getReference("item_img/" + product.getImagePath3())
                                                    .getDownloadUrl()
                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            Picasso.get()
                                                                    .load(uri)
                                                                    .resize(150, 150)
                                                                    .into(addImageButton3);

                                                        }
                                                    });

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    title.setText(product.getTitle().toString());
                                                    price.setText(product.getPrice().toString());
                                                    qty.setText(product.getQty().toString());
                                                    desc.setText(product.getDescription().toString());
                                                }
                                            });
                                            break;
                                        }

                                    }

                                }
                            });

                    firestore.collection("Categories").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                            Category item = snapshot.toObject(Category.class);
                                            arrayList.add(item.getCategoryName());
                                        }

                                        for (int i = 0; i < arrayList.size(); i++) {
                                            if (arrayList.get(i).equals(product.getCategoryName().toString())) {
                                                position = i;
                                                break;
                                            }
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                setupSpinnerAdapter();
                                            }
                                        });

                                    } else {
                                    }

                                }
                            });

                }
            }).start();

            findViewById(R.id.updateProductBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    EditText titleText = findViewById(R.id.editProductName);
                    Spinner categoryText = findViewById(R.id.editCategory);
                    EditText descText = findViewById(R.id.editDescription);
                    EditText priceText = findViewById(R.id.editPrice);
                    EditText qtyText = findViewById(R.id.editQty);

                    String title = titleText.getText().toString();
                    String desc = descText.getText().toString();
                    String price = priceText.getText().toString();
                    String qty = qtyText.getText().toString();
                    String category = product.getCategoryName().toString();

                    String image1Id = UUID.randomUUID().toString();
                    String image2Id = UUID.randomUUID().toString();
                    String image3Id = UUID.randomUUID().toString();

                    if (title.isEmpty()) {
                        Toast.makeText(EditProductActivity.this, "Please enter item title", Toast.LENGTH_SHORT).show();
                    } else if (desc.isEmpty()) {
                        Toast.makeText(EditProductActivity.this, "Please enter item description", Toast.LENGTH_SHORT).show();
                    } else if (price.isEmpty()) {
                        Toast.makeText(EditProductActivity.this, "Please enter item price", Toast.LENGTH_SHORT).show();
                    } else if (qty.isEmpty()) {
                        Toast.makeText(EditProductActivity.this, "Please enter item qty", Toast.LENGTH_SHORT).show();
                    } else {

                        firestore.collection("products")
                                .whereNotEqualTo("id", id).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    boolean existsTitle = false;

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                            Product exitsItem = snapshot.toObject(Product.class);
                                            if (title.equals(exitsItem.getTitle())) {
                                                existsTitle = true;
                                            }
                                        }
                                        if (existsTitle) {
                                            Toast.makeText(EditProductActivity.this, "Product title Already exists", Toast.LENGTH_SHORT).show();
                                        } else {

                                            String ref = String.valueOf(System.currentTimeMillis());

                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("title", title);
                                            updates.put("price", price);
                                            updates.put("qty", qty);
                                            updates.put("categoryName", category);
                                            updates.put("description", desc);

                                            if (imagePath1 != null) {
                                                updates.put("imagePath1", image1Id);
                                                isFinished1 = false;
                                            }

                                            if (imagePath2 != null) {
                                                updates.put("imagePath2", image2Id);
                                                isFinished2 = false;
                                            }

                                            if (imagePath3 != null) {
                                                updates.put("imagePath3", image3Id);
                                                isFinished3 = false;
                                            }

                                            firestore.collection("Items")
                                                    .whereEqualTo("id", id).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                List<String> documentIds = new ArrayList<>();

                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    String documentId = document.getId();

                                                                    ProgressDialog dialog = new ProgressDialog(EditProductActivity.this);
                                                                    dialog.setMessage("Update Product...");
                                                                    startActivity(new Intent(EditProductActivity.this,ManageProductActivity.class));
                                                                    dialog.setCancelable(false);
                                                                    dialog.show();

                                                                    firestore.collection("Items").document(documentId).update(updates)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    dialog.dismiss();

                                                                                    StorageReference storageRef = storage.getReference();


                                                                                    if (imagePath1 != null) {

                                                                                        ProgressDialog dialog = new ProgressDialog(EditProductActivity.this);
                                                                                        dialog.setMessage("Uploading");
                                                                                        dialog.setCancelable(false);
                                                                                        dialog.show();

                                                                                        StorageReference desertRef = storageRef.child("item_img/" + product.getImagePath1());

                                                                                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception exception) {
                                                                                            }
                                                                                        });

                                                                                        StorageReference reference = storage.getReference("item_img")
                                                                                                .child(image1Id);
                                                                                        reference.putFile(imagePath1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                            @Override
                                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                isFinished1 = true;

                                                                                                if (isFinished1 & isFinished2 & isFinished3) {
                                                                                                    finish();
                                                                                                }
                                                                                                dialog.dismiss();
                                                                                                Toast.makeText(getApplicationContext(), "Updated Product", Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                dialog.dismiss();
                                                                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                            @Override
                                                                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                                                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                                                dialog.setMessage("Image 1 Uploading " + (int) progress + "%");
                                                                                            }
                                                                                        });

                                                                                    } else {
                                                                                        isFinished1 = true;
                                                                                    }

                                                                                    if (imagePath2 != null) {
                                                                                        ProgressDialog dialog = new ProgressDialog(EditProductActivity.this);
                                                                                        dialog.setMessage("Uploading");
                                                                                        dialog.setCancelable(false);
                                                                                        dialog.show();

                                                                                        StorageReference desertRef = storageRef.child("item_img/" + product.getImagePath2());

                                                                                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {

                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                dialog.dismiss();
                                                                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });

                                                                                        StorageReference reference = storage.getReference("item_img")
                                                                                                .child(image2Id);
                                                                                        reference.putFile(imagePath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                            @Override
                                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                isFinished2 = true;
                                                                                                if (isFinished1 & isFinished2 & isFinished3) {
                                                                                                    finish();
                                                                                                }
                                                                                                dialog.dismiss();
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                dialog.dismiss();
                                                                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                            @Override
                                                                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                                                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                                                dialog.setMessage("Image 2 Uploading " + (int) progress + "%");
                                                                                            }
                                                                                        });
                                                                                    } else {
                                                                                        isFinished2 = true;
                                                                                    }

                                                                                    if (imagePath3 != null) {
                                                                                        ProgressDialog dialog = new ProgressDialog(EditProductActivity.this);
                                                                                        dialog.setMessage("Uploading");
                                                                                        dialog.setCancelable(false);
                                                                                        dialog.show();

                                                                                        StorageReference desertRef = storageRef.child("item_img/" + product.getImagePath3());

                                                                                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception exception) {
                                                                                            }
                                                                                        });

                                                                                        StorageReference reference = storage.getReference("item_img")
                                                                                                .child(image3Id);
                                                                                        reference.putFile(imagePath3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                            @Override
                                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                isFinished3 = true;
                                                                                                if (isFinished1 & isFinished2 & isFinished3) {
                                                                                                    finish();
                                                                                                }
                                                                                                dialog.dismiss();
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                dialog.dismiss();
                                                                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                            @Override
                                                                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                                                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                                                                                dialog.setMessage("Image 3 Uploading " + (int) progress + "%");
                                                                                            }
                                                                                        });
                                                                                    } else {
                                                                                        isFinished3 = true;
                                                                                    }

                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    dialog.dismiss();
                                                                                    Toast.makeText(EditProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });

                                                                }

                                                            } else {
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }

                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imagePath1 = result.getData().getData();

                        Picasso.get()
                                .load(imagePath1)
                                .resize(150, 150)
                                .into(addImageButton1);
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

                        Picasso.get()
                                .load(imagePath2)
                                .resize(150, 150)
                                .into(addImageButton2);
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

                        Picasso.get()
                                .load(imagePath3)
                                .resize(150, 150)
                                .into(addImageButton3);
                    }
                }
            }
    );

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        product.setCategoryName(parent.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setupSpinnerAdapter() {
        Spinner spinner = findViewById(R.id.editCategory);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        if (position != -1) {
            spinner.setSelection(position);
        }
        spinner.setOnItemSelectedListener(this);
    }
}