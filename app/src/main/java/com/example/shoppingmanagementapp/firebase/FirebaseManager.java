package com.example.shoppingmanagementapp.firebase;

import com.example.shoppingmanagementapp.models.Product;
import com.example.shoppingmanagementapp.models.CartItem;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class FirebaseManager {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private static final String USERS_COLLECTION = "users";
    private static final String PRODUCTS_COLLECTION = "products";
    private static final String CART_COLLECTION = "cart";

    private final Set<ListenerRegistration> listeners = new HashSet<>();
    private ListenerRegistration cartListener;

    public FirebaseManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public Task<Void> addToCart(Product product, int quantity) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Add to cart failed: User not logged in");
            return Tasks.forException(new Exception("User not logged in"));
        }

        String userId = currentUser.getUid();
        String productId = product.getProductID();
        DocumentReference cartItemRef = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CART_COLLECTION)
                .document(productId);

        return cartItemRef.get().continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        CartItem existingItem = task.getResult().toObject(CartItem.class);
                        if (existingItem != null) {
                            int newQuantity = existingItem.getQuantity() + quantity;
                            existingItem.setQuantity(newQuantity);
                            return cartItemRef.set(existingItem);
                        }
                    }

                    CartItem newCartItem = new CartItem(productId, product.getProductName(),
                            product.getProductPrice(), quantity, product.getProductImage());

                    return cartItemRef.set(newCartItem);
                })
                .addOnSuccessListener(aVoid -> System.out.println("Successfully added/updated item in cart: " + product.getProductName()))
                .addOnFailureListener(e -> {
                    System.out.println("Failed to add/update item in cart: " + e.getMessage());
                    e.printStackTrace();
                });
    }
    public void addCartListener(OnCartUpdateListener listener) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Add cart listener failed: User not logged in");
            return;
        }

        String userId = currentUser.getUid();
        System.out.println("Setting up cart listener for user: " + userId);

        cartListener = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CART_COLLECTION)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        System.out.println("Cart listener error: " + e.getMessage());
                        listener.onError(e);
                        return;
                    }
                    if (snapshots == null) {
                        System.out.println("No snapshots received");
                        return;
                    }

                    List<CartItem> items = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        CartItem item = doc.toObject(CartItem.class);
                        if (item != null) {
                            System.out.println("Found cart item: " + item.getProductName());
                            items.add(item);
                        }
                    }
                    System.out.println("Total cart items: " + items.size());
                    listener.onUpdate(items);
                });

        listeners.add(cartListener);
    }

    public Task<Void> removeFromCart(String productId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Remove from cart failed: User not logged in");
            return Tasks.forException(new Exception("User not logged in"));
        }

        String userId = currentUser.getUid();
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CART_COLLECTION)
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid ->
                        System.out.println("Successfully removed item from cart: " + productId))
                .addOnFailureListener(e -> {
                    System.out.println("Failed to remove item from cart: " + e.getMessage());
                    e.printStackTrace();
                });
    }
    public Task<Integer> getProductStock(String productId) {
        return db.collection(PRODUCTS_COLLECTION)
                .document(productId)
                .get()
                .continueWith(task -> {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        return document.getLong("productStock").intValue();
                    }
                    return 0;
                });
    }

    public void cleanup() {
        if (cartListener != null) {
            cartListener.remove();
        }
        for (ListenerRegistration listener : listeners) {
            listener.remove();
        }
        listeners.clear();
        System.out.println("Cleaned up all listeners");
    }

    public Task<Void> addProduct(Product product) {
        return db.collection(PRODUCTS_COLLECTION)
                .document(product.getProductID())
                .set(product)
                .addOnSuccessListener(aVoid ->
                        System.out.println("Successfully added product: " + product.getProductName()))
                .addOnFailureListener(e ->
                        System.out.println("Failed to add product: " + e.getMessage()));
    }

    public void addProductsListener(OnProductsUpdateListener listener) {
        ListenerRegistration registration = db.collection(PRODUCTS_COLLECTION)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        listener.onError(e);
                        return;
                    }

                    if (snapshots == null) return;

                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            products.add(product);
                        }
                    }
                    listener.onUpdate(products);
                });

        listeners.add(registration);
    }

    public Task<Void> updateProductStock(String productId, int newStock) {
        return db.collection(PRODUCTS_COLLECTION)
                .document(productId)
                .update("productStock", newStock)
                .addOnSuccessListener(aVoid ->
                        System.out.println("Successfully updated stock for product: " + productId))
                .addOnFailureListener(e ->
                        System.out.println("Failed to update stock: " + e.getMessage()));
    }


    public interface OnProductsUpdateListener {
        void onUpdate(List<Product> products);
        void onError(Exception e);
    }

    public interface OnCartUpdateListener {
        void onUpdate(List<CartItem> cartItems);
        void onError(Exception e);
    }
}