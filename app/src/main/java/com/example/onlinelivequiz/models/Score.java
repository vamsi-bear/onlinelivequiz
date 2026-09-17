package com.example.onlinelivequiz.models;

public class Score {

    private String userId;
    private String userName;
    private int score;
    private long completionTime;

    public Score() {
        // Required by Firebase
    }

    public Score(
            String userId,
            String userName,
            int score,
            long completionTime) {

        this.userId = userId;
        this.userName = userName;
        this.score = score;
        this.completionTime = completionTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }
}