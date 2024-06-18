package org.zwierzchowski.marcin.user;

import org.mindrot.jbcrypt.BCrypt;
import org.zwierzchowski.marcin.exception.InvalidCredentialsFormatException;
import org.zwierzchowski.marcin.exception.InvalidPasswordException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;

public class UserDataService {

  private UserRepository userRepository;

  public UserDataService() {
    userRepository = new UserRepository();
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
      throws UserNotFoundException, InvalidPasswordException {

    User user = userRepository.finByUsername(username);
    if (user == null) {
      throw new UserNotFoundException("User not exist", username);
    }

    if (!BCrypt.checkpw(password, user.getPassword())) {
      throw new InvalidPasswordException("Invalid password");
    }

    return true;
  }

  public User getUser(String username) throws UserNotFoundException {

    return userRepository.finByUsername(username);
  }

  public void deleteUser(String username) throws UserNotFoundException {

    User user = userRepository.finByUsername(username);
    if (user == null) {
      throw new UserNotFoundException("User not exist", username);
    }
    userRepository.deleteUser(username);
  }

  public void updateUser(String username, String password, String role)
      throws UserNotFoundException {
    User user = userRepository.finByUsername(username);
    user.setUsername(username);
    user.setPassword(password);
    user.setRole(User.Role.valueOf(role.toUpperCase()));

    userRepository.updateUser(user, username);
  }
}
