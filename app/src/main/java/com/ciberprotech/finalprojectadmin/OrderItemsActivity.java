package com.ciberprotech.finalprojectadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ciberprotech.finalprojectadmin.adapter.OrderItemsAdapter;
import com.ciberprotech.finalprojectadmin.model.Order;
import com.ciberprotech.finalprojectadmin.model.Product;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderItemsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FirebaseFirestore firestore;
    private ArrayList<Product> items1;
    private GoogleMap map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker_current, marker_pin;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);

        firestore = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String orderid = getIntent().getExtras().getString("orderId");

        TextView oemail = findViewById(R.id.oemail);
        TextView omobile = findViewById(R.id.omobile);
        TextView oline1 = findViewById(R.id.oaddress);
        TextView ototal = findViewById(R.id.ototal);

        //Load Items
        items1 = new ArrayList<>();
        RecyclerView categoryView = findViewById(R.id.loadoProducts);
        OrderItemsAdapter ordersAdapter = new OrderItemsAdapter(items1, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        categoryView.setLayoutManager(linearLayoutManager);
        categoryView.setAdapter(ordersAdapter);

        firestore.collection("orders")
//                .whereEqualTo("id",orderid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                Order order = snapshot1.toObject(Order.class);

                                if (order.getId().equals(orderid)) {
                                    String orderId = snapshot1.getId();

                                    oemail.setText("Email : " + order.getEmail());
                                    omobile.setText("Mobile : " + order.getMobile());
                                    oline1.setText("Address : " + order.getAddress());
                                    ototal.setText("Total : Rs." + order.getTotal() + ".00");

                                    latitude = Double.valueOf(order.getLatitude());
                                    longitude = Double.parseDouble(order.getLongitude());

                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                    mapFragment.getMapAsync(OrderItemsActivity.this);

                                    firestore.collection("orders/" + orderId + "/Items").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot snapshot2 : task.getResult()) {
                                                            Product item = snapshot2.toObject(Product.class);
                                                            items1.add(item);
                                                        }
                                                    }
                                                    ordersAdapter.notifyDataSetChanged();
                                                }
                                            });
                                    break;
                                }

                            }

                        }
                    }
                });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create a URI for the location
                String geoUri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Location+Marker)";

                // Create an Intent with ACTION_VIEW and the location URI
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");

                // Verify if there's an app to handle this intent
                PackageManager packageManager = getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    // Open the location in Google Maps
                    startActivity(intent);
                } else {
                    // Handle if there's no app to handle this intent
                    Toast.makeText(getApplicationContext(), "please Install google map", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @SuppressLint("MissingPermission")
    private void setDeliverLocation() {
        if (checkPermissions()) {
            LatLng latLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            MarkerOptions options = new MarkerOptions().title("Deliver Location").position(latLng);
            marker_current = map.addMarker(options);
            moveCamera(latLng);
        }
    }


    public void moveCamera(LatLng latLng) {
        CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(15f).build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        if (checkPermissions()) {
            map.setMyLocationEnabled(true);
            setDeliverLocation();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    private boolean checkPermissions() {
        boolean permission = false;
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permission = true;
        }

        return permission;
    }

}