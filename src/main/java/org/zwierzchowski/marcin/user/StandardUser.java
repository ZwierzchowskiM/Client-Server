package org.zwierzchowski.marcin.user;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.zwierzchowski.marcin.message.Message;

@Getter
@Setter
public class StandardUser extends User {

  private static final int MAX_UNREAD_MESSAGES = 4;

  public StandardUser(String username, String password) {
    super(username, password, Role.USER, new ArrayList<>());
  }

  public StandardUser(int id,String username, String password, List<Message> messages) {
  super(id,username, password, Role.USER, messages);
  }

  @Override
  public boolean isUserInboxFull() {
    long countUnread =
        super.getMessages().stream()
            .filter(m -> m.getStatus().equals(Message.Status.UNREAD))
            .count();
    return countUnread > MAX_UNREAD_MESSAGES;
  }
}
