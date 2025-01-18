package com.example.shoppingmanagementapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shoppingmanagementapp.R;
import com.example.shoppingmanagementapp.activities.MainActivity;

public class FragmentLogin extends Fragment {

    public FragmentLogin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText emailEditText = view.findViewById(R.id.emailEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        Button loginButton = view.findViewById(R.id.buttonLoginToFriends);
        Button registerButton = view.findViewById(R.id.buttonLoginToRegister);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.login(email, password, view);
            } else {
                Toast.makeText(getContext(), "Error: Activity not found", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_fragmentLogin_to_fragmentRegister));

        return view;
    }
}

