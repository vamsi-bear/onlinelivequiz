package com.example.onlinelivequiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinelivequiz.R;
import com.example.onlinelivequiz.firebase.FirebaseHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameText;
    private TextView emailText;
    private TextView userIdText;

    private Button logoutButton;
    private Button homeButton;

    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        userIdText = findViewById(R.id.userIdText);

        logoutButton = findViewById(R.id.logoutButton);
        homeButton = findViewById(R.id.homeButton);

        firebaseHelper = new FirebaseHelper();

        loadProfile();

        logoutButton.setOnClickListener(v -> logout());

        homeButton.setOnClickListener(v -> finish());
    }

    private void loadProfile() {
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();

        if (currentUser == null) {
            goToLogin();
            return;
        }

        String userId = currentUser.getUid();
        emailText.setText(currentUser.getEmail());
        userIdText.setText("User ID: " + userId);

        firebaseHelper.getUserProfile(userId, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String name = document.getString("name");
                    if (name != null && !name.trim().isEmpty()) {
                        nameText.setText(name);
                    } else {
                        nameText.setText("User");
                    }
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Unable to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        firebaseHelper.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}