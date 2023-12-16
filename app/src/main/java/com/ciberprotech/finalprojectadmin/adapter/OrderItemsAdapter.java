package com.ciberprotech.finalprojectadmin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ciberprotech.finalprojectadmin.R;
import com.ciberprotech.finalprojectadmin.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {

    private ArrayList<Product> items;
    private Context context;
    private FirebaseStorage storage;

    public OrderItemsAdapter(ArrayList<Product> items, Context context) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public OrderItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_item_view_layout, parent, false);
        return new OrderItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product item = items.get(position);

        holder.itemTitleText.setText(item.getTitle());
        holder.itemCategoryText.setText(item.getCategoryName());
        holder.itemPriceText.setText("Rs." + item.getPrice() + ".00");
        holder.itemQtyText.setText("Quantity : " + item.getQty());

        storage.getReference("item_img/" + item.getImagePath1())
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
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitleText,itemCategoryText, itemPriceText, itemQtyText;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitleText = itemView.findViewById(R.id.orderProductName);
            itemCategoryText = itemView.findViewById(R.id.orderCategory);
            itemPriceText = itemView.findViewById(R.id.orderPrice);
            itemQtyText = itemView.findViewById(R.id.orderQty);
            image = itemView.findViewById(R.id.orderImage);
        }
    }

}
