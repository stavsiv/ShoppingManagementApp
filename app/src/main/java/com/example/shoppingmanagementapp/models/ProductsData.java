package com.example.shoppingmanagementapp.models;

import com.example.shoppingmanagementapp.R;
import com.example.shoppingmanagementapp.firebase.FirebaseManager;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ProductsData {
    private static final FirebaseManager firebaseManager = new FirebaseManager();
    private static boolean isInitialized = false;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void clearAllProducts() {
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                    isInitialized = false;
                    initializeProductsInFirebase();
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error clearing products: " + e.getMessage());
                });
    }

    public static void initializeProductsInFirebase() {
        if (isInitialized) {
            return;
        }

        String[] productNameArray = {
                "Friends Hoodie",
                "Friends Mug",
                "Friends Tote Bag",
                "Friends Case",
                "Friends Hat",
                "Friends Keychain",
                "Friends Family PJs Set",
                "Friends Lego",
                "Friends Notebook",
                "Friends TShirt"
        };

        double[] productPriceArray = {
                59.9, 19.9, 29.9, 9.9, 19.9,
                14.9, 199.9, 149.9, 19.9, 29.9
        };

        int[] productStockArray = {
                30, 50, 60, 100, 50,
                200, 10, 50, 70, 30
        };

        Integer[] productImageArray = {
                R.drawable.friends_hoodie,
                R.drawable.friends_mug,
                R.drawable.friends_tote_bag,
                R.drawable.friends_case,
                R.drawable.friends_hat,
                R.drawable.friends_keychain,
                R.drawable.friends_family_pjs_set,
                R.drawable.friends_lego,
                R.drawable.friends_notebook,
                R.drawable.friends_tshirt
        };

        String[] id_ = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < productNameArray.length; i++) {
            Product product = new Product(
                    id_[i],
                    productNameArray[i],
                    productPriceArray[i],
                    productStockArray[i],
                    productImageArray[i]
            );
            products.add(product);
        }

        final int[] successCount = {0};
        final int totalProducts = products.size();

        for (Product product : products) {
            firebaseManager.addProduct(product)
                    .addOnSuccessListener(aVoid -> {
                        successCount[0]++;
                        if (successCount[0] == totalProducts) {
                            isInitialized = true;
                            System.out.println("All products initialized successfully");
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("Failed to add product: " + product.getProductName());
                        System.out.println("Error: " + e.getMessage());
                    });
        }
    }
}