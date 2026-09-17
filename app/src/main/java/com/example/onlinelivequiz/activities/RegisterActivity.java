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

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private Button registerButton;
    private TextView loginText;

    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginText = findViewById(R.id.loginText);

        firebaseHelper = new FirebaseHelper();

        // Register button
        registerButton.setOnClickListener(v -> registerUser());

        // Go to login
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Enter your name");
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email required");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEditText.setError("Password (min 6 chars) required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        registerButton.setEnabled(false);
        registerButton.setText("Creating Account...");

        firebaseHelper.registerUser(email, password, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = task.getResult().getUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    firebaseHelper.createUserProfile(userId, name, email, profileTask -> {
                        if (profileTask.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            openMainActivity();
                        } else {
                            registerButton.setEnabled(true);
                            registerButton.setText("REGISTER");
                            Toast.makeText(RegisterActivity.this, "Profile could not be saved.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                registerButton.setEnabled(true);
                registerButton.setText("REGISTER");
                String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
