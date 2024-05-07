package org.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CredentialsValidator {

    String regexUsername = "^[A-Za-z]\\w{5,29}$";
    String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";


    public boolean validateUsername(String username) {

        if (username.length() < 3) {
            return false;
        }
        Pattern p = Pattern.compile(regexUsername);
        Matcher m = p.matcher(username);
        return m.matches();
    }

    public boolean validatePassword(String password) {
        if (password.length() < 3) {
            return false;
        }
        Pattern p = Pattern.compile(regexPassword);
        Matcher m = p.matcher(password);
        return m.matches();

    }
}
