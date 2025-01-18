package com.example.shoppingmanagementapp.adapters;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingmanagementapp.R;
import com.example.shoppingmanagementapp.models.Product;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameText;
    private final TextView priceText;
    private final TextView stockText;
    private final NumberPicker quantityPicker;
    private final Button addToCartButton;
    private final ImageView productImage;
    private int lastSelectedQuantity = 1;
    private final ProductAdapter.OnProductClickListener listener;

    public ProductViewHolder(@NonNull View itemView, ProductAdapter.OnProductClickListener listener) {
        super(itemView);
        this.listener = listener;
        nameText = itemView.findViewById(R.id.product_name);
        priceText = itemView.findViewById(R.id.product_price);
        stockText = itemView.findViewById(R.id.product_stock);
        quantityPicker = itemView.findViewById(R.id.quantity_picker);
        addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        productImage = itemView.findViewById(R.id.product_image);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void bind(Product product) {
        nameText.setText(product.getProductName());
        priceText.setText(String.format("₪%.2f", product.getProductPrice()));
        productImage.setImageResource(product.getProductImage());

        boolean hasStock = product.getProductStock() > 0;
        if (hasStock) {
            stockText.setText(String.format("in stock: %d", product.getProductStock()));
            setupQuantityPicker(product);
            setupAddToCartButton(product);
        } else {
            stockText.setText("Out of stock");
            disableControls();
        }
    }

    private void setupQuantityPicker(Product product) {
        quantityPicker.setEnabled(true);
        quantityPicker.setMinValue(1);
        quantityPicker.setMaxValue(product.getProductStock());
        quantityPicker.setValue(lastSelectedQuantity);

        quantityPicker.setOnValueChangedListener((picker, oldVal, newVal) -> lastSelectedQuantity = newVal);
    }

    private void setupAddToCartButton(Product product) {
        addToCartButton.setEnabled(true);
        addToCartButton.setOnClickListener(v -> {
            int quantity = quantityPicker.getValue();
            if (quantity <= product.getProductStock()) {
                listener.onAddToCartClick(product, quantity);
            } else {
                Toast.makeText(itemView.getContext(),
                        "Not enough stock available",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableControls() {
        addToCartButton.setEnabled(false);
        quantityPicker.setEnabled(false);
    }
}