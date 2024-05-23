package org.zwierzchowski.marcin.exception;

public class UserNotFoundException extends Exception {
    private String username;

    public UserNotFoundException(String message, String username) {
        super(message);
        this.username = username;
    }

    @Override
    public String getMessage() {
        return "User '" + username + "' not found";
    }
}