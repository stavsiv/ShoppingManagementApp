package com.example.shoppingmanagementapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.example.shoppingmanagementapp.models.ProductsData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.shoppingmanagementapp.R;
import com.example.shoppingmanagementapp.models.User;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //ProductsData.initializeProductsInFirebase();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void login(String email, String password, View view) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Navigation.findNavController(view)
                                .navigate(R.id.action_fragmentLogin_to_fragmentProducts);
                    } else {
                        Toast.makeText(MainActivity.this, "login failed: " +
                                Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void register(String email, String password1, String password2, String phoneNumber, View view) {
        if (!password1.equals(password2)) {
            Toast.makeText(this, "passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                        User newUser = new User(email, "", phoneNumber);
                        newUser.setUserId(userId);

                        db.collection("users")
                                .document(userId)
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(MainActivity.this,
                                            "successful registration", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(view)
                                            .navigate(R.id.action_fragmentRegister_to_fragmentLogin);
                                })
                                .addOnFailureListener(e -> Toast.makeText(MainActivity.this,
                                        "invalid user name: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(MainActivity.this, "registration failed: " +
                                Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public FirebaseFirestore getFirestore() {
        return db;
    }
}