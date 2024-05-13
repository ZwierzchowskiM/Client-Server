package org.zwierzchowski.marcin.utils;

import java.util.List;
import java.util.regex.Pattern;

public class CredentialsValidator {

    private static final String USERNAME_PATTERN  = "^[A-Za-z]\\w{4,29}$";
    private static final String PASSWORD_PATTERN = "^[A-Za-z]\\w{4,29}$";
    private static final List<String> ROLES = List.of("admin", "standard");


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
        if (!ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role");
        }
    }
}
