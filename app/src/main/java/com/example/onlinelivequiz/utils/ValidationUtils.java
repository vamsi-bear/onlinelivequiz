package com.example.onlinelivequiz.utils;

import android.util.Patterns;

public class ValidationUtils {

    private ValidationUtils() {
        // Prevent object creation
    }

    public static boolean isEmpty(String value) {

        return value == null ||
                value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {

        return email != null &&
                Patterns.EMAIL_ADDRESS
                        .matcher(email)
                        .matches();
    }

    public static boolean isValidPassword(
            String password) {

        return password != null &&
                password.length() >= 6;
    }

    public static boolean passwordsMatch(
            String password,
            String confirmPassword) {

        return password != null &&
                password.equals(confirmPassword);
    }
}