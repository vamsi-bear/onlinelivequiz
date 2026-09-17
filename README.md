# Online Live Quiz

A Firebase-powered Android quiz application developed using Java and XML. The application provides an interactive online assessment experience with student authentication, timed multiple-choice quizzes, automatic evaluation, attempt tracking, results, and real-time leaderboards.

---

## Overview

Online Live Quiz is an Android-based quiz platform designed to provide an interactive and real-time assessment experience.

Students can register and log in, view available quizzes, attempt timed multiple-choice questions, receive their scores, and view leaderboard results.

Administrators can create quizzes, add questions, configure quiz settings, and view leaderboard information.

The application uses Firebase services for authentication, cloud data storage, and real-time leaderboard updates.

---

## Features

### Student Features

- Student registration and login
- Firebase Authentication
- Student profile
- View available quizzes
- View quiz description
- View quiz duration
- View number of questions
- Multiple-choice questions
- Countdown timer
- Automatic quiz submission when the timer expires
- Manual quiz submission
- Automatic score calculation
- Attempt tracking
- Maximum attempt restriction
- Result display
- Real-time leaderboard
- Logout functionality

### Admin Features

- Admin authentication
- Admin dashboard
- Create quizzes
- Add quiz questions
- Configure quiz duration
- Configure total number of questions
- Configure maximum attempts
- View leaderboard
- View test duration information
- Logout functionality

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Android application development |
| XML | Android UI development |
| Android Studio | Development environment |
| Firebase Authentication | User registration and authentication |
| Cloud Firestore | Users, quizzes, questions and attempts |
| Firebase Realtime Database | Real-time leaderboard |
| RecyclerView | Dynamic quiz, question and leaderboard lists |
| Gradle | Build and dependency management |

---

## Architecture

The project follows a modular structure that separates activities, adapters, models, Firebase operations, and utility classes.

```text
com.example.onlinelivequiz
│
├── activities
│   ├── LoginActivity.java
│   ├── RegisterActivity.java
│   ├── MainActivity.java
│   ├── QuizActivity.java
│   ├── ResultActivity.java
│   ├── LeaderboardActivity.java
│   ├── ProfileActivity.java
│   ├── AdminActivity.java
│   ├── AddQuizActivity.java
│   └── AddQuestionActivity.java
│
├── adapters
│   ├── QuizAdapter.java
│   ├── QuestionAdapter.java
│   └── LeaderboardAdapter.java
│
├── firebase
│   └── FirebaseHelper.java
│
├── models
│   ├── User.java
│   ├── Quiz.java
│   ├── Question.java
│   └── Score.java
│
└── utils
    ├── TimerManager.java
    └── ValidationUtils.java
