package org.zwierzchowski.marcin.server;

import lombok.Getter;
import lombok.Setter;
import org.zwierzchowski.marcin.user.User;

@Getter
@Setter
public class Session {

  private User loggedInUser;

  public boolean isUserLoggedIn() {
    return loggedInUser != null && !loggedInUser.getRole().equals(User.Role.ADMIN);
  }

  public boolean isAdminLoggedIn() {
    return loggedInUser != null && loggedInUser.getRole().equals(User.Role.ADMIN);
  }

  public void logoutUser() {
    loggedInUser = null;
  }
}
