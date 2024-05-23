package org.zwierzchowski.marcin.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zwierzchowski.marcin.exception.InvalidMessageException;

import static org.junit.jupiter.api.Assertions.*;

class MessageValidatorTest {

  @Test
  @DisplayName("Validate correct message")
  void shouldNotThrowExceptionForValidMessage() {
    assertDoesNotThrow(() -> MessageValidator.validateMessage("Test message"));
  }

  @Test
  @DisplayName("Validate empty message")
  void shouldThrowExceptionForEmptyMessage() {
    assertThrows(InvalidMessageException.class, () -> MessageValidator.validateMessage(""));
  }

  @Test
  @DisplayName("Validate long message")
  void shouldThrowExceptionForLongMessage() {
    String longMessage =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer ut ligula vel mi dapibus commodo vel nec velit. Nullam vitae purus in arcu vehicula viverra. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer ut ligula vel mi dapibus commodo vel nec velit. Nullam vitae purus in arcu vehicula viverra.";
    assertThrows(
        InvalidMessageException.class, () -> MessageValidator.validateMessage(longMessage));
  }

  @Test
  @DisplayName("Validate null message")
  void shouldThrowExceptionForNullMessage() {
    assertThrows(InvalidMessageException.class, () -> MessageValidator.validateMessage(null));
  }
}
