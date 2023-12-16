package com.ciberprotech.finalprojectadmin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ciberprotech.finalprojectadmin.R;
import com.ciberprotech.finalprojectadmin.listner.OrderSelectListner;
import com.ciberprotech.finalprojectadmin.model.Order;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{
    private ArrayList<Order> orders;
    private Context context;
    private OrderSelectListner orderSelectListner;

    public OrderAdapter(ArrayList<Order> orders, Context context, OrderSelectListner orderSelectListner) {
        this.orders = orders;
        this.context = context;
        this.orderSelectListner = orderSelectListner;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_orders_layout, parent, false);
        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Order order = orders.get(position);

        holder.orderIdTxt.setText("Id : "+order.getId());
        holder.orderDateTxt.setText("Date : "+order.getDate_time());
        holder.orderPriceTxt.setText("Rs. "+order.getTotal()+".00");
        holder.orderEmailTxt.setText("Email : "+order.getEmail());
        holder.orderStatusTxt.setText(String.valueOf((order.getDeliver_status())).equals("1") ? "Delivered":"Pending");

        holder.orderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderSelectListner.selectOrder(orders.get(position));
            }
        });

        if(String.valueOf(order.getDeliver_status()).equals("1")){
            holder.deliver.setVisibility(View.GONE);
        }

        holder.deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderSelectListner.setDelivered(orders.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTxt, orderDateTxt,orderPriceTxt,orderStatusTxt,orderEmailTxt;
        View orderCard;
        Button deliver;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderid);
            orderDateTxt = itemView.findViewById(R.id.orderdate);
            orderPriceTxt = itemView.findViewById(R.id.orderprice);
            orderStatusTxt = itemView.findViewById(R.id.orderstatus);
            orderEmailTxt = itemView.findViewById(R.id.orderemail);
            orderCard = itemView.findViewById(R.id.orderCard);
            deliver = itemView.findViewById(R.id.deliverbtn);
        }
    }
}
