package com.example.shoppingmanagementapp.adapters;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingmanagementapp.R;
import com.example.shoppingmanagementapp.models.CartItem;

public class ShoppingCartViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameText;
    private final TextView priceText;
    private final TextView quantityText;
    private final TextView subtotalText;
    private final ImageButton removeButton;
    private final ImageView productImage;
    private final ShoppingCartAdapter.OnCartItemClickListener listener;

    public ShoppingCartViewHolder(@NonNull View itemView, ShoppingCartAdapter.OnCartItemClickListener listener) {
        super(itemView);
        this.listener = listener;
        nameText = itemView.findViewById(R.id.product_name);
        priceText = itemView.findViewById(R.id.product_price);
        quantityText = itemView.findViewById(R.id.quantity_text);
        subtotalText = itemView.findViewById(R.id.subtotal);
        removeButton = itemView.findViewById(R.id.remove_button);
        productImage = itemView.findViewById(R.id.product_image);
    }

    @SuppressLint("DefaultLocale")
    public void bind(CartItem item) {
        nameText.setText(item.getProductName());
        priceText.setText(String.format("₪%.2f", item.getPrice()));
        quantityText.setText(String.format("quantity : %d", item.getQuantity()));
        subtotalText.setText(String.format("₪%.2f", item.getSubtotal()));

        if (productImage != null) {
            productImage.setImageResource(item.getProductImage());
        }

        removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(item);
            }
        });
    }
}