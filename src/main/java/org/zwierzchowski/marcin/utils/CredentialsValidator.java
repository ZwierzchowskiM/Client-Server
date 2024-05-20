package org.zwierzchowski.marcin.utils;

import org.zwierzchowski.marcin.exception.InvalidCredentialsException;
import org.zwierzchowski.marcin.user.User;

import java.util.regex.Pattern;

public class CredentialsValidator {

  private static final String USERNAME_PATTERN = "^[A-Za-z][A-Za-z0-9]{1,28}$";
  private static final String PASSWORD_PATTERN = "^[A-Za-z]\\w{2,29}$";

  private CredentialsValidator() {}

  public static void validateUsername(String username) throws InvalidCredentialsException {
    if (username.length() < 3 || !Pattern.matches(USERNAME_PATTERN, username)) {
      throw new InvalidCredentialsException("Invalid username format");
    }
  }

  public static void validatePassword(String password) throws InvalidCredentialsException {
    if (password.length() < 3 || !Pattern.matches(PASSWORD_PATTERN, password)) {
      throw new InvalidCredentialsException("Invalid password format");
    }
  }

  public static void validateRole(String role) throws InvalidCredentialsException {
    boolean isValidRole = false;
    for (User.Role r : User.Role.values()) {
      if (r.name().equalsIgnoreCase(role)) {
        isValidRole = true;
        break;
      }
    }
    if (!isValidRole) {
      throw new InvalidCredentialsException("Invalid role");
    }
  }
}
