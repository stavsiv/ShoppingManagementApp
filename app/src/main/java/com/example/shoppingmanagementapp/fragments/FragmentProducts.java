package com.example.shoppingmanagementapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.navigation.Navigation;

import com.example.shoppingmanagementapp.firebase.FirebaseManager;

import com.example.shoppingmanagementapp.R;
import com.example.shoppingmanagementapp.adapters.ProductAdapter;
import com.example.shoppingmanagementapp.models.CartItem;
import com.example.shoppingmanagementapp.models.Product;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FragmentProducts extends Fragment implements ProductAdapter.OnProductClickListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private FirebaseManager firebaseManager;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_products_list, container, false);
        initializeViews();
        setupRecyclerView();
        loadProducts();
        return rootView;
    }

    @SuppressLint("SetTextI18n")
    private void initializeViews() {
        recyclerView = rootView.findViewById(R.id.products_recycler_view);
        firebaseManager = new FirebaseManager();

        TextView usernameTxt = rootView.findViewById(R.id.username_text);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String username = email != null ? email.split("@")[0] : "";
            usernameTxt.setText("Hello, " + username);
        }

        ImageButton cartButton = rootView.findViewById(R.id.cart_button);
        cartButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_fragmentProducts_to_fragmentShoppingCart));
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        firebaseManager.addProductsListener(new FirebaseManager.OnProductsUpdateListener() {
            @Override
            public void onUpdate(List<Product> products) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    adapter.updateProducts(products);
                });
            }

            @Override
            public void onError(Exception e) {
                showError("Error loading products", e);
            }
        });
    }

    @Override
    public void onAddToCartClick(Product product, int quantity) {
        if (getContext() == null || firebaseManager == null) return;

        if (quantity <= 0) {
            showError("Invalid quantity", new Exception("Quantity must be greater than 0"));
            return;
        }

        if (quantity > product.getProductStock()) {
            showError("Not enough stock available", new Exception("Requested quantity exceeds available stock"));
            return;
        }

        int newStock = product.getProductStock() - quantity;
        firebaseManager.updateProductStock(product.getProductID(), newStock)
                .addOnSuccessListener(aVoid -> {
                    firebaseManager.addToCart(product, quantity)
                            .addOnSuccessListener(unused -> {
                                @SuppressLint("DefaultLocale") String message = String.format("Added to cart: %s, quantity: %d",
                                        product.getProductName(), quantity);
                                showSuccess(message);
                            })
                            .addOnFailureListener(e -> {
                                firebaseManager.updateProductStock(product.getProductID(), product.getProductStock());
                                showError("Error adding to cart: " + e.getMessage(), e);
                            });
                })
                .addOnFailureListener(e ->
                        showError("Error updating stock: " + e.getMessage(), e));
    }

    private void showSuccess(String message) {
        if (getContext() != null && rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
                    .setAction("Close", v -> {})
                    .show();
        }
    }

    private void showError(String message, Exception e) {
        if (getContext() != null && rootView != null) {
            e.printStackTrace();
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                    .setAction("Try Again", v -> loadProducts())
                    .show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseManager.addCartListener(new FirebaseManager.OnCartUpdateListener() {
            @Override
            public void onUpdate(List<CartItem> cartItems) {
                updateCartUI(cartItems);
            }

            @Override
            public void onError(Exception e) {
                showError("Error updating shopping cart", e);
            }
        });
    }

    private void updateCartUI(List<CartItem> cartItems) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            TextView cartBadge = rootView.findViewById(R.id.cart_badge);
            if (cartBadge != null) {
                int itemCount = cartItems.size();
                cartBadge.setText(String.valueOf(itemCount));
                cartBadge.setVisibility(itemCount > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (firebaseManager != null) {
            firebaseManager.cleanup();
        }
    }
}