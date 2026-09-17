package com.example.onlinelivequiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelivequiz.R;
import com.example.onlinelivequiz.adapters.QuestionAdapter;
import com.example.onlinelivequiz.firebase.FirebaseHelper;
import com.example.onlinelivequiz.models.Question;
import com.example.onlinelivequiz.models.Score;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private TextView quizTitleText;
    private TextView timerText;
    private RecyclerView questionRecyclerView;
    private Button submitButton;

    private FirebaseHelper firebaseHelper;
    private QuestionAdapter questionAdapter;

    private final List<Question> questionList = new ArrayList<>();

    private String quizId;
    private String quizTitle;

    private CountDownTimer countDownTimer;

    private long remainingTimeMillis;
    private long totalQuizTimeMillis;

    // Exact time when quiz starts
    private long quizStartTimeMillis;

    // Admin-defined maximum attempts
    private int maxAttempts;

    // Number of attempts already used
    private int attemptsUsed;

    // Current attempt number
    private int currentAttemptNumber;

    // Prevent duplicate submission
    private boolean quizSubmitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz);

        // =====================================================
        // GET QUIZ INFORMATION
        // =====================================================

        quizId = getIntent().getStringExtra("quizId");
        quizTitle = getIntent().getStringExtra("quizTitle");

        if (quizId == null || quizId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Quiz ID not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        quizTitleText = findViewById(R.id.quizTitleText);
        timerText = findViewById(R.id.timerText);
        questionRecyclerView = findViewById(R.id.questionRecyclerView);
        submitButton = findViewById(R.id.submitButton);

        // =====================================================
        // SET QUIZ TITLE
        // =====================================================

        if (quizTitle != null && !quizTitle.trim().isEmpty()) {
            quizTitleText.setText(quizTitle);
        } else {
            quizTitleText.setText("Quiz");
        }

        // =====================================================
        // SUBMIT BUTTON CLICK
        // =====================================================

        submitButton.setOnClickListener(v -> {
            submitQuiz();
        });

        // =====================================================
        // FIREBASE
        // =====================================================

        firebaseHelper = new FirebaseHelper();

        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        questionRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        questionAdapter = new QuestionAdapter(questionList);

        questionRecyclerView.setAdapter(questionAdapter);

        // =====================================================
        // DISABLE SUBMIT UNTIL QUIZ STARTS
        // =====================================================

        submitButton.setEnabled(false);

        // =====================================================
        // CHECK ATTEMPTS
        // =====================================================

        checkAttempts();
    }

    // =====================================================
    // CHECK STUDENT ATTEMPTS
    // =====================================================

    private void checkAttempts() {

        FirebaseUser currentUser =
                firebaseHelper.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        String userId = currentUser.getUid();

        // Get quiz details
        firebaseHelper.getQuizDetails(
                quizId,
                task -> {

                    if (!task.isSuccessful()
                            || task.getResult() == null
                            || !task.getResult().exists()) {

                        Toast.makeText(
                                QuizActivity.this,
                                "Quiz not found",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                        return;
                    }

                    DocumentSnapshot quizDocument =
                            task.getResult();

                    // Get maximum attempts
                    Long maxAttemptsValue =
                            quizDocument.getLong("maxAttempts");

                    if (maxAttemptsValue == null
                            || maxAttemptsValue <= 0) {

                        Toast.makeText(
                                QuizActivity.this,
                                "Invalid attempts configuration",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                        return;
                    }

                    maxAttempts = maxAttemptsValue.intValue();

                    // Get student's used attempts
                    firebaseHelper.getAttemptCount(
                            quizId,
                            userId,
                            attemptTask -> {

                                if (!attemptTask.isSuccessful()) {

                                    Toast.makeText(
                                            QuizActivity.this,
                                            "Failed to check attempts",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    finish();
                                    return;
                                }

                                Integer count =
                                        attemptTask.getResult();

                                attemptsUsed =
                                        count != null ? count : 0;

                                // Check attempt limit
                                if (attemptsUsed >= maxAttempts) {

                                    Toast.makeText(
                                            QuizActivity.this,
                                            "No attempts remaining. "
                                                    + "You have used all "
                                                    + maxAttempts
                                                    + " attempts.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    finish();
                                    return;
                                }

                                // Current attempt
                                currentAttemptNumber =
                                        attemptsUsed + 1;

                                int remainingAttempts =
                                        maxAttempts - attemptsUsed;

                                Toast.makeText(
                                        QuizActivity.this,
                                        "Attempt "
                                                + currentAttemptNumber
                                                + " of "
                                                + maxAttempts
                                                + ". Remaining: "
                                                + remainingAttempts,
                                        Toast.LENGTH_LONG
                                ).show();

                                // Load questions
                                loadQuestions();
                            }
                    );
                }
        );
    }

    // =====================================================
    // LOAD QUESTIONS
    // =====================================================

    private void loadQuestions() {

        firebaseHelper.getQuestions(
                quizId,
                task -> {

                    if (task.isSuccessful()) {

                        questionList.clear();

                        if (task.getResult() != null) {

                            for (DocumentSnapshot document :
                                    task.getResult().getDocuments()) {

                                Question question =
                                        document.toObject(
                                                Question.class
                                        );

                                if (question != null) {

                                    // Set question ID if missing
                                    if (question.getQuestionId() == null
                                            || question.getQuestionId().isEmpty()) {

                                        question.setQuestionId(
                                                document.getId()
                                        );
                                    }

                                    questionList.add(question);
                                }
                            }
                        }

                        questionAdapter.notifyDataSetChanged();

                        // No questions
                        if (questionList.isEmpty()) {

                            Toast.makeText(
                                    QuizActivity.this,
                                    "No questions found",
                                    Toast.LENGTH_LONG
                            ).show();

                            submitButton.setEnabled(false);

                            return;
                        }

                        // Load duration
                        loadQuizDuration();

                    } else {

                        Toast.makeText(
                                QuizActivity.this,
                                "Failed to load questions",
                                Toast.LENGTH_LONG
                        ).show();

                        submitButton.setEnabled(false);
                    }
                }
        );
    }

    // =====================================================
    // LOAD QUIZ DURATION
    // =====================================================

    private void loadQuizDuration() {

        firebaseHelper.getQuizDetails(
                quizId,
                task -> {

                    if (task.isSuccessful()
                            && task.getResult() != null
                            && task.getResult().exists()) {

                        DocumentSnapshot document =
                                task.getResult();

                        Long duration =
                                document.getLong("duration");

                        if (duration == null
                                || duration <= 0) {

                            Toast.makeText(
                                    QuizActivity.this,
                                    "Invalid quiz duration",
                                    Toast.LENGTH_LONG
                            ).show();

                            submitButton.setEnabled(false);

                            return;
                        }

                        totalQuizTimeMillis =
                                duration * 60 * 1000L;

                        // Start timer
                        startTimer();

                    } else {

                        Toast.makeText(
                                QuizActivity.this,
                                "Failed to load quiz duration",
                                Toast.LENGTH_LONG
                        ).show();

                        submitButton.setEnabled(false);
                    }
                }
        );
    }

    // =====================================================
    // START TIMER
    // =====================================================

    private void startTimer() {

        remainingTimeMillis =
                totalQuizTimeMillis;

        // Record exact start time
        quizStartTimeMillis =
                System.currentTimeMillis();

        // Enable submit button
        submitButton.setEnabled(true);

        submitButton.setText("SUBMIT QUIZ");

        countDownTimer =
                new CountDownTimer(
                        totalQuizTimeMillis,
                        1000
                ) {

                    @Override
                    public void onTick(
                            long millisUntilFinished) {

                        remainingTimeMillis =
                                millisUntilFinished;

                        updateTimerText(
                                millisUntilFinished
                        );
                    }

                    @Override
                    public void onFinish() {

                        remainingTimeMillis = 0;

                        timerText.setText("00:00");

                        Toast.makeText(
                                QuizActivity.this,
                                "Time's up!",
                                Toast.LENGTH_SHORT
                        ).show();

                        submitQuiz();
                    }
                };

        countDownTimer.start();
    }

    // =====================================================
    // UPDATE TIMER
    // =====================================================

    private void updateTimerText(
            long milliseconds) {

        long minutes =
                milliseconds / 60000;

        long seconds =
                (milliseconds % 60000) / 1000;

        timerText.setText(
                String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        minutes,
                        seconds
                )
        );
    }

    // =====================================================
    // SUBMIT QUIZ
    // =====================================================

    private void submitQuiz() {

        // Prevent duplicate submission
        if (quizSubmitted) {
            return;
        }

        // Make sure questions exist
        if (questionList.isEmpty()) {

            Toast.makeText(
                    this,
                    "No questions available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        quizSubmitted = true;

        // Stop timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Disable submit button
        submitButton.setEnabled(false);

        submitButton.setText("SUBMITTING...");

        // =================================================
        // CALCULATE SCORE
        // =================================================

        int score = calculateScore();

        // =================================================
        // CALCULATE COMPLETION TIME
        // =================================================

        long currentTimeMillis =
                System.currentTimeMillis();

        long completionTimeMillis =
                currentTimeMillis -
                        quizStartTimeMillis;

        if (completionTimeMillis < 0) {
            completionTimeMillis = 0;
        }

        if (completionTimeMillis >
                totalQuizTimeMillis) {

            completionTimeMillis =
                    totalQuizTimeMillis;
        }

        long completionTimeSeconds =
                completionTimeMillis / 1000;

        // =================================================
        // SAVE ATTEMPT
        // =================================================

        saveAttempt(
                score,
                completionTimeSeconds
        );
    }

    // =====================================================
    // CALCULATE SCORE
    // =====================================================

    private int calculateScore() {

        int score = 0;

        for (Question question : questionList) {

            String selectedAnswer =
                    question.getSelectedAnswer();

            String correctAnswer =
                    question.getCorrectAnswer();

            if (selectedAnswer != null
                    && correctAnswer != null
                    && selectedAnswer.equalsIgnoreCase(
                    correctAnswer.trim())) {

                score++;
            }
        }

        return score;
    }

    // =====================================================
    // SAVE ATTEMPT
    // =====================================================

    private void saveAttempt(
            int score,
            long completionTimeSeconds) {

        FirebaseUser currentUser =
                firebaseHelper.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "User session expired",
                    Toast.LENGTH_LONG
            ).show();

            openResultActivity(score);

            return;
        }

        String userId =
                currentUser.getUid();

        String userName =
                currentUser.getEmail();

        // =================================================
        // SAVE ATTEMPT TO FIRESTORE
        // =================================================

        firebaseHelper.saveAttempt(
                quizId,
                userId,
                currentAttemptNumber,
                score,
                completionTimeSeconds,
                task -> {

                    if (!task.isSuccessful()) {

                        String errorMessage =
                                "Unknown error";

                        if (task.getException() != null) {
                            errorMessage =
                                    task.getException()
                                            .getMessage();
                        }

                        Toast.makeText(
                                QuizActivity.this,
                                "Failed to save attempt: "
                                        + errorMessage,
                                Toast.LENGTH_LONG
                        ).show();

                        // Allow retry
                        submitButton.setEnabled(true);

                        submitButton.setText(
                                "SUBMIT QUIZ"
                        );

                        quizSubmitted = false;

                        return;
                    }

                    // =================================================
                    // SAVE / UPDATE LEADERBOARD
                    // =================================================

                    Score scoreObject =
                            new Score(
                                    userId,
                                    userName,
                                    score,
                                    completionTimeSeconds
                            );

                    firebaseHelper.submitScore(
                            quizId,
                            scoreObject,
                            leaderboardTask -> {

                                if (!leaderboardTask.isSuccessful()) {

                                    Toast.makeText(
                                            QuizActivity.this,
                                            "Attempt saved, but leaderboard update failed",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }

                                // Open result even if leaderboard fails
                                openResultActivity(score);
                            }
                    );
                }
        );
    }

    // =====================================================
    // OPEN RESULT ACTIVITY
    // =====================================================

    private void openResultActivity(
            int score) {

        Intent intent =
                new Intent(
                        QuizActivity.this,
                        ResultActivity.class
                );

        intent.putExtra(
                "quizId",
                quizId
        );

        intent.putExtra(
                "quizTitle",
                quizTitle
        );

        intent.putExtra(
                "score",
                score
        );

        intent.putExtra(
                "totalQuestions",
                questionList.size()
        );

        intent.putExtra(
                "attemptNumber",
                currentAttemptNumber
        );

        intent.putExtra(
                "maxAttempts",
                maxAttempts
        );

        startActivity(intent);

        finish();
    }

    // =====================================================
    // ACTIVITY DESTROY
    // =====================================================

    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        super.onDestroy();
    }
}