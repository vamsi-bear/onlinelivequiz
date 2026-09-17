package com.example.onlinelivequiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelivequiz.R;
import com.example.onlinelivequiz.adapters.QuizAdapter;
import com.example.onlinelivequiz.firebase.FirebaseHelper;
import com.example.onlinelivequiz.models.Quiz;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextView emptyText;

    private Button profileButton;
    private Button adminButton;
    private Button logoutButton;

    private RecyclerView quizRecyclerView;

    private FirebaseHelper firebaseHelper;
    private QuizAdapter quizAdapter;

    private final List<Quiz> quizList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        emptyText = findViewById(R.id.emptyText);
        profileButton = findViewById(R.id.profileButton);
        adminButton = findViewById(R.id.adminButton);
        logoutButton = findViewById(R.id.logoutButton);
        quizRecyclerView = findViewById(R.id.quizRecyclerView);

        firebaseHelper = new FirebaseHelper();

        // Check logged-in user
        FirebaseUser user = firebaseHelper.getCurrentUser();

        if (user == null) {
            goToLogin();
            return;
        }

        // Welcome message
        String email = user.getEmail();

        if (email != null) {
            welcomeText.setText("Welcome, " + email);
        }

        // Initially hide admin button
        // It will only become visible if the user is an admin.
        adminButton.setVisibility(View.GONE);

        // Check user's role
        checkUserRole();

        // RecyclerView
        quizRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        quizAdapter = new QuizAdapter(this, quizList);
        quizRecyclerView.setAdapter(quizAdapter);

        // Load quizzes
        loadQuizzes();

        // Profile button
        profileButton.setOnClickListener(v -> {
            startActivity(
                    new Intent(
                            MainActivity.this,
                            ProfileActivity.class
                    )
            );
        });

        // Admin button
        adminButton.setOnClickListener(v -> {

            // Check role again before opening AdminActivity
            checkAdminAndOpenDashboard();
        });

        // Logout button
        logoutButton.setOnClickListener(v -> logout());
    }

    // ==========================================
    // CHECK USER ROLE
    // ==========================================

    private void checkUserRole() {

        String userId = firebaseHelper.getCurrentUserId();

        if (userId == null) {
            adminButton.setVisibility(View.GONE);
            return;
        }

        firebaseHelper.getUserProfile(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        // No Firestore profile
                        // Treat as student
                        adminButton.setVisibility(View.GONE);
                        return;
                    }

                    String role = documentSnapshot.getString("role");

                    if ("admin".equals(role)) {

                        // =========================
                        // ADMIN
                        // =========================

                        adminButton.setVisibility(View.VISIBLE);

                    } else {

                        // =========================
                        // STUDENT OR NO ROLE
                        // =========================

                        adminButton.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {

                    // If role cannot be checked,
                    // NEVER show admin access.
                    adminButton.setVisibility(View.GONE);

                    Toast.makeText(
                            MainActivity.this,
                            "Unable to verify user role",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // ==========================================
    // CHECK ADMIN BEFORE OPENING DASHBOARD
    // ==========================================

    private void checkAdminAndOpenDashboard() {

        String userId = firebaseHelper.getCurrentUserId();

        if (userId == null) {
            goToLogin();
            return;
        }

        firebaseHelper.getUserProfile(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                MainActivity.this,
                                "Access denied",
                                Toast.LENGTH_SHORT
                        ).show();

                        adminButton.setVisibility(View.GONE);
                        return;
                    }

                    String role = documentSnapshot.getString("role");

                    if ("admin".equals(role)) {

                        // User is admin
                        Intent intent = new Intent(
                                MainActivity.this,
                                AdminActivity.class
                        );

                        startActivity(intent);

                    } else {

                        // User is not admin
                        Toast.makeText(
                                MainActivity.this,
                                "Admin access denied",
                                Toast.LENGTH_SHORT
                        ).show();

                        adminButton.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            MainActivity.this,
                            "Unable to verify admin access",
                            Toast.LENGTH_SHORT
                    ).show();

                    adminButton.setVisibility(View.GONE);
                });
    }

    // ==========================================
    // LOAD QUIZZES
    // ==========================================

    private void loadQuizzes() {

        emptyText.setText("Loading quizzes...");

        firebaseHelper.getAllQuizzes(task -> {

            if (task.isSuccessful()) {

                quizList.clear();

                if (task.getResult() != null) {

                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : task.getResult()) {

                        Quiz quiz = document.toObject(Quiz.class);

                        if (quiz != null) {

                            if (quiz.getQuizId() == null) {
                                quiz.setQuizId(document.getId());
                            }

                            quizList.add(quiz);
                        }
                    }
                }

                quizAdapter.notifyDataSetChanged();

                if (quizList.isEmpty()) {
                    emptyText.setText("No quizzes available.");
                } else {
                    emptyText.setText("");
                }

            } else {

                emptyText.setText("Error loading quizzes.");
            }
        });
    }

    // ==========================================
    // LOGOUT
    // ==========================================

    private void logout() {

        firebaseHelper.logout();

        goToLogin();
    }

    // ==========================================
    // GO TO LOGIN
    // ==========================================

    private void goToLogin() {

        Intent intent = new Intent(
                MainActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    // ==========================================
    // ON RESUME
    // ==========================================

    @Override
    protected void onResume() {

        super.onResume();

        if (firebaseHelper == null) {
            firebaseHelper = new FirebaseHelper();
        }

        if (firebaseHelper.getCurrentUser() != null) {

            // Re-check role every time MainActivity
            // becomes visible.
            checkUserRole();

            // Reload quizzes
            loadQuizzes();
        }
    }
}