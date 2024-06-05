package org.zwierzchowski.marcin.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zwierzchowski.marcin.user.Admin;
import org.zwierzchowski.marcin.user.StandardUser;
import org.zwierzchowski.marcin.user.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionTest {

  private Session session;
  private User user;

  @BeforeEach
  void setUp() {
    session = new Session();
  }

  @Test
  void shoutReturnTrueWhenAdminIsLoggedIn() {
    user = new Admin("user", "pass");
    session.setLoggedInUser(user);

    assertTrue(session.isAdminLoggedIn());
  }

  @Test
  void shoutReturnTrueWhenUserIsLoggedIn() {
    user = new StandardUser("user", "pass");
    session.setLoggedInUser(user);

    assertFalse(session.isAdminLoggedIn());
  }

  @Test
  void shoutReturnFalseWhenNoUserIsLoggedIn() {
    assertFalse(session.isAdminLoggedIn());
  }
}
