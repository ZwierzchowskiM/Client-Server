package org.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CredentialsValidator {

    private static final String USERNAME_PATTERN  = "^[A-Za-z]\\w{5,29}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";


    public static boolean validateUsername(String username) {

        if (username.length() < 3) {
            return false;
        }
        Pattern p = Pattern.compile(USERNAME_PATTERN);
        Matcher m = p.matcher(username);
        return m.matches();
    }

    public static boolean validatePassword(String password) {
        if (password.length() < 3) {
            return false;
        }
        Pattern p = Pattern.compile(PASSWORD_PATTERN);
        Matcher m = p.matcher(password);
        return m.matches();

    }
}
