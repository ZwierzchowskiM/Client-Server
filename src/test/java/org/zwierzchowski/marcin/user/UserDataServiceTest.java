package org.zwierzchowski.marcin.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.*;
import org.zwierzchowski.marcin.exception.DatabaseConnectionException;
import org.zwierzchowski.marcin.exception.InvalidCredentialsFormatException;
import org.zwierzchowski.marcin.exception.InvalidPasswordException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;

class UserDataServiceTest {

  private Map<String, User> users;
  private User testUser;
  private String username;
  private String password;
  private String hashedPassword;
  private String role;

  @Mock private UserRepository userRepository;

  @InjectMocks private UserDataService userDataService;

  @BeforeEach
  void setUp() throws DatabaseConnectionException {

    MockitoAnnotations.openMocks(this);
    users = new HashMap<>();
    username = "john";
    password = "pass";
    hashedPassword = BCrypt.hashpw("pass", BCrypt.gensalt());
    testUser = new StandardUser(username, hashedPassword);
  }

  @Test
  @DisplayName("Should return standard user")
  void shouldReturnUserWithStandardRole() throws InvalidCredentialsFormatException {

    role = "user";

    User result = userDataService.addUser(username, password, role);

    assertNotNull(result);
    assertEquals(username, result.getUsername());
    assertTrue(result instanceof StandardUser);
    verify(userRepository, times(1)).saveUser(any(User.class));
  }

  @Test
  @DisplayName("Should return admin user")
  void shouldReturnUserWithAdminRole() throws InvalidCredentialsFormatException {

    role = "admin";

    User newUser = userDataService.addUser(username, password, role);

    assertNotNull(newUser);
    assertTrue(newUser instanceof Admin);
    assertEquals(username, newUser.getUsername());
    assertTrue(BCrypt.checkpw(password, newUser.getPassword()));
  }

  @Test
  void shouldThrowInvalidCredentialsFormatExceptionWhenInvalidRole() {

    String role = "invalidRole";

    assertThrows(
        InvalidCredentialsFormatException.class,
        () -> {
          userDataService.addUser(username, password, role);
        });
  }

  @Test
  @DisplayName("Should return testUser when username is valid")
  void shouldReturnUserWhenValidUsername() throws UserNotFoundException {

    when(userRepository.findByUsername(username)).thenReturn(testUser);

    User retrievedUser = userDataService.getUser(username);
    assertNotNull(retrievedUser);
    assertEquals(username, retrievedUser.getUsername());
    assertEquals(testUser, retrievedUser);
  }

  @Test
  @DisplayName("Should throws UserNotFoundException when username is invalid")
  void shouldThrowsUserNotFoundExceptionUserWhenInvalidUsername() throws UserNotFoundException {

    when(userRepository.findByUsername(username)).thenThrow(UserNotFoundException.class);

    assertThrows(UserNotFoundException.class, () -> userDataService.getUser(username));
  }

  @Test
  @DisplayName("should remove user from database when exists")
  void shouldRemoveUserFromDatabaseWhenExists() throws UserNotFoundException {

    when(userRepository.findByUsername(username)).thenReturn(testUser);

    userDataService.deleteUser(username);

    verify(userRepository, times(1)).deleteUser(username);
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when username is invalid")
  void shouldThrowUserNotFoundExceptionWhenInvalidUsername() throws UserNotFoundException {

    String invalidUsername = "invalidUser";

    when(userRepository.findByUsername(invalidUsername)).thenThrow(UserNotFoundException.class);

    UserNotFoundException exception =
        assertThrows(
            UserNotFoundException.class, () -> userDataService.deleteUser(invalidUsername));
  }

  @Test
  @DisplayName("Should return true for valid credentials")
  void shouldReturnTrueForValidCredentials()
      throws UserNotFoundException, InvalidPasswordException, IllegalArgumentException {

    when(userRepository.findByUsername(username)).thenReturn(testUser);

    boolean isValid = userDataService.isValidCredentials(username, password);
    assertTrue(isValid);
  }

  @Test
  @DisplayName("Should throw InvalidPasswordException for invalid password")
  void shouldThrowInvalidPasswordExceptionForInvalidPassword() throws UserNotFoundException {

    when(userRepository.findByUsername(username)).thenReturn(testUser);

    InvalidPasswordException exception =
        assertThrows(
            InvalidPasswordException.class,
            () -> userDataService.isValidCredentials(username, "wrongpassword"));
  }

  @Test
  @DisplayName("Should throw UserNotFoundException for invalid username")
  void shouldThrowUserNotFoundExceptionForInvalidUsername() throws UserNotFoundException {

    String invalidUsername = "invaliduser";

    when(userRepository.findByUsername(username)).thenReturn(testUser);

    UserNotFoundException exception =
          assertThrows(
              UserNotFoundException.class,
              () -> userDataService.isValidCredentials(invalidUsername, password));
    }
  }

