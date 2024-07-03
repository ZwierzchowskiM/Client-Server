package org.zwierzchowski.marcin.user;

import org.mindrot.jbcrypt.BCrypt;
import org.zwierzchowski.marcin.exception.InvalidCredentialsFormatException;
import org.zwierzchowski.marcin.exception.InvalidPasswordException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;

public class UserDataService {

  private UserRepository userRepository;

  public UserDataService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User addUser(String username, String password, String role)
      throws InvalidCredentialsFormatException {
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    User newUser =
        switch (role.toLowerCase()) {
          case "user" -> new StandardUser(username, hashedPassword);
          case "admin" -> new Admin(username, hashedPassword);
          default -> throw new InvalidCredentialsFormatException("Unexpected value: " + role);
        };

    userRepository.saveUser(newUser);
    return newUser;
  }

  public boolean isValidCredentials(String username, String password)
      throws UserNotFoundException, InvalidPasswordException, IllegalArgumentException {

    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UserNotFoundException("User not exist", username);
    }

    if (!BCrypt.checkpw(password, user.getPassword())) {
      throw new InvalidPasswordException("Invalid password");
    }

    return true;
  }

  public User getUser(String username) throws UserNotFoundException {

    return userRepository.findByUsername(username);
  }

  public void deleteUser(String username) throws UserNotFoundException {

    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UserNotFoundException("User not exist", username);
    }
    userRepository.deleteUser(username);
  }
}
