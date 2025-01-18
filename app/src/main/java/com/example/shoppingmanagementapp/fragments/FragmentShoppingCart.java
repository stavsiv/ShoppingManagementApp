package com.example.shoppingmanagementapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingmanagementapp.R;
import com.example.shoppingmanagementapp.adapters.ShoppingCartAdapter;
import com.example.shoppingmanagementapp.firebase.FirebaseManager;
import com.example.shoppingmanagementapp.models.CartItem;
import com.example.shoppingmanagementapp.models.ShoppingCart;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class FragmentShoppingCart extends Fragment implements ShoppingCartAdapter.OnCartItemClickListener{

    private RecyclerView recyclerView;
    private ShoppingCartAdapter adapter;
    private FirebaseManager firebaseManager;
    private ShoppingCart shoppingCart;
    private TextView totalPriceTextView;
    private Button checkoutButton;

    private View emptyCartView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        initViews(view);
        setupRecyclerView();
        loadCartItems();
        return view;
    }

    private void initViews(View view) {
        ImageView backToProductsButton = view.findViewById(R.id.back_to_products_button);
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        totalPriceTextView = view.findViewById(R.id.total_price);
        checkoutButton = view.findViewById(R.id.checkout_button);
        emptyCartView = view.findViewById(R.id.empty_cart_view);

        firebaseManager = new FirebaseManager();
        shoppingCart = new ShoppingCart();

        checkoutButton.setOnClickListener(v -> onCheckoutClicked());
        backToProductsButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_fragmentShoppingCart_to_fragmentProducts));

    }

    private void setupRecyclerView() {
        adapter = new ShoppingCartAdapter(new ArrayList<>(shoppingCart.getItems().values()), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadCartItems() {
        firebaseManager.addCartListener(new FirebaseManager.OnCartUpdateListener() {
            @Override
            public void onUpdate(List<CartItem> items) {
                updateUI(items);
            }

            @Override
            public void onError(Exception e) {
                showError("error occurred while loading shopping cart");
            }
        });
    }

    @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
    private void updateUI(List<CartItem> items) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            shoppingCart = new ShoppingCart();
            for (CartItem item : items) {
                shoppingCart.addItem(item);
            }

            adapter.updateItems(items);

            updateTotalPrice();


            boolean isEmpty = shoppingCart.getItems().isEmpty();
            emptyCartView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            checkoutButton.setEnabled(!isEmpty);
        });
    }
    @SuppressLint("DefaultLocale")
    private void updateTotalPrice() {
        double totalPrice = calculateTotalPrice();
        totalPriceTextView.setText(String.format("total : ₪%.2f ", totalPrice));
    }

    private double calculateTotalPrice() {
        if (adapter == null) return 0.0;

        double total = 0.0;
        List<CartItem> items = adapter.getItems();
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
    @Override
    public void onRemoveItem(CartItem item) {
        firebaseManager.getProductStock(item.getProductId())
                .addOnSuccessListener(currentStock -> {
                    int newStock = currentStock + item.getQuantity();

                    firebaseManager.updateProductStock(item.getProductId(), newStock)
                            .addOnSuccessListener(aVoid -> {
                                firebaseManager.removeFromCart(item.getProductId())
                                        .addOnSuccessListener(unused ->
                                                Snackbar.make(requireView(), "item removed from cart", Snackbar.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> {
                                            firebaseManager.updateProductStock(item.getProductId(), currentStock);
                                            showError("error occurred while removing item");
                                        });
                            })
                            .addOnFailureListener(e ->
                                    showError("error occurred while updating stock"));
                })
                .addOnFailureListener(e ->
                        showError("error occurred while getting product stock"));
    }

    private void onCheckoutClicked() {
        double totalPrice = calculateTotalPrice();
        if (totalPrice > 0) {
            showMessage("Proceed to payment");
        } else {
            showError("empty cart");
        }
    }

    private void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (firebaseManager != null) {
            firebaseManager.cleanup();
        }
    }
}