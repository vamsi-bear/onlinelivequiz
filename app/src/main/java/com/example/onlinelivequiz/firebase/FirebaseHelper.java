package com.example.onlinelivequiz.firebase;

import android.util.Log;

import com.example.onlinelivequiz.models.Score;
import com.example.onlinelivequiz.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

public class FirebaseHelper {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private FirebaseDatabase realtimeDatabase;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public FirebaseHelper() {

        auth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        try {

            realtimeDatabase =
                    FirebaseDatabase.getInstance();

        } catch (Exception e) {

            Log.e(
                    "FirebaseHelper",
                    "Realtime Database initialization failed: "
                            + e.getMessage()
            );

            realtimeDatabase = null;
        }
    }

    // =====================================================
    // AUTHENTICATION
    // =====================================================

    public void registerUser(
            String email,
            String password,
            OnCompleteListener<AuthResult> listener) {

        auth.createUserWithEmailAndPassword(
                email,
                password
        ).addOnCompleteListener(listener);
    }

    public void loginUser(
            String email,
            String password,
            OnCompleteListener<AuthResult> listener) {

        auth.signInWithEmailAndPassword(
                email,
                password
        ).addOnCompleteListener(listener);
    }

    public void logout() {

        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {

        return auth.getCurrentUser();
    }

    public String getCurrentUserId() {

        FirebaseUser user =
                auth.getCurrentUser();

        return user != null
                ? user.getUid()
                : null;
    }

    public boolean isUserLoggedIn() {

        return auth.getCurrentUser() != null;
    }

    // =====================================================
    // USER PROFILE
    // =====================================================

    public void createUserProfile(
            String userId,
            String name,
            String email,
            OnCompleteListener<Void> listener) {

        User user = new User();

        user.setUserId(userId);
        user.setName(name);
        user.setEmail(email);

        // New users are students by default
        user.setRole("student");

        firestore
                .collection("users")
                .document(userId)
                .set(user)
                .addOnCompleteListener(listener);
    }

    public DocumentReference getUserProfile(
            String userId) {

        return firestore
                .collection("users")
                .document(userId);
    }

    public void getUserProfile(
            String userId,
            OnCompleteListener<DocumentSnapshot> listener) {

        firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(listener);
    }

    // =====================================================
    // QUIZZES
    // =====================================================

    public CollectionReference getQuizzes() {

        return firestore.collection("quizzes");
    }

    public void getAllQuizzes(
            OnCompleteListener<QuerySnapshot> listener) {

        firestore
                .collection("quizzes")
                .get()
                .addOnCompleteListener(listener);
    }

    public DocumentReference getQuiz(
            String quizId) {

        return firestore
                .collection("quizzes")
                .document(quizId);
    }

    public void getQuizDetails(
            String quizId,
            OnCompleteListener<DocumentSnapshot> listener) {

        firestore
                .collection("quizzes")
                .document(quizId)
                .get()
                .addOnCompleteListener(listener);
    }

    // =====================================================
    // QUESTIONS
    // =====================================================

    public void getQuestions(
            String quizId,
            OnCompleteListener<QuerySnapshot> listener) {

        firestore
                .collection("questions")
                .whereEqualTo("quizId", quizId)
                .get()
                .addOnCompleteListener(listener);
    }

    // =====================================================
    // REALTIME DATABASE
    // LEADERBOARD
    // =====================================================

    public DatabaseReference getLeaderboardReference(
            String quizId) {

        if (realtimeDatabase == null) {
            return null;
        }

        return realtimeDatabase
                .getReference("leaderboards")
                .child(quizId);
    }

    // =====================================================
    // SUBMIT BEST SCORE
    // =====================================================

    public void submitScore(
            String quizId,
            Score newScore,
            OnCompleteListener<Void> listener) {

        // -------------------------------------------------
        // VALIDATION
        // -------------------------------------------------

        if (realtimeDatabase == null
                || quizId == null
                || quizId.isEmpty()
                || newScore == null
                || newScore.getUserId() == null
                || newScore.getUserId().isEmpty()) {

            listener.onComplete(
                    Tasks.forException(
                            new Exception(
                                    "Invalid leaderboard data"
                            )
                    )
            );

            return;
        }

        // -------------------------------------------------
        // STUDENT'S LEADERBOARD LOCATION
        // -------------------------------------------------

        DatabaseReference scoreReference =
                realtimeDatabase
                        .getReference("leaderboards")
                        .child(quizId)
                        .child(newScore.getUserId());

        // -------------------------------------------------
        // GET EXISTING SCORE
        // -------------------------------------------------

        scoreReference
                .get()
                .addOnCompleteListener(task -> {

                    // -------------------------------------
                    // FAILED TO READ
                    // -------------------------------------

                    if (!task.isSuccessful()) {

                        listener.onComplete(
                                Tasks.forException(
                                        task.getException() != null
                                                ? task.getException()
                                                : new Exception(
                                                "Failed to read leaderboard"
                                        )
                                )
                        );

                        return;
                    }

                    DataSnapshot snapshot =
                            task.getResult();

                    // -------------------------------------
                    // FIRST ATTEMPT
                    // -------------------------------------

                    if (snapshot == null
                            || !snapshot.exists()) {

                        scoreReference
                                .setValue(newScore)
                                .addOnCompleteListener(
                                        listener
                                );

                        return;
                    }

                    // -------------------------------------
                    // READ EXISTING SCORE
                    // -------------------------------------

                    Score existingScore =
                            snapshot.getValue(
                                    Score.class
                            );

                    // If existing data is invalid,
                    // replace it with the new score.
                    if (existingScore == null) {

                        scoreReference
                                .setValue(newScore)
                                .addOnCompleteListener(
                                        listener
                                );

                        return;
                    }

                    // -------------------------------------
                    // DETERMINE WHICH SCORE IS BETTER
                    // -------------------------------------

                    boolean newAttemptIsBetter = false;

                    /*
                     * Rule 1:
                     *
                     * Higher score is better.
                     */

                    if (newScore.getScore()
                            > existingScore.getScore()) {

                        newAttemptIsBetter = true;
                    }

                    /*
                     * Rule 2:
                     *
                     * If scores are equal,
                     * faster completion time is better.
                     */

                    else if (
                            newScore.getScore()
                                    == existingScore.getScore()
                                    &&
                                    newScore.getCompletionTime()
                                            < existingScore
                                            .getCompletionTime()
                    ) {

                        newAttemptIsBetter = true;
                    }

                    // -------------------------------------
                    // UPDATE LEADERBOARD IF BETTER
                    // -------------------------------------

                    if (newAttemptIsBetter) {

                        scoreReference
                                .setValue(newScore)
                                .addOnCompleteListener(
                                        listener
                                );

                    } else {

                        /*
                         * Existing score is already better.
                         *
                         * No database update is required.
                         *
                         * The attempt itself is still saved
                         * separately in Firestore.
                         */

                        listener.onComplete(
                                Tasks.forResult(null)
                        );
                    }
                });
    }

    // =====================================================
    // REALTIME LEADERBOARD LISTENER
    // =====================================================

    public void listenToLeaderboard(
            String quizId,
            ValueEventListener listener) {

        if (realtimeDatabase == null) {
            return;
        }

        realtimeDatabase
                .getReference("leaderboards")
                .child(quizId)
                .addValueEventListener(listener);
    }

    // =====================================================
    // REMOVE LEADERBOARD LISTENER
    // =====================================================

    public void removeLeaderboardListener(
            String quizId,
            ValueEventListener listener) {

        if (realtimeDatabase == null) {
            return;
        }

        realtimeDatabase
                .getReference("leaderboards")
                .child(quizId)
                .removeEventListener(listener);
    }

    // =====================================================
    // GET LEADERBOARD ONCE
    // =====================================================

    public void getLeaderboardOnce(
            String quizId,
            OnCompleteListener<DataSnapshot> listener) {

        if (realtimeDatabase == null) {

            listener.onComplete(
                    Tasks.forException(
                            new Exception(
                                    "Realtime Database unavailable"
                            )
                    )
            );

            return;
        }

        realtimeDatabase
                .getReference("leaderboards")
                .child(quizId)
                .get()
                .addOnCompleteListener(listener);
    }

    // =====================================================
    // ADMIN QUIZ OPERATIONS
    // =====================================================

    public void deleteQuiz(
            String quizId,
            OnCompleteListener<Void> listener) {

        if (quizId == null
                || quizId.isEmpty()) {

            listener.onComplete(
                    Tasks.forException(
                            new Exception(
                                    "Invalid quiz ID"
                            )
                    )
            );

            return;
        }

        firestore
                .collection("quizzes")
                .document(quizId)
                .delete()
                .addOnCompleteListener(listener);
    }

    public void addQuiz(
            String quizId,
            Object quiz,
            OnCompleteListener<Void> listener) {

        firestore
                .collection("quizzes")
                .document(quizId)
                .set(quiz)
                .addOnCompleteListener(listener);
    }

    public void addQuestion(
            String questionId,
            Object question,
            OnCompleteListener<Void> listener) {

        firestore
                .collection("questions")
                .document(questionId)
                .set(question)
                .addOnCompleteListener(listener);
    }

    // =====================================================
    // STUDENT ATTEMPTS
    // =====================================================

    /*
     * Database structure:
     *
     * quizAttempts
     *     └── quizId
     *          └── students
     *               └── userId
     *                    ├── attemptCount
     *                    └── attempts
     *                         ├── attempt1
     *                         ├── attempt2
     *                         └── attempt3
     */

    // =====================================================
    // GET STUDENT ATTEMPT DOCUMENT
    // =====================================================

    public DocumentReference getStudentAttempts(
            String quizId,
            String userId) {

        return firestore
                .collection("quizAttempts")
                .document(quizId)
                .collection("students")
                .document(userId);
    }

    // =====================================================
    // GET STUDENT ATTEMPT DOCUMENT
    // =====================================================

    public void getStudentAttempts(
            String quizId,
            String userId,
            OnCompleteListener<DocumentSnapshot> listener) {

        getStudentAttempts(
                quizId,
                userId
        )
                .get()
                .addOnCompleteListener(listener);
    }

    // =====================================================
    // GET ATTEMPT COUNT
    // =====================================================

    public void getAttemptCount(
            String quizId,
            String userId,
            OnCompleteListener<Integer> listener) {

        getStudentAttempts(
                quizId,
                userId
        )
                .get()
                .addOnCompleteListener(task -> {

                    // -------------------------------------
                    // DOCUMENT DOES NOT EXIST
                    // -------------------------------------

                    if (!task.isSuccessful()
                            || task.getResult() == null
                            || !task.getResult().exists()) {

                        listener.onComplete(
                                Tasks.forResult(0)
                        );

                        return;
                    }

                    // -------------------------------------
                    // READ ATTEMPT COUNT
                    // -------------------------------------

                    DocumentSnapshot document =
                            task.getResult();

                    Long attemptCount =
                            document.getLong(
                                    "attemptCount"
                            );

                    int count =
                            attemptCount != null
                                    ? attemptCount.intValue()
                                    : 0;

                    listener.onComplete(
                            Tasks.forResult(count)
                    );
                });
    }

    // =====================================================
    // SAVE STUDENT ATTEMPT
    // =====================================================

    public void saveAttempt(
            String quizId,
            String userId,
            int attemptNumber,
            int score,
            long completionTime,
            OnCompleteListener<Void> listener) {

        // -------------------------------------------------
        // VALIDATION
        // -------------------------------------------------

        if (quizId == null
                || quizId.isEmpty()
                || userId == null
                || userId.isEmpty()
                || attemptNumber <= 0) {

            listener.onComplete(
                    Tasks.forException(
                            new Exception(
                                    "Invalid attempt data"
                            )
                    )
            );

            return;
        }

        // -------------------------------------------------
        // STUDENT DOCUMENT
        // -------------------------------------------------

        DocumentReference studentRef =
                getStudentAttempts(
                        quizId,
                        userId
                );

        // -------------------------------------------------
        // ATTEMPT DOCUMENT ID
        // -------------------------------------------------

        String attemptId =
                "attempt" + attemptNumber;

        // -------------------------------------------------
        // ATTEMPT DATA
        // -------------------------------------------------

        AttemptData attemptData =
                new AttemptData(
                        attemptNumber,
                        score,
                        completionTime,
                        System.currentTimeMillis()
                );

        // -------------------------------------------------
        // SAVE ATTEMPT
        // -------------------------------------------------

        studentRef
                .collection("attempts")
                .document(attemptId)
                .set(attemptData)
                .addOnCompleteListener(task -> {

                    // -------------------------------------
                    // ATTEMPT SAVE FAILED
                    // -------------------------------------

                    if (!task.isSuccessful()) {

                        listener.onComplete(task);

                        return;
                    }

                    // -------------------------------------
                    // UPDATE ATTEMPT COUNT
                    // -------------------------------------

                    studentRef
                            .set(
                                    new AttemptCount(
                                            attemptNumber
                                    ),
                                    SetOptions.merge()
                            )
                            .addOnCompleteListener(
                                    listener
                            );
                });
    }

    // =====================================================
    // ATTEMPT COUNT MODEL
    // =====================================================

    public static class AttemptCount {

        private int attemptCount;

        // Required by Firestore
        public AttemptCount() {
        }

        public AttemptCount(
                int attemptCount) {

            this.attemptCount =
                    attemptCount;
        }

        public int getAttemptCount() {

            return attemptCount;
        }

        public void setAttemptCount(
                int attemptCount) {

            this.attemptCount =
                    attemptCount;
        }
    }

    // =====================================================
    // ATTEMPT DATA MODEL
    // =====================================================

    public static class AttemptData {

        private int attemptNumber;
        private int score;
        private long completionTime;
        private long submittedAt;

        // Required by Firestore
        public AttemptData() {
        }

        public AttemptData(
                int attemptNumber,
                int score,
                long completionTime,
                long submittedAt) {

            this.attemptNumber =
                    attemptNumber;

            this.score =
                    score;

            this.completionTime =
                    completionTime;

            this.submittedAt =
                    submittedAt;
        }

        public int getAttemptNumber() {

            return attemptNumber;
        }

        public void setAttemptNumber(
                int attemptNumber) {

            this.attemptNumber =
                    attemptNumber;
        }

        public int getScore() {

            return score;
        }

        public void setScore(
                int score) {

            this.score =
                    score;
        }

        public long getCompletionTime() {

            return completionTime;
        }

        public void setCompletionTime(
                long completionTime) {

            this.completionTime =
                    completionTime;
        }

        public long getSubmittedAt() {

            return submittedAt;
        }

        public void setSubmittedAt(
                long submittedAt) {

            this.submittedAt =
                    submittedAt;
        }
    }
}