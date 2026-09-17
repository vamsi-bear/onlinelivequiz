package com.example.onlinelivequiz.models;

public class Quiz {

    private String quizId;
    private String title;
    private String description;
    private int duration;
    private int totalQuestions;

    // Number of attempts allowed by admin
    private int maxAttempts;

    public Quiz() {
        // Required by Firebase Firestore
    }

    public Quiz(
            String quizId,
            String title,
            String description,
            int duration,
            int totalQuestions,
            int maxAttempts) {

        this.quizId = quizId;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.totalQuestions = totalQuestions;
        this.maxAttempts = maxAttempts;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}