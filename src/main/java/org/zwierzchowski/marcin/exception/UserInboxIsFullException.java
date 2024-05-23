package org.zwierzchowski.marcin.exception;

public class UserInboxIsFullException extends Exception {
  private String username;

  public UserInboxIsFullException(String message, String username) {
    super(message);
    this.username = username;
  }

  @Override
  public String getMessage() {
    return "Recipient '" + username + "' inbox is full";
  }
}
