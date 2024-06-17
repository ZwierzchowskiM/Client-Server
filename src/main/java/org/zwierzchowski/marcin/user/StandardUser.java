package org.zwierzchowski.marcin.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import org.zwierzchowski.marcin.message.Message;

@Getter
@Setter
@JsonTypeName("user")
public class StandardUser extends User {

  private final int maxUnreadMessages = 4;

  @JsonCreator
  public StandardUser(
      @JsonProperty("username") String username, @JsonProperty("password") String password) {
    super(username, password, Role.USER, new ArrayList<>());
  }

  @Override
  public boolean isUserInboxFull() {
    long countUnread =
        super.getMessages().stream()
            .filter(m -> m.getStatus().equals(Message.Status.UNREAD))
            .count();
    return countUnread > maxUnreadMessages;
  }
}
