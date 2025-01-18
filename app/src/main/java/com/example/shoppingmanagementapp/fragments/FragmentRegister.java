package com.example.shoppingmanagementapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shoppingmanagementapp.R;
import com.example.shoppingmanagementapp.activities.MainActivity;


public class FragmentRegister extends Fragment {

    public FragmentRegister() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText emailEditText = view.findViewById(R.id.editTextEmail);
        EditText password1EditText = view.findViewById(R.id.editTextPassword1);
        EditText password2EditText = view.findViewById(R.id.editTextPassword2);
        EditText phoneEditText = view.findViewById(R.id.editTextPhoneNumber);
        Button registerButton = view.findViewById(R.id.buttonRegisterToLogin);
        ImageView infoIcon = view.findViewById(R.id.infoIconPassword);

        infoIcon.setOnClickListener(v ->
                Toast.makeText(getContext(), "Password must contain:\n- At least 8 characters\n- One English letter\n- One special character", Toast.LENGTH_LONG).show()
        );

        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password1 = password1EditText.getText().toString().trim();
            String password2 = password2EditText.getText().toString().trim();
            String phoneNumber = phoneEditText.getText().toString().trim();

            if (email.isEmpty() || password1.isEmpty() || password2.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password1)) {
                Toast.makeText(getContext(), "Password must contain:\n- At least 8 characters\n- One English letter\n- One special character", Toast.LENGTH_SHORT).show();
                return;
            }
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.register(email, password1, password2, phoneNumber, view);
            } else {
                Toast.makeText(getContext(), "Error: Activity not found", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[a-zA-Z].*") &&
                password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
}