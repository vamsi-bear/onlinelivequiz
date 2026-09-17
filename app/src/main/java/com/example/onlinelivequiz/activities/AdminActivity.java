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

public class AdminActivity extends AppCompatActivity {

    private TextView adminEmailText;

    private Button addQuizButton;
    private Button leaderboardButton;
    private Button logoutButton;
    private Button backButton;

    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views
        adminEmailText = findViewById(R.id.adminEmailText);

        addQuizButton = findViewById(R.id.addQuizButton);
        leaderboardButton = findViewById(R.id.leaderboardButton);

        logoutButton = findViewById(R.id.logoutButton);
        backButton = findViewById(R.id.backButton);

        // Firebase
        firebaseHelper = new FirebaseHelper();

        // Check login
        FirebaseUser currentUser = firebaseHelper.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            goToLogin();
            return;
        }

        // Display admin email
        String email = currentUser.getEmail();

        if (email != null) {
            adminEmailText.setText(email);
        }

        // Add Quiz
        addQuizButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminActivity.this,
                    AddQuizActivity.class
            );

            startActivity(intent);
        });

        // Leaderboard
        leaderboardButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminActivity.this,
                    LeaderboardActivity.class
            );

            // Tell LeaderboardActivity that this is admin
            intent.putExtra("isAdmin", true);

            startActivity(intent);
        });

        // Back
        backButton.setOnClickListener(v -> finish());

        // Logout
        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {

        firebaseHelper.logout();

        Toast.makeText(
                this,
                "Admin logged out",
                Toast.LENGTH_SHORT
        ).show();

        goToLogin();
    }

    private void goToLogin() {

        Intent intent = new Intent(
                AdminActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}