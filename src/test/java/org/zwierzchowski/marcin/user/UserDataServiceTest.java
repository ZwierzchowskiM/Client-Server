package org.zwierzchowski.marcin.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.zwierzchowski.marcin.exception.InvalidCredentialsFormatException;
import org.zwierzchowski.marcin.exception.InvalidPasswordException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.utils.FileService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserDataServiceTest {

  private UserDataService userDataService;
  Map<String, User> users;
  User testUser;
  String username;
  String password;
  String hashedPassword;
  String role;

  @BeforeEach
  void setUp() {
    userDataService = new UserDataService();
    users = new HashMap<>();
    username = "john";
    password = "pass";
    hashedPassword = BCrypt.hashpw("pass", BCrypt.gensalt());
    testUser = new StandardUser(username, hashedPassword);
  }

  @Test
  @DisplayName("Should return standard user")
  void shouldReturnUserWithStandardRole() throws IOException, InvalidCredentialsFormatException {

    role = "user";

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);

      User newUser = userDataService.addUser(username, password, role);
      assertNotNull(newUser);
      assertTrue(newUser instanceof StandardUser);
      assertEquals(username, newUser.getUsername());
      assertTrue(BCrypt.checkpw(password, newUser.getPassword()));
    }
  }

  @Test
  @DisplayName("Should return standard user")
  void shouldReturnUserWithAdminRole() throws IOException, InvalidCredentialsFormatException {

    role = "admin";

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);

      User newUser = userDataService.addUser(username, password, role);
      assertNotNull(newUser);
      assertTrue(newUser instanceof Admin);
      assertEquals(username, newUser.getUsername());
      assertTrue(BCrypt.checkpw(password, newUser.getPassword()));
    }
  }

  @Test
  @DisplayName("Should throw Invalid Credentials Exception when invalid role")
  void shouldThrowInvalidCredentialsExceptionWhenInvalidRole() throws IOException {

    role = "invalidRole";

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);

      assertThrows(
          InvalidCredentialsFormatException.class,
          () -> userDataService.addUser(username, password, role));
    }
  }

  @Test
  @DisplayName("Should return testUser when username is valid")
  void shouldReturnUserWhenValidUsername()
      throws IOException, InvalidCredentialsFormatException, UserNotFoundException {

    users.put(username, testUser);

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);

      User retrievedUser = userDataService.getUser(username);
      assertNotNull(retrievedUser);
      assertEquals(username, retrievedUser.getUsername());
      assertEquals(testUser, retrievedUser);
    }
  }

  @Test
  @DisplayName("Should throws UserNotFoundException when username is invalid")
  void shouldThrowsUserNotFoundExceptionUserWhenInvalidUsername()
      throws IOException, UserNotFoundException {

    users.put(username, testUser);

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);

      assertThrows(UserNotFoundException.class, () -> userDataService.getUser("invalidUsername"));
    }
  }

  @Test
  @DisplayName("should remove user from database when exists")
  void shouldRemoveUserFromDatabaseWhenExists() throws UserNotFoundException, IOException {

    users.put(username, testUser);

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
      userDataService.deleteUser(username);
      assertFalse(users.containsKey(username));
    }
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when username is invalid")
  void shouldThrowUserNotFoundExceptionWhenInvalidUsername() {
    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);

      String invalidUsername = "invalidUser";
      UserNotFoundException exception =
          assertThrows(
              UserNotFoundException.class, () -> userDataService.deleteUser(invalidUsername));
    }
  }

  @Test
  @DisplayName("Should return true for valid credentials")
  void shouldReturnTrueForValidCredentials()
      throws IOException, UserNotFoundException, InvalidPasswordException {
    users.put(username, testUser);

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);

      boolean isValid = userDataService.isValidCredentials(username, password);
      assertTrue(isValid);
    }
  }

  @Test
  @DisplayName("Should throw InvalidPasswordException for invalid password")
  void shouldThrowInvalidPasswordExceptionForInvalidPassword() throws IOException {
    users.put(username, testUser);

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);

      InvalidPasswordException exception =
          assertThrows(
              InvalidPasswordException.class,
              () -> userDataService.isValidCredentials(username, "wrongpassword"));
    }
  }

  @Test
  @DisplayName("Should throw UserNotFoundException for invalid username")
  void shouldThrowUserNotFoundExceptionForInvalidUsername() throws IOException {
    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);

      String invalidUsername = "invaliduser";
      UserNotFoundException exception =
          assertThrows(
              UserNotFoundException.class,
              () -> userDataService.isValidCredentials(invalidUsername, password));
    }
  }
}
