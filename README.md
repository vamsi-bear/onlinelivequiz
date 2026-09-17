# Online Live Quiz

A Firebase-powered Android quiz application developed using Java and XML. The application provides a complete quiz experience for students and administrators, including authentication, timed quizzes, automatic evaluation, attempt tracking, and real-time leaderboards.

---

## Overview

Online Live Quiz is an Android-based quiz platform designed to provide an interactive and real-time assessment experience.

Students can register, log in, view available quizzes, attempt timed multiple-choice questions, receive their scores, and view leaderboard results.

Administrators can create quizzes and monitor leaderboard information.

The application uses Firebase services for authentication, cloud data storage, and real-time leaderboard updates.

---

## Features

### Student Features

- Student registration and login
- Firebase Authentication
- View available quizzes
- View quiz description and duration
- Multiple-choice questions
- Countdown timer
- Automatic quiz submission when the timer expires
- Automatic score calculation
- Attempt tracking
- Maximum attempt restriction
- Result display
- Real-time leaderboard
- Student profile
- Logout functionality

### Admin Features

- Admin dashboard
- Admin authentication
- Create quizzes
- Configure quiz duration
- Configure total questions
- Configure maximum attempts
- View leaderboard
- View test duration information
- Logout functionality

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Application development |
| XML | Android UI development |
| Android Studio | Development environment |
| Firebase Authentication | User authentication |
| Cloud Firestore | Quiz, question, user and attempt data |
| Firebase Realtime Database | Real-time leaderboard |
| RecyclerView | Dynamic quiz and leaderboard lists |
| Gradle | Build and dependency management |

---

## Architecture

The project follows a modular structure separating activities, adapters, models, Firebase operations, and utility classes.

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
