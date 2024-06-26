package org.zwierzchowski.marcin.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Admin extends User {


  public Admin(String username, String password) {
    super(username, password, Role.ADMIN, 100);
  }

  public Admin(int id, String username, String password) {
    super(id, username, password, Role.ADMIN, 100);
  }
}
