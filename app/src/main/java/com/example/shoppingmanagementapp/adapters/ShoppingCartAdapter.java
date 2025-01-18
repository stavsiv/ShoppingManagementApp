package com.example.shoppingmanagementapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingmanagementapp.R;
import com.example.shoppingmanagementapp.models.CartItem;
import java.util.List;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartViewHolder> {
    private List<CartItem> items;
    private final OnCartItemClickListener listener;

    public interface OnCartItemClickListener {
        void onRemoveItem(CartItem item);
    }

    public ShoppingCartAdapter(List<CartItem> items, OnCartItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShoppingCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_cart_item, parent, false);
        return new ShoppingCartViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingCartViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
    public List<CartItem> getItems() {
        return items;
    }
}