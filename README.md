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


🔥 Firebase Integration

The application uses three major Firebase services.

Firebase Authentication

Firebase Authentication manages:

Student registration
Student login
Admin login
User session
Logout
Cloud Firestore

Cloud Firestore stores the application's main data.

Collections used:

users
quizzes
questions
quizAttempts
Users
users
 └── userId
      ├── userId
      ├── name
      ├── email
      └── role
Quizzes
quizzes
 └── quizId
      ├── quizId
      ├── title
      ├── description
      ├── duration
      ├── totalQuestions
      └── maxAttempts
Questions
questions
 └── questionId
      ├── questionId
      ├── quizId
      ├── question
      ├── optionA
      ├── optionB
      ├── optionC
      ├── optionD
      └── correctAnswer
Quiz Attempts
quizAttempts
 └── quizId
      └── students
           └── userId
                ├── attemptCount
                └── attempts
                     ├── attempt1
                     ├── attempt2
                     └── ...
⚡ Firebase Realtime Database

The Realtime Database is used for leaderboard information.

leaderboards
 └── quizId
      └── userId
           ├── userId
           ├── userName
           ├── score
           └── completionTime

This allows leaderboard information to be updated and observed in real time.

🔄 Application Flow
                         ┌─────────────────┐
                         │   Launch App    │
                         └────────┬────────┘
                                  │
                         ┌────────▼────────┐
                         │ Login / Register│
                         └────────┬────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
             ┌──────▼──────┐             ┌──────▼──────┐
             │   Student   │             │    Admin    │
             └──────┬──────┘             └──────┬──────┘
                    │                           │
             ┌──────▼──────┐             ┌──────▼──────┐
             │ Quiz List   │             │Admin Dashboard│
             └──────┬──────┘             └──────┬──────┘
                    │                           │
             ┌──────▼──────┐             ┌──────▼──────┐
             │ Start Quiz  │             │ Create Quiz │
             └──────┬──────┘             └──────┬──────┘
                    │                           │
             ┌──────▼──────┐             ┌──────▼──────┐
             │ Timed Quiz  │             │Add Questions│
             └──────┬──────┘             └─────────────┘
                    │
             ┌──────▼──────┐
             │Submit Quiz  │
             └──────┬──────┘
                    │
             ┌──────▼──────┐
             │Calculate    │
             │Score        │
             └──────┬──────┘
                    │
             ┌──────▼──────┐
             │   Result    │
             └──────┬──────┘
                    │
             ┌──────▼─────────┐
             │  Leaderboard   │
             └────────────────┘
⏱️ Quiz Timer

Each quiz has a configured duration.

The timer:

Starts when the quiz begins
Displays the remaining time
Updates continuously
Automatically submits the quiz when time reaches zero
Prevents duplicate submissions

Example:

10:00
09:59
09:58
...
00:02
00:01
00:00

When the timer reaches 00:00, the quiz is automatically submitted.

📝 Quiz Evaluation

When a student submits a quiz, the application performs the following operations:

Collects the selected answers.
Compares selected answers with the correct answers.
Calculates the total score.
Calculates completion time.
Saves the student's attempt.
Updates the leaderboard.
Opens the result screen.

The application also prevents duplicate submissions after a quiz has already been submitted.

🏆 Leaderboard

The leaderboard displays student performance for a particular quiz.

Leaderboard information includes:

Student name
Score
Completion time

When a student attempts the same quiz multiple times, the better score is retained.

If two attempts have the same score, completion time is used as the secondary comparison.

🔢 Attempt Management

The application supports maximum quiz attempts.

For example:

Maximum Attempts: 2

Attempt 1 → Score: 6/10
Attempt 2 → Score: 8/10

The student's attempt count is stored in Firestore.

Each attempt contains:

attemptNumber
score
completionTime
submittedAt
👤 User Roles

The application supports different user roles.

Student
   │
   ├── View Quizzes
   ├── Attempt Quiz
   ├── View Results
   ├── View Leaderboard
   └── View Profile

Admin
   │
   ├── Admin Dashboard
   ├── Create Quiz
   ├── Add Questions
   └── View Leaderboard
🔐 Security

Firebase Authentication is used to authenticate application users.

Firestore security rules are used to restrict access to user-specific information and quiz attempts.

The application also separates student and administrator functionality using roles.

For a production deployment, role-based authorization should additionally be enforced through secure Firebase rules/server-side validation rather than relying only on client-side UI restrictions.

📂 Main Components
Activities
Activity	Responsibility
LoginActivity	User login
RegisterActivity	Student registration
MainActivity	Student dashboard
QuizActivity	Quiz attempt and timer
ResultActivity	Display quiz result
LeaderboardActivity	Display leaderboard
ProfileActivity	Display user profile
AdminActivity	Admin dashboard
AddQuizActivity	Create a quiz
AddQuestionActivity	Add quiz questions
Adapters
QuizAdapter

Displays available quizzes in RecyclerView.

QuestionAdapter

Displays quiz questions and allows students to select answers.

LeaderboardAdapter

Displays leaderboard records.

Models
User

Represents application users.

Quiz

Represents quiz information.

Question

Represents individual multiple-choice questions.

Score

Represents student leaderboard performance.

Utilities
TimerManager

Responsible for timer-related functionality.

ValidationUtils

Provides input validation functionality.

📱 User Interface

The application contains dedicated screens for:

Login
Registration
Student Dashboard
Quiz List
Quiz Attempt
Result
Leaderboard
Profile
Admin Dashboard
Add Quiz
Add Question

RecyclerView is used for dynamic lists such as quizzes, questions and leaderboard entries.

🚀 Getting Started
Prerequisites

Install the following:

Android Studio
Java/JDK
Android SDK
Gradle
Firebase account
Android emulator or physical Android device
📥 Clone the Repository
git clone https://github.com/vamsi-bear/onlinelivequiz.git

Then open the project in Android Studio.

🔥 Firebase Configuration

Create a Firebase project and enable:

Firebase Authentication
Cloud Firestore
Firebase Realtime Database

Add the Android Firebase configuration file:

google-services.json

Place it inside:

app/google-services.json

Configure the required Firestore collections and Realtime Database structure.

▶️ Running the Application
Clone the repository.
Open the project in Android Studio.
Configure Firebase.
Add google-services.json.
Allow Gradle to synchronize.
Connect an Android device or start an emulator.
Click Run in Android Studio.
Register or log in.
Start a quiz.
📊 Example Quiz
Title:
Python & AI/ML

Description:
Test your Python and machine learning knowledge.

Duration:
10 minutes

Total Questions:
10

Maximum Attempts:
2
🎯 Learning Outcomes

This project helped implement and understand:

Android application development
Java programming
XML UI development
Firebase Authentication
Cloud Firestore
Firebase Realtime Database
CRUD operations
RecyclerView
Activity navigation
Intent-based data passing
Countdown timers
Multiple-choice quiz systems
Automatic score calculation
Attempt management
Real-time data synchronization
Leaderboard implementation
Firebase security rules
Role-based application flow
🔮 Future Enhancements

Possible future improvements include:

Quiz categories
Question randomization
Question difficulty levels
Negative marking
Quiz scheduling
Push notifications
Detailed performance analytics
Admin user management
Offline quiz support
Advanced role-based security
Profile customization
Quiz history and statistics
📸 Screenshots

Add screenshots of the application here.

Recommended screenshots:

Login Screen
Registration Screen
Student Dashboard
Quiz List
Quiz Attempt Screen
Result Screen
Leaderboard
Profile Screen
Admin Dashboard
Add Quiz Screen
Add Question Screen

Example:

## Screenshots

### Login
![Login Screen](screenshots/login.png)

### Student Dashboard
![Student Dashboard](screenshots/dashboard.png)

### Quiz
![Quiz Screen](screenshots/quiz.png)

### Result
![Result Screen](screenshots/result.png)

### Leaderboard
![Leaderboard](screenshots/leaderboard.png)

### Admin Dashboard
![Admin Dashboard](screenshots/admin.png)
📌 Project Information

Project Name: Online Live Quiz

Platform: Android

Language: Java

UI: XML

Backend: Firebase

Authentication: Firebase Authentication

Database: Cloud Firestore + Firebase Realtime Database

Development Tool: Android Studio

Package Name: com.example.onlinelivequiz

👨‍💻 Author
Vamsi Anga

Computer Science Engineering Student

GitHub: https://github.com/vamsi-bear

📄 License

This project was developed for educational and academic purposes.
