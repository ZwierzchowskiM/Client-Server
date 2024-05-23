package org.zwierzchowski.marcin.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.zwierzchowski.marcin.exception.InvalidCredentialsException;
import org.zwierzchowski.marcin.exception.InvalidMessageException;

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
        InvalidCredentialsException.class, () -> CredentialsValidator.validateUsername(""));
  }

  @Test
  @DisplayName("Validate short username")
  void shouldThrowExceptionForShortUsername() {
    assertThrows(
        InvalidCredentialsException.class, () -> CredentialsValidator.validateUsername("u"));
  }

  @Test
  @DisplayName("Validate long username")
  void shouldThrowExceptionForLongUsername() {
    String longUsername = "Loremipsumdolorsitametconsecteturadipiscingelit";
    assertThrows(
        InvalidCredentialsException.class,
        () -> CredentialsValidator.validateUsername(longUsername));
  }

  @Test
  @DisplayName("Validate username with white spaces")
  void shouldThrowExceptionForUsernameWithWhiteSpaces() {
    assertThrows(
        InvalidCredentialsException.class,
        () -> CredentialsValidator.validateUsername("test user"));
  }

  @Test
  @DisplayName("Validate username with special characters")
  void shouldThrowExceptionForUsernameWithSpecialCharacter() {
    assertThrows(
        InvalidCredentialsException.class,
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
        InvalidCredentialsException.class, () -> CredentialsValidator.validatePassword(""));
  }

  @Test
  @DisplayName("Validate short password")
  void shouldThrowExceptionForShortPassword() {
    assertThrows(
        InvalidCredentialsException.class, () -> CredentialsValidator.validatePassword("p"));
  }

  @Test
  @DisplayName("Validate long password")
  void shouldThrowExceptionForLongPassword() {
    String longPassword = "Loremipsumdolorsitametconsecteturadipiscingelit";
    assertThrows(
        InvalidCredentialsException.class,
        () -> CredentialsValidator.validatePassword(longPassword));
  }

  @Test
  @DisplayName("Validate password with white spaces")
  void shouldThrowExceptionForPasswordWithWhiteSpaces() {
    assertThrows(
        InvalidCredentialsException.class,
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
  void shouldNotThrowExceptionForInvalidRole() {
    assertThrows(
        InvalidCredentialsException.class, () -> CredentialsValidator.validateRole("invalidRole"));
  }
}
