package org.zwierzchowski.marcin.utils;

import org.zwierzchowski.marcin.exception.InvalidCredentialsException;
import org.zwierzchowski.marcin.exception.InvalidMessageException;

public class MessageValidator {

  private static final int MESSAGE_LENGTH = 255;

  private MessageValidator() {}

  public static void validateMessage(String content) throws InvalidMessageException {
    if (content == null || content.length()  > MESSAGE_LENGTH || content.isEmpty()) {
      throw new InvalidMessageException("Invalid message format");
    }
  }
}
