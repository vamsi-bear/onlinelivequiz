package com.example.onlinelivequiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinelivequiz.R;
import com.example.onlinelivequiz.firebase.FirebaseHelper;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    private Button loginButton;
    private TextView registerText;

    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);

        // Firebase
        firebaseHelper = new FirebaseHelper();

        // Check if user is already logged in
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();
        if (currentUser != null) {
            openMainActivity();
            return;
        }

        // Login button
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> loginUser());
        }

        // Register link
        if (registerText != null) {
            registerText.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter your email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Enter your password");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        firebaseHelper.loginUser(email, password, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                openMainActivity();
            } else {
                loginButton.setEnabled(true);
                loginButton.setText("LOGIN");
                String error = task.getException() != null ? task.getException().getMessage() : "Login failed";
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
