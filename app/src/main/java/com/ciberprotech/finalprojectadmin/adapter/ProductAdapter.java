package com.ciberprotech.finalprojectadmin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ciberprotech.finalprojectadmin.R;
import com.ciberprotech.finalprojectadmin.listner.ProductSelectListener;
import com.ciberprotech.finalprojectadmin.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter  extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<Product> products;
    private Context context;
    private FirebaseStorage storage;
    private ProductSelectListener selectListener;

    public ProductAdapter(ArrayList<Product> products, Context context, ProductSelectListener selectListener) {
        this.products = products;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = products.get(position);
        holder.productName.setText(product.getTitle());
        holder.category.setText(product.getCategoryName());
        holder.price.setText("Rs. "+product.getPrice());
        holder.qty.setText(product.getQty());

        if (product.getStatus().equals("true")) {
            holder.status.setChecked(true);
        }else {
            holder.status.setChecked(false);
        }

        holder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.changeStatus(products.get(position));
            }
        });

        holder.editProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.editProduct(products.get(position));
            }
        });

        storage.getReference("item_img/"+product.getImagePath1())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .fit()
                                .centerCrop()
                                .into(holder.image);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView category;
        TextView price;
        TextView qty;
        ImageView image;
        Switch status;
        ImageView editProductBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textViewProductName);
            category = itemView.findViewById(R.id.textViewCategory);
            price = itemView.findViewById(R.id.textViewPrice);
            qty = itemView.findViewById(R.id.textViewQty);
            image = itemView.findViewById(R.id.productImage);
            status = itemView.findViewById(R.id.pswitch);
            editProductBtn = itemView.findViewById(R.id.editProductBtn);

        }
    }
}
