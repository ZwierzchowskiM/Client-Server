package org.zwierzchowski.marcin.utils;

import org.zwierzchowski.marcin.user.User;

import java.util.regex.Pattern;

public class CredentialsValidator {

    private static final String USERNAME_PATTERN = "^[A-Za-z]\\w{2,29}$";
    private static final String PASSWORD_PATTERN = "^[A-Za-z]\\w{2,29}$";

    private CredentialsValidator() {
    }

    public static void validateUsername(String username) {
        if (username.length() < 3 || !Pattern.matches(USERNAME_PATTERN, username)) {
            throw new IllegalArgumentException("Invalid username format");
        }
    }

    public static void validatePassword(String password) {
        if (password.length() < 3 || !Pattern.matches(PASSWORD_PATTERN, password)) {
            throw new IllegalArgumentException("Invalid password format");
        }
    }

    public static void validateRole(String role) {
        boolean isValidRole = false;
        for (User.Role r : User.Role.values()) {
            if (r.name().equalsIgnoreCase(role)) {
                isValidRole = true;
                break;
            }
        }
        if (!isValidRole) {
            throw new IllegalArgumentException("Invalid role");
        }
    }
}
