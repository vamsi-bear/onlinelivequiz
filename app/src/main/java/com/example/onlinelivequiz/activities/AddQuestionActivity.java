package com.example.onlinelivequiz.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinelivequiz.R;
import com.example.onlinelivequiz.firebase.FirebaseHelper;
import com.example.onlinelivequiz.models.Question;

import java.util.UUID;

public class AddQuestionActivity extends AppCompatActivity {

    private TextView quizTitleText;
    private TextView questionNumberText;

    private EditText questionEditText;
    private EditText optionAEditText;
    private EditText optionBEditText;
    private EditText optionCEditText;
    private EditText optionDEditText;
    private EditText correctAnswerEditText;

    private Button saveQuestionButton;
    private Button finishButton;

    private FirebaseHelper firebaseHelper;

    private String quizId;
    private String quizTitle;

    private int totalQuestions;
    private int currentQuestion = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_question);

        // =====================================================
        // GET DATA FROM AddQuizActivity
        // =====================================================

        quizId = getIntent().getStringExtra("quizId");
        quizTitle = getIntent().getStringExtra("quizTitle");
        totalQuestions = getIntent().getIntExtra("totalQuestions", 1);

        if (quizId == null || quizId.isEmpty()) {
            Toast.makeText(this, "Quiz ID not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================
        quizTitleText = findViewById(R.id.quizTitleText);
        questionNumberText = findViewById(R.id.questionNumberText);
        questionEditText = findViewById(R.id.questionEditText);
        optionAEditText = findViewById(R.id.optionAEditText);
        optionBEditText = findViewById(R.id.optionBEditText);
        optionCEditText = findViewById(R.id.optionCEditText);
        optionDEditText = findViewById(R.id.optionDEditText);
        correctAnswerEditText = findViewById(R.id.correctAnswerEditText);
        saveQuestionButton = findViewById(R.id.saveQuestionButton);
        finishButton = findViewById(R.id.finishButton);

        // =====================================================
        // FIREBASE
        // =====================================================
        firebaseHelper = new FirebaseHelper();

        // =====================================================
        // DISPLAY QUIZ INFORMATION
        // =====================================================
        quizTitleText.setText(quizTitle != null ? quizTitle : "Quiz");
        updateQuestionNumber();

        // =====================================================
        // LISTENERS
        // =====================================================
        saveQuestionButton.setOnClickListener(v -> saveQuestion());
        finishButton.setOnClickListener(v -> finishQuizCreation());
    }

    private void updateQuestionNumber() {
        questionNumberText.setText("Question " + currentQuestion + " of " + totalQuestions);
    }

    private void saveQuestion() {
        String questionText = questionEditText.getText().toString().trim();
        String optionA = optionAEditText.getText().toString().trim();
        String optionB = optionBEditText.getText().toString().trim();
        String optionC = optionCEditText.getText().toString().trim();
        String optionD = optionDEditText.getText().toString().trim();
        String correctAnswer = correctAnswerEditText.getText().toString().trim().toUpperCase();

        if (TextUtils.isEmpty(questionText)) {
            questionEditText.setError("Enter question");
            return;
        }
        if (TextUtils.isEmpty(optionA)) { optionAEditText.setError("Enter option A"); return; }
        if (TextUtils.isEmpty(optionB)) { optionBEditText.setError("Enter option B"); return; }
        if (TextUtils.isEmpty(optionC)) { optionCEditText.setError("Enter option C"); return; }
        if (TextUtils.isEmpty(optionD)) { optionDEditText.setError("Enter option D"); return; }

        if (!correctAnswer.equals("A") && !correctAnswer.equals("B") && !correctAnswer.equals("C") && !correctAnswer.equals("D")) {
            correctAnswerEditText.setError("Enter A, B, C or D");
            return;
        }

        saveQuestionButton.setEnabled(false);
        saveQuestionButton.setText("SAVING...");

        String questionId = UUID.randomUUID().toString();
        Question question = new Question();
        question.setQuestionId(questionId);
        question.setQuizId(quizId);
        question.setQuestion(questionText);
        question.setOptionA(optionA);
        question.setOptionB(optionB);
        question.setOptionC(optionC);
        question.setOptionD(optionD);
        question.setCorrectAnswer(correctAnswer);

        firebaseHelper.addQuestion(questionId, question, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddQuestionActivity.this, "Question " + currentQuestion + " saved!", Toast.LENGTH_SHORT).show();
                if (currentQuestion >= totalQuestions) {
                    finishQuizCreation();
                } else {
                    currentQuestion++;
                    clearFields();
                    updateQuestionNumber();
                    saveQuestionButton.setEnabled(true);
                    saveQuestionButton.setText("SAVE QUESTION");
                }
            } else {
                saveQuestionButton.setEnabled(true);
                saveQuestionButton.setText("SAVE QUESTION");
                Toast.makeText(AddQuestionActivity.this, "Failed to save question", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearFields() {
        questionEditText.setText("");
        optionAEditText.setText("");
        optionBEditText.setText("");
        optionCEditText.setText("");
        optionDEditText.setText("");
        correctAnswerEditText.setText("");
        questionEditText.requestFocus();
    }

    private void finishQuizCreation() {
        Toast.makeText(this, "Quiz created successfully!", Toast.LENGTH_LONG).show();
        finish();
    }
}