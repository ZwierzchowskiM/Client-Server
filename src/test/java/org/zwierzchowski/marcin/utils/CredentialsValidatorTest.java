package org.zwierzchowski.marcin.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zwierzchowski.marcin.exception.InvalidCredentialsFormatException;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsValidatorTest {

  @Test
  @DisplayName("Validate correct username")
  void shouldNotThrowExceptionForValidUsername() {
    assertDoesNotThrow(() -> CredentialsValidator.validateUsername("user"));
  }

  @Test
  @DisplayName("Validate empty username")
  void shouldThrowExceptionForEmptyUsername() {
    assertThrows(
        InvalidCredentialsFormatException.class, () -> CredentialsValidator.validateUsername(""));
  }

  @Test
  @DisplayName("Validate short username")
  void shouldThrowExceptionForShortUsername() {
    assertThrows(
        InvalidCredentialsFormatException.class, () -> CredentialsValidator.validateUsername("u"));
  }

  @Test
  @DisplayName("Validate long username")
  void shouldThrowExceptionForLongUsername() {
    String longUsername = "Loremipsumdolorsitametconsecteturadipiscingelit";
    assertThrows(
        InvalidCredentialsFormatException.class,
        () -> CredentialsValidator.validateUsername(longUsername));
  }

  @Test
  @DisplayName("Validate username with white spaces")
  void shouldThrowExceptionForUsernameWithWhiteSpaces() {
    assertThrows(
        InvalidCredentialsFormatException.class,
        () -> CredentialsValidator.validateUsername("test user"));
  }

  @Test
  @DisplayName("Validate username with special characters")
  void shouldThrowExceptionForUsernameWithSpecialCharacter() {
    assertThrows(
        InvalidCredentialsFormatException.class,
        () -> CredentialsValidator.validateUsername("test$user"));
  }

  @Test
  @DisplayName("Validate correct password")
  void shouldNotThrowExceptionForValidPassword() {
    assertDoesNotThrow(() -> CredentialsValidator.validateUsername("pass"));
  }

  @Test
  @DisplayName("Validate empty password")
  void shouldThrowExceptionForEmptyPassword() {
    assertThrows(
        InvalidCredentialsFormatException.class, () -> CredentialsValidator.validatePassword(""));
  }

  @Test
  @DisplayName("Validate short password")
  void shouldThrowExceptionForShortPassword() {
    assertThrows(
        InvalidCredentialsFormatException.class, () -> CredentialsValidator.validatePassword("p"));
  }

  @Test
  @DisplayName("Validate long password")
  void shouldThrowExceptionForLongPassword() {
    String longPassword = "Loremipsumdolorsitametconsecteturadipiscingelit";
    assertThrows(
        InvalidCredentialsFormatException.class,
        () -> CredentialsValidator.validatePassword(longPassword));
  }

  @Test
  @DisplayName("Validate password with white spaces")
  void shouldThrowExceptionForPasswordWithWhiteSpaces() {
    assertThrows(
        InvalidCredentialsFormatException.class,
        () -> CredentialsValidator.validatePassword("test password"));
  }

  @Test
  @DisplayName("Validate correct role with lowercase")
  void shouldNotThrowExceptionForValidRoleLowercase() {
    assertDoesNotThrow(() -> CredentialsValidator.validateRole("admin"));
  }

  @Test
  @DisplayName("Validate correct role with uppercase")
  void shouldNotThrowExceptionForValidRoleUppercase() {
    assertDoesNotThrow(() -> CredentialsValidator.validateRole("ADMIN"));
  }

  @Test
  @DisplayName("Validate incorrect role")
  void shouldThrowExceptionForInvalidRole() {
    assertThrows(
        InvalidCredentialsFormatException.class, () -> CredentialsValidator.validateRole("invalidRole"));
  }
}
