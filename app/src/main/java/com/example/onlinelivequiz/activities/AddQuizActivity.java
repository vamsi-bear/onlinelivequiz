package com.example.onlinelivequiz.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinelivequiz.R;
import com.example.onlinelivequiz.firebase.FirebaseHelper;
import com.example.onlinelivequiz.models.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddQuizActivity extends AppCompatActivity {

    // Quiz details
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText durationEditText;
    private EditText totalQuestionsEditText;
    private EditText maxAttemptsEditText;

    // Question area
    private LinearLayout questionsContainer;
    private Button createQuizButton;

    private FirebaseHelper firebaseHelper;

    // Stores all question views
    private final List<QuestionViews> questionViewsList = new ArrayList<>();

    // ---------------------------------------------------------
    // QUESTION VIEW HOLDER
    // ---------------------------------------------------------

    private static class QuestionViews {

        EditText questionEditText;
        EditText optionAEditText;
        EditText optionBEditText;
        EditText optionCEditText;
        EditText optionDEditText;

        RadioGroup correctAnswerGroup;

        RadioButton optionARadio;
        RadioButton optionBRadio;
        RadioButton optionCRadio;
        RadioButton optionDRadio;

        TextView questionNumberText;

        QuestionViews(
                TextView questionNumberText,
                EditText questionEditText,
                EditText optionAEditText,
                EditText optionBEditText,
                EditText optionCEditText,
                EditText optionDEditText,
                RadioGroup correctAnswerGroup,
                RadioButton optionARadio,
                RadioButton optionBRadio,
                RadioButton optionCRadio,
                RadioButton optionDRadio) {

            this.questionNumberText = questionNumberText;
            this.questionEditText = questionEditText;
            this.optionAEditText = optionAEditText;
            this.optionBEditText = optionBEditText;
            this.optionCEditText = optionCEditText;
            this.optionDEditText = optionDEditText;

            this.correctAnswerGroup = correctAnswerGroup;

            this.optionARadio = optionARadio;
            this.optionBRadio = optionBRadio;
            this.optionCRadio = optionCRadio;
            this.optionDRadio = optionDRadio;
        }
    }

    // ---------------------------------------------------------
    // ON CREATE
    // ---------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_quiz);

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        durationEditText = findViewById(R.id.durationEditText);
        totalQuestionsEditText = findViewById(R.id.totalQuestionsEditText);
        maxAttemptsEditText = findViewById(R.id.maxAttemptsEditText);

        questionsContainer = findViewById(R.id.questionsContainer);
        createQuizButton = findViewById(R.id.createQuizButton);

        firebaseHelper = new FirebaseHelper();

        // -----------------------------------------------------
        // WHEN TOTAL QUESTIONS CHANGES
        // -----------------------------------------------------

        totalQuestionsEditText.setOnFocusChangeListener((v, hasFocus) -> {

            if (!hasFocus) {
                createQuestionFields();
            }
        });

        // -----------------------------------------------------
        // CREATE QUIZ
        // -----------------------------------------------------

        createQuizButton.setOnClickListener(v -> {

            // Make sure question fields exist
            createQuestionFields();

            createQuiz();
        });
    }

    // ---------------------------------------------------------
    // CREATE QUESTION FIELDS
    // ---------------------------------------------------------

    private void createQuestionFields() {

        String totalText = totalQuestionsEditText
                .getText()
                .toString()
                .trim();

        if (totalText.isEmpty()) {
            return;
        }

        int totalQuestions;

        try {
            totalQuestions = Integer.parseInt(totalText);
        } catch (NumberFormatException e) {
            return;
        }

        if (totalQuestions <= 0) {
            return;
        }

        // Maximum 50 questions to prevent an excessively large form
        if (totalQuestions > 50) {
            totalQuestions = 50;
            totalQuestionsEditText.setText("50");
        }

        // Do not recreate fields if the correct number already exists
        if (questionViewsList.size() == totalQuestions) {
            return;
        }

        // Save existing data before rebuilding
        List<String[]> oldData = new ArrayList<>();

        for (QuestionViews q : questionViewsList) {

            String question =
                    q.questionEditText.getText().toString();

            String a =
                    q.optionAEditText.getText().toString();

            String b =
                    q.optionBEditText.getText().toString();

            String c =
                    q.optionCEditText.getText().toString();

            String d =
                    q.optionDEditText.getText().toString();

            oldData.add(new String[]{question, a, b, c, d});
        }

        questionsContainer.removeAllViews();
        questionViewsList.clear();

        // -----------------------------------------------------
        // CREATE EACH QUESTION
        // -----------------------------------------------------

        for (int i = 0; i < totalQuestions; i++) {

            QuestionViews views = createSingleQuestion(i + 1);

            // Restore previous data if available
            if (i < oldData.size()) {

                views.questionEditText.setText(oldData.get(i)[0]);
                views.optionAEditText.setText(oldData.get(i)[1]);
                views.optionBEditText.setText(oldData.get(i)[2]);
                views.optionCEditText.setText(oldData.get(i)[3]);
                views.optionDEditText.setText(oldData.get(i)[4]);
            }

            questionViewsList.add(views);
        }
    }

    // ---------------------------------------------------------
    // CREATE ONE QUESTION
    // ---------------------------------------------------------

    private QuestionViews createSingleQuestion(int questionNumber) {

        // Main question card
        LinearLayout questionLayout =
                new LinearLayout(this);

        questionLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        questionLayout.setPadding(
                20,
                20,
                20,
                20
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                12,
                0,
                20
        );

        questionLayout.setLayoutParams(cardParams);

        // -----------------------------------------------------
        // QUESTION NUMBER
        // -----------------------------------------------------

        TextView questionNumberText =
                new TextView(this);

        questionNumberText.setText(
                "Question " + questionNumber
        );

        questionNumberText.setTextSize(21);
        questionNumberText.setTextColor(
                getResources().getColor(R.color.primary)
        );

        questionNumberText.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        questionLayout.addView(
                questionNumberText
        );

        // -----------------------------------------------------
        // QUESTION
        // -----------------------------------------------------

        EditText questionEditText =
                createEditText(
                        "Enter question " + questionNumber,
                        false
                );

        questionEditText.setMinLines(2);
        questionEditText.setGravity(
                android.view.Gravity.TOP
        );

        questionLayout.addView(
                questionEditText
        );

        // -----------------------------------------------------
        // OPTION A
        // -----------------------------------------------------

        EditText optionAEditText =
                createEditText(
                        "Option A",
                        false
                );

        questionLayout.addView(
                optionAEditText
        );

        // -----------------------------------------------------
        // OPTION B
        // -----------------------------------------------------

        EditText optionBEditText =
                createEditText(
                        "Option B",
                        false
                );

        questionLayout.addView(
                optionBEditText
        );

        // -----------------------------------------------------
        // OPTION C
        // -----------------------------------------------------

        EditText optionCEditText =
                createEditText(
                        "Option C",
                        false
                );

        questionLayout.addView(
                optionCEditText
        );

        // -----------------------------------------------------
        // OPTION D
        // -----------------------------------------------------

        EditText optionDEditText =
                createEditText(
                        "Option D",
                        false
                );

        questionLayout.addView(
                optionDEditText
        );

        // -----------------------------------------------------
        // CORRECT ANSWER LABEL
        // -----------------------------------------------------

        TextView correctAnswerText =
                new TextView(this);

        correctAnswerText.setText(
                "Select Correct Answer"
        );

        correctAnswerText.setTextSize(17);

        correctAnswerText.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        correctAnswerText.setPadding(
                0,
                12,
                0,
                4
        );

        questionLayout.addView(
                correctAnswerText
        );

        // -----------------------------------------------------
        // RADIO GROUP
        // -----------------------------------------------------

        RadioGroup correctAnswerGroup =
                new RadioGroup(this);

        correctAnswerGroup.setOrientation(
                RadioGroup.VERTICAL
        );

        // A
        RadioButton optionARadio =
                new RadioButton(this);

        optionARadio.setText("Option A");
        optionARadio.setTextSize(16);
        optionARadio.setId(
                View.generateViewId()
        );

        correctAnswerGroup.addView(
                optionARadio
        );

        // B
        RadioButton optionBRadio =
                new RadioButton(this);

        optionBRadio.setText("Option B");
        optionBRadio.setTextSize(16);
        optionBRadio.setId(
                View.generateViewId()
        );

        correctAnswerGroup.addView(
                optionBRadio
        );

        // C
        RadioButton optionCRadio =
                new RadioButton(this);

        optionCRadio.setText("Option C");
        optionCRadio.setTextSize(16);
        optionCRadio.setId(
                View.generateViewId()
        );

        correctAnswerGroup.addView(
                optionCRadio
        );

        // D
        RadioButton optionDRadio =
                new RadioButton(this);

        optionDRadio.setText("Option D");
        optionDRadio.setTextSize(16);
        optionDRadio.setId(
                View.generateViewId()
        );

        correctAnswerGroup.addView(
                optionDRadio
        );

        questionLayout.addView(
                correctAnswerGroup
        );

        // -----------------------------------------------------
        // ADD QUESTION LAYOUT
        // -----------------------------------------------------

        questionsContainer.addView(
                questionLayout
        );

        return new QuestionViews(
                questionNumberText,
                questionEditText,
                optionAEditText,
                optionBEditText,
                optionCEditText,
                optionDEditText,
                correctAnswerGroup,
                optionARadio,
                optionBRadio,
                optionCRadio,
                optionDRadio
        );
    }

    // ---------------------------------------------------------
    // CREATE EDIT TEXT
    // ---------------------------------------------------------

    private EditText createEditText(
            String hint,
            boolean number) {

        EditText editText =
                new EditText(this);

        editText.setHint(hint);

        editText.setTextSize(16);

        editText.setPadding(
                14,
                14,
                14,
                14
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                8,
                0,
                8
        );

        editText.setLayoutParams(params);

        if (number) {

            editText.setInputType(
                    InputType.TYPE_CLASS_NUMBER
            );

        } else {

            editText.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            );
        }

        return editText;
    }

    // ---------------------------------------------------------
    // CREATE QUIZ
    // ---------------------------------------------------------

    private void createQuiz() {

        // -----------------------------------------------------
        // GET QUIZ DETAILS
        // -----------------------------------------------------

        String title =
                titleEditText.getText()
                        .toString()
                        .trim();

        String description =
                descriptionEditText.getText()
                        .toString()
                        .trim();

        String durationText =
                durationEditText.getText()
                        .toString()
                        .trim();

        String totalQuestionsText =
                totalQuestionsEditText.getText()
                        .toString()
                        .trim();

        String maxAttemptsText =
                maxAttemptsEditText.getText()
                        .toString()
                        .trim();

        // -----------------------------------------------------
        // BASIC VALIDATION
        // -----------------------------------------------------

        if (title.isEmpty()) {

            titleEditText.setError(
                    "Enter quiz title"
            );

            titleEditText.requestFocus();

            return;
        }

        if (description.isEmpty()) {

            descriptionEditText.setError(
                    "Enter quiz description"
            );

            descriptionEditText.requestFocus();

            return;
        }

        if (durationText.isEmpty()) {

            durationEditText.setError(
                    "Enter duration"
            );

            durationEditText.requestFocus();

            return;
        }

        if (totalQuestionsText.isEmpty()) {

            totalQuestionsEditText.setError(
                    "Enter total questions"
            );

            totalQuestionsEditText.requestFocus();

            return;
        }

        if (maxAttemptsText.isEmpty()) {

            maxAttemptsEditText.setError(
                    "Enter allowed attempts"
            );

            maxAttemptsEditText.requestFocus();

            return;
        }

        // -----------------------------------------------------
        // PARSE NUMBERS
        // -----------------------------------------------------

        int duration;
        int totalQuestions;
        int maxAttempts;

        try {

            duration =
                    Integer.parseInt(durationText);

            totalQuestions =
                    Integer.parseInt(totalQuestionsText);

            maxAttempts =
                    Integer.parseInt(maxAttemptsText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Enter valid numbers",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // -----------------------------------------------------
        // NUMBER VALIDATION
        // -----------------------------------------------------

        if (duration <= 0) {

            durationEditText.setError(
                    "Duration must be greater than 0"
            );

            durationEditText.requestFocus();

            return;
        }

        if (totalQuestions <= 0) {

            totalQuestionsEditText.setError(
                    "Questions must be greater than 0"
            );

            totalQuestionsEditText.requestFocus();

            return;
        }

        if (totalQuestions > 50) {

            totalQuestionsEditText.setError(
                    "Maximum 50 questions"
            );

            totalQuestionsEditText.requestFocus();

            return;
        }

        if (maxAttempts <= 0) {

            maxAttemptsEditText.setError(
                    "Attempts must be greater than 0"
            );

            maxAttemptsEditText.requestFocus();

            return;
        }

        // -----------------------------------------------------
        // MAKE SURE QUESTION COUNT MATCHES
        // -----------------------------------------------------

        if (questionViewsList.size() != totalQuestions) {

            createQuestionFields();
        }

        if (questionViewsList.size() != totalQuestions) {

            Toast.makeText(
                    this,
                    "Please enter the total number of questions",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // -----------------------------------------------------
        // VALIDATE EVERY QUESTION
        // -----------------------------------------------------

        for (int i = 0;
             i < questionViewsList.size();
             i++) {

            QuestionViews q =
                    questionViewsList.get(i);

            String question =
                    q.questionEditText
                            .getText()
                            .toString()
                            .trim();

            String optionA =
                    q.optionAEditText
                            .getText()
                            .toString()
                            .trim();

            String optionB =
                    q.optionBEditText
                            .getText()
                            .toString()
                            .trim();

            String optionC =
                    q.optionCEditText
                            .getText()
                            .toString()
                            .trim();

            String optionD =
                    q.optionDEditText
                            .getText()
                            .toString()
                            .trim();

            if (question.isEmpty()) {

                q.questionEditText.setError(
                        "Enter question"
                );

                q.questionEditText.requestFocus();

                Toast.makeText(
                        this,
                        "Complete Question " + (i + 1),
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (optionA.isEmpty()) {

                q.optionAEditText.setError(
                        "Enter Option A"
                );

                q.optionAEditText.requestFocus();

                return;
            }

            if (optionB.isEmpty()) {

                q.optionBEditText.setError(
                        "Enter Option B"
                );

                q.optionBEditText.requestFocus();

                return;
            }

            if (optionC.isEmpty()) {

                q.optionCEditText.setError(
                        "Enter Option C"
                );

                q.optionCEditText.requestFocus();

                return;
            }

            if (optionD.isEmpty()) {

                q.optionDEditText.setError(
                        "Enter Option D"
                );

                q.optionDEditText.requestFocus();

                return;
            }

            if (q.correctAnswerGroup.getCheckedRadioButtonId()
                    == -1) {

                Toast.makeText(
                        this,
                        "Select correct answer for Question "
                                + (i + 1),
                        Toast.LENGTH_SHORT
                ).show();

                q.correctAnswerGroup.requestFocus();

                return;
            }
        }

        // -----------------------------------------------------
        // GENERATE QUIZ ID
        // -----------------------------------------------------

        String quizId =
                UUID.randomUUID().toString();

        // -----------------------------------------------------
        // CREATE QUIZ OBJECT
        // -----------------------------------------------------

        Quiz quiz =
                new Quiz();

        quiz.setQuizId(quizId);
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setDuration(duration);

        // IMPORTANT:
        // Number of questions actually entered
        quiz.setTotalQuestions(
                questionViewsList.size()
        );

        quiz.setMaxAttempts(maxAttempts);

        // -----------------------------------------------------
        // DISABLE BUTTON
        // -----------------------------------------------------

        createQuizButton.setEnabled(false);

        createQuizButton.setText(
                "CREATING QUIZ..."
        );

        // -----------------------------------------------------
        // SAVE QUIZ FIRST
        // -----------------------------------------------------

        firebaseHelper.addQuiz(
                quizId,
                quiz,
                task -> {

                    if (!task.isSuccessful()) {

                        Toast.makeText(
                                AddQuizActivity.this,
                                "Failed to create quiz: "
                                        + getErrorMessage(task),
                                Toast.LENGTH_LONG
                        ).show();

                        createQuizButton.setEnabled(true);

                        createQuizButton.setText(
                                "CREATE QUIZ"
                        );

                        return;
                    }

                    // Quiz saved successfully
                    // Now save all questions

                    saveQuestions(
                            quizId,
                            0
                    );
                }
        );
    }

    // ---------------------------------------------------------
    // SAVE QUESTIONS ONE BY ONE
    // ---------------------------------------------------------

    private void saveQuestions(
            String quizId,
            int questionIndex) {

        // -----------------------------------------------------
        // ALL QUESTIONS SAVED
        // -----------------------------------------------------

        if (questionIndex >= questionViewsList.size()) {

            Toast.makeText(
                    AddQuizActivity.this,
                    "Quiz and all questions created successfully",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        QuestionViews q =
                questionViewsList.get(questionIndex);

        // -----------------------------------------------------
        // GET QUESTION DATA
        // -----------------------------------------------------

        String question =
                q.questionEditText
                        .getText()
                        .toString()
                        .trim();

        String optionA =
                q.optionAEditText
                        .getText()
                        .toString()
                        .trim();

        String optionB =
                q.optionBEditText
                        .getText()
                        .toString()
                        .trim();

        String optionC =
                q.optionCEditText
                        .getText()
                        .toString()
                        .trim();

        String optionD =
                q.optionDEditText
                        .getText()
                        .toString()
                        .trim();

        String correctAnswer;

        int checkedId =
                q.correctAnswerGroup
                        .getCheckedRadioButtonId();

        if (checkedId ==
                q.optionARadio.getId()) {

            correctAnswer = "A";

        } else if (checkedId ==
                q.optionBRadio.getId()) {

            correctAnswer = "B";

        } else if (checkedId ==
                q.optionCRadio.getId()) {

            correctAnswer = "C";

        } else {

            correctAnswer = "D";
        }

        // -----------------------------------------------------
        // CREATE QUESTION OBJECT
        // -----------------------------------------------------

        QuestionData questionData =
                new QuestionData();

        questionData.setQuestionId(
                UUID.randomUUID().toString()
        );

        questionData.setQuizId(
                quizId
        );

        questionData.setQuestion(
                question
        );

        questionData.setOptionA(
                optionA
        );

        questionData.setOptionB(
                optionB
        );

        questionData.setOptionC(
                optionC
        );

        questionData.setOptionD(
                optionD
        );

        questionData.setCorrectAnswer(
                correctAnswer
        );

        // -----------------------------------------------------
        // SAVE QUESTION
        // -----------------------------------------------------

        String questionId =
                questionData.getQuestionId();

        int currentQuestionNumber =
                questionIndex + 1;

        createQuizButton.setText(
                "SAVING QUESTION "
                        + currentQuestionNumber
                        + " / "
                        + questionViewsList.size()
        );

        firebaseHelper.addQuestion(
                questionId,
                questionData,
                task -> {

                    if (!task.isSuccessful()) {

                        Toast.makeText(
                                AddQuizActivity.this,
                                "Failed to save Question "
                                        + currentQuestionNumber
                                        + ": "
                                        + getErrorMessage(task),
                                Toast.LENGTH_LONG
                        ).show();

                        createQuizButton.setEnabled(true);

                        createQuizButton.setText(
                                "CREATE QUIZ"
                        );

                        return;
                    }

                    // Save next question
                    saveQuestions(
                            quizId,
                            questionIndex + 1
                    );
                }
        );
    }

    // ---------------------------------------------------------
    // ERROR MESSAGE
    // ---------------------------------------------------------

    private String getErrorMessage(
            com.google.android.gms.tasks.Task<?> task) {

        if (task.getException() != null) {

            return task.getException()
                    .getMessage();

        }

        return "Unknown error";
    }

    // ---------------------------------------------------------
    // QUESTION FIRESTORE DATA
    // ---------------------------------------------------------

    public static class QuestionData {

        private String questionId;
        private String quizId;
        private String question;

        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;

        private String correctAnswer;

        public QuestionData() {
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(
                String questionId) {

            this.questionId = questionId;
        }

        public String getQuizId() {
            return quizId;
        }

        public void setQuizId(
                String quizId) {

            this.quizId = quizId;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(
                String question) {

            this.question = question;
        }

        public String getOptionA() {
            return optionA;
        }

        public void setOptionA(
                String optionA) {

            this.optionA = optionA;
        }

        public String getOptionB() {
            return optionB;
        }

        public void setOptionB(
                String optionB) {

            this.optionB = optionB;
        }

        public String getOptionC() {
            return optionC;
        }

        public void setOptionC(
                String optionC) {

            this.optionC = optionC;
        }

        public String getOptionD() {
            return optionD;
        }

        public void setOptionD(
                String optionD) {

            this.optionD = optionD;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(
                String correctAnswer) {

            this.correctAnswer = correctAnswer;
        }
    }
}