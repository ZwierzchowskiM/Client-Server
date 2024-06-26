package org.zwierzchowski.marcin.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StandardUser extends User {

  public StandardUser(String username, String password) {
    super(username, password, Role.USER,5);
  }

  public StandardUser(int id, String username, String password) {
    super(id, username, password, Role.USER,5);
  }

}
