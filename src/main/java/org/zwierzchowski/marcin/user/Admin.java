package org.zwierzchowski.marcin.user;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.zwierzchowski.marcin.message.Message;

@Getter
@Setter
public class Admin extends User {


  public Admin(String username, String password) {
    super(username, password, Role.ADMIN, new ArrayList<>());
  }

  public Admin(int id, String username, String password,  List<Message> messages) {
    super(id, username, password, Role.ADMIN, messages);
  }

  @Override
  public boolean isUserInboxFull() {
    return false;
  }
}
