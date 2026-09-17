package com.example.onlinelivequiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinelivequiz.R;

public class ResultActivity extends AppCompatActivity {

    private TextView quizTitleText;
    private TextView scoreText;
    private TextView percentageText;
    private TextView attemptText;
    private TextView remainingAttemptsText;

    private Button leaderboardButton;
    private Button homeButton;

    private String quizId;
    private String quizTitle;

    private int score;
    private int totalQuestions;

    private int attemptNumber;
    private int maxAttempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_result
        );

        // =================================================
        // INITIALIZE VIEWS
        // =================================================

        quizTitleText =
                findViewById(R.id.quizTitleText);

        scoreText =
                findViewById(R.id.scoreText);

        percentageText =
                findViewById(R.id.percentageText);

        attemptText =
                findViewById(R.id.attemptText);

        remainingAttemptsText =
                findViewById(
                        R.id.remainingAttemptsText
                );

        leaderboardButton =
                findViewById(
                        R.id.leaderboardButton
                );

        homeButton =
                findViewById(
                        R.id.homeButton
                );

        // =================================================
        // GET DATA FROM QUIZ ACTIVITY
        // =================================================

        quizId =
                getIntent()
                        .getStringExtra("quizId");

        quizTitle =
                getIntent()
                        .getStringExtra("quizTitle");

        score =
                getIntent()
                        .getIntExtra(
                                "score",
                                0
                        );

        totalQuestions =
                getIntent()
                        .getIntExtra(
                                "totalQuestions",
                                0
                        );

        attemptNumber =
                getIntent()
                        .getIntExtra(
                                "attemptNumber",
                                1
                        );

        maxAttempts =
                getIntent()
                        .getIntExtra(
                                "maxAttempts",
                                1
                        );

        // =================================================
        // QUIZ TITLE
        // =================================================

        if (quizTitle != null
                && !quizTitle.trim().isEmpty()) {

            quizTitleText.setText(
                    quizTitle
            );

        } else {

            quizTitleText.setText(
                    "Quiz Result"
            );
        }

        // =================================================
        // SCORE
        // =================================================

        scoreText.setText(
                score + " / " + totalQuestions
        );

        // =================================================
        // PERCENTAGE
        // =================================================

        if (totalQuestions > 0) {

            double percentage =
                    ((double) score
                            / totalQuestions)
                            * 100;

            percentageText.setText(
                    String.format(
                            java.util.Locale.getDefault(),
                            "%.1f%%",
                            percentage
                    )
            );

        } else {

            percentageText.setText(
                    "0%"
            );
        }

        // =================================================
        // ATTEMPT INFORMATION
        // =================================================

        attemptText.setText(
                "Attempt "
                        + attemptNumber
                        + " of "
                        + maxAttempts
        );

        // =================================================
        // REMAINING ATTEMPTS
        // =================================================

        int remainingAttempts =
                maxAttempts - attemptNumber;

        if (remainingAttempts < 0) {
            remainingAttempts = 0;
        }

        if (remainingAttempts == 0) {

            remainingAttemptsText.setText(
                    "No attempts remaining"
            );

        } else {

            remainingAttemptsText.setText(
                    remainingAttempts
                            + " attempt"
                            + (remainingAttempts == 1
                            ? ""
                            : "s")
                            + " remaining"
            );
        }

        // =================================================
        // LEADERBOARD BUTTON
        // =================================================

        leaderboardButton.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    ResultActivity.this,
                                    LeaderboardActivity.class
                            );

                    intent.putExtra(
                            "quizId",
                            quizId
                    );

                    intent.putExtra(
                            "quizTitle",
                            quizTitle
                    );

                    startActivity(intent);
                }
        );

        // =================================================
        // HOME BUTTON
        // =================================================

        homeButton.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    ResultActivity.this,
                                    MainActivity.class
                            );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );

                    startActivity(intent);

                    finish();
                }
        );
    }
}