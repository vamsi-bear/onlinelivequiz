package com.example.onlinelivequiz.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelivequiz.R;
import com.example.onlinelivequiz.adapters.LeaderboardAdapter;
import com.example.onlinelivequiz.firebase.FirebaseHelper;
import com.example.onlinelivequiz.models.Quiz;
import com.example.onlinelivequiz.models.Score;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class LeaderboardActivity extends AppCompatActivity {

    private TextView quizTitleText;
    private TextView durationText;

    private Spinner quizSpinner;
    private Button viewLeaderboardButton;

    private RecyclerView leaderboardRecyclerView;

    private LeaderboardAdapter adapter;

    private final List<Score> scoreList =
            new ArrayList<>();

    private final List<Quiz> quizList =
            new ArrayList<>();

    private final List<String> quizNames =
            new ArrayList<>();

    private FirebaseHelper firebaseHelper;

    private String quizId;
    private String quizTitle;

    // Quiz ID whose listener is currently active
    private String listeningQuizId;

    private ValueEventListener leaderboardListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_leaderboard
        );

        // -------------------------------------------------
        // INITIALIZE VIEWS
        // -------------------------------------------------

        quizTitleText =
                findViewById(R.id.quizTitleText);

        durationText =
                findViewById(R.id.durationText);

        quizSpinner =
                findViewById(R.id.quizSpinner);

        viewLeaderboardButton =
                findViewById(
                        R.id.viewLeaderboardButton
                );

        leaderboardRecyclerView =
                findViewById(
                        R.id.leaderboardRecyclerView
                );

        // -------------------------------------------------
        // FIREBASE
        // -------------------------------------------------

        firebaseHelper =
                new FirebaseHelper();

        // -------------------------------------------------
        // RECYCLER VIEW
        // -------------------------------------------------

        adapter =
                new LeaderboardAdapter(
                        scoreList
                );

        leaderboardRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        leaderboardRecyclerView.setAdapter(
                adapter
        );

        // -------------------------------------------------
        // GET INTENT DATA
        // -------------------------------------------------

        quizId =
                getIntent()
                        .getStringExtra("quizId");

        quizTitle =
                getIntent()
                        .getStringExtra("quizTitle");

        // -------------------------------------------------
        // STUDENT OR ADMIN
        // -------------------------------------------------

        if (quizId != null
                && !quizId.isEmpty()) {

            // Student leaderboard
            quizTitleText.setText(
                    quizTitle != null
                            ? quizTitle
                            : "Leaderboard"
            );

            quizSpinner.setVisibility(
                    View.GONE
            );

            viewLeaderboardButton.setVisibility(
                    View.GONE
            );

            loadQuizDuration(quizId);

            loadLeaderboard();

        } else {

            // Admin leaderboard
            loadQuizzes();
        }

        // -------------------------------------------------
        // ADMIN VIEW BUTTON
        // -------------------------------------------------

        viewLeaderboardButton.setOnClickListener(
                v -> {

                    int position =
                            quizSpinner
                                    .getSelectedItemPosition();

                    if (position < 0
                            || position >= quizList.size()) {

                        Toast.makeText(
                                LeaderboardActivity.this,
                                "Please select a quiz",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Quiz selectedQuiz =
                            quizList.get(position);

                    quizId =
                            selectedQuiz.getQuizId();

                    quizTitle =
                            selectedQuiz.getTitle();

                    quizTitleText.setText(
                            quizTitle != null
                                    ? quizTitle
                                    : "Leaderboard"
                    );

                    loadQuizDuration(
                            quizId
                    );

                    loadLeaderboard();
                }
        );
    }

    // =====================================================
    // LOAD QUIZZES FOR ADMIN
    // =====================================================

    private void loadQuizzes() {

        firebaseHelper.getAllQuizzes(
                task -> {

                    if (task.isSuccessful()) {

                        quizList.clear();

                        quizNames.clear();

                        if (task.getResult() != null) {

                            for (DocumentSnapshot document :
                                    task.getResult()
                                            .getDocuments()) {

                                Quiz quiz =
                                        document.toObject(
                                                Quiz.class
                                        );

                                if (quiz != null) {

                                    if (quiz.getQuizId()
                                            == null
                                            || quiz.getQuizId()
                                            .isEmpty()) {

                                        quiz.setQuizId(
                                                document.getId()
                                        );
                                    }

                                    quizList.add(
                                            quiz
                                    );

                                    quizNames.add(
                                            quiz.getTitle()
                                                    != null
                                                    ? quiz.getTitle()
                                                    : "Untitled Quiz"
                                    );
                                }
                            }
                        }

                        if (quizList.isEmpty()) {

                            Toast.makeText(
                                    LeaderboardActivity.this,
                                    "No quizzes available",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        ArrayAdapter<String>
                                spinnerAdapter =
                                new ArrayAdapter<>(
                                        LeaderboardActivity.this,
                                        android.R.layout
                                                .simple_spinner_item,
                                        quizNames
                                );

                        spinnerAdapter
                                .setDropDownViewResource(
                                        android.R.layout
                                                .simple_spinner_dropdown_item
                                );

                        quizSpinner.setAdapter(
                                spinnerAdapter
                        );

                    } else {

                        Toast.makeText(
                                LeaderboardActivity.this,
                                "Failed to load quizzes",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =====================================================
    // LOAD QUIZ DURATION
    // =====================================================

    private void loadQuizDuration(
            String selectedQuizId) {

        firebaseHelper.getQuizDetails(
                selectedQuizId,
                task -> {

                    if (task.isSuccessful()
                            && task.getResult() != null
                            && task.getResult().exists()) {

                        Long duration =
                                task.getResult()
                                        .getLong("duration");

                        if (duration != null) {

                            durationText.setText(
                                    String.format(
                                            Locale.getDefault(),
                                            "Test Duration: %d minutes",
                                            duration
                                    )
                            );

                        } else {

                            durationText.setText(
                                    "Test Duration: Not available"
                            );
                        }

                    } else {

                        durationText.setText(
                                "Test Duration: Not available"
                        );
                    }
                }
        );
    }

    // =====================================================
    // LOAD LEADERBOARD
    // =====================================================

    private void loadLeaderboard() {

        if (quizId == null
                || quizId.isEmpty()) {

            return;
        }

        // -------------------------------------------------
        // REMOVE OLD LISTENER
        // -------------------------------------------------

        if (leaderboardListener != null
                && listeningQuizId != null) {

            firebaseHelper.removeLeaderboardListener(
                    listeningQuizId,
                    leaderboardListener
            );

            leaderboardListener = null;

            listeningQuizId = null;
        }

        // Clear current list
        scoreList.clear();

        adapter.notifyDataSetChanged();

        // Remember current quiz
        listeningQuizId = quizId;

        // -------------------------------------------------
        // CREATE LISTENER
        // -------------------------------------------------

        leaderboardListener =
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        /*
                         * Map:
                         *
                         * userId -> best Score
                         *
                         * This ensures only ONE row
                         * per student.
                         */
                        Map<String, Score>
                                bestScores =
                                new HashMap<>();

                        // ---------------------------------------------
                        // READ ALL LEADERBOARD RECORDS
                        // ---------------------------------------------

                        for (DataSnapshot data :
                                snapshot.getChildren()) {

                            Score score =
                                    data.getValue(
                                            Score.class
                                    );

                            if (score == null) {
                                continue;
                            }

                            String userId =
                                    score.getUserId();

                            if (userId == null
                                    || userId.isEmpty()) {

                                continue;
                            }

                            // -----------------------------------------
                            // CHECK EXISTING BEST SCORE
                            // -----------------------------------------

                            Score existing =
                                    bestScores.get(
                                            userId
                                    );

                            if (existing == null) {

                                bestScores.put(
                                        userId,
                                        score
                                );

                            } else {

                                /*
                                 * Higher score is better.
                                 */
                                if (score.getScore()
                                        > existing.getScore()) {

                                    bestScores.put(
                                            userId,
                                            score
                                    );

                                } else if (
                                        score.getScore()
                                                == existing.getScore()
                                                &&
                                                score.getCompletionTime()
                                                        < existing
                                                        .getCompletionTime()
                                ) {

                                    /*
                                     * Same score:
                                     * faster time is better.
                                     */
                                    bestScores.put(
                                            userId,
                                            score
                                    );
                                }
                            }
                        }

                        // ---------------------------------------------
                        // UPDATE LIST
                        // ---------------------------------------------

                        scoreList.clear();

                        scoreList.addAll(
                                bestScores.values()
                        );

                        // ---------------------------------------------
                        // SORT
                        // ---------------------------------------------

                        Collections.sort(
                                scoreList,
                                (s1, s2) -> {

                                    // Higher score first
                                    int scoreComparison =
                                            Integer.compare(
                                                    s2.getScore(),
                                                    s1.getScore()
                                            );

                                    if (scoreComparison != 0) {

                                        return scoreComparison;
                                    }

                                    // Same score:
                                    // Faster completion first
                                    return Long.compare(
                                            s1.getCompletionTime(),
                                            s2.getCompletionTime()
                                    );
                                }
                        );

                        // ---------------------------------------------
                        // UPDATE RECYCLER VIEW
                        // ---------------------------------------------

                        adapter.notifyDataSetChanged();

                        // ---------------------------------------------
                        // EMPTY LEADERBOARD
                        // ---------------------------------------------

                        if (scoreList.isEmpty()) {

                            Toast.makeText(
                                    LeaderboardActivity.this,
                                    "No students have completed this quiz yet",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                LeaderboardActivity.this,
                                "Failed to load leaderboard: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                };

        // -------------------------------------------------
        // START LISTENING
        // -------------------------------------------------

        firebaseHelper.listenToLeaderboard(
                quizId,
                leaderboardListener
        );
    }

    // =====================================================
    // REMOVE LISTENER
    // =====================================================

    @Override
    protected void onDestroy() {

        if (leaderboardListener != null
                && listeningQuizId != null) {

            firebaseHelper.removeLeaderboardListener(
                    listeningQuizId,
                    leaderboardListener
            );

            leaderboardListener = null;

            listeningQuizId = null;
        }

        super.onDestroy();
    }
}