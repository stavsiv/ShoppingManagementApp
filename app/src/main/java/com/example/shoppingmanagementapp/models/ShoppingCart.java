package com.example.shoppingmanagementapp.models;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private String userId;
    private final Map<String, CartItem> cartItems;
    private double totalPrice;

    public ShoppingCart() {
        cartItems = new HashMap<>();
        totalPrice = 0.0;
    }

    public void addItem(Product product, int quantity) {
        String productId = product.getProductID();
        if (cartItems.containsKey(productId)) {
            CartItem existingItem = cartItems.get(productId);
            assert existingItem != null;
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            cartItems.put(productId, new CartItem(productId, product.getProductName(), product.getProductPrice(), quantity, product.getProductImage()));
        }
        calculateTotal();
    }

    public void addItem(CartItem cartItem) {
        String productId = cartItem.getProductId();
        if (cartItems.containsKey(productId)) {
            CartItem existingItem = cartItems.get(productId);
            assert existingItem != null;
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
            cartItems.put(productId, existingItem);
        } else {
            cartItems.put(productId, cartItem);
        }
        calculateTotal();
    }

    public void removeItem(String productId) {
        cartItems.remove(productId);
        calculateTotal();
    }

    private void calculateTotal() {
        totalPrice = cartItems.values().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    public Map<String, CartItem> getItems() { return cartItems; }
    public double getTotalPrice() { return totalPrice; }
}


