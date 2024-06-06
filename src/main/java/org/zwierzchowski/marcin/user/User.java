package org.zwierzchowski.marcin.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.zwierzchowski.marcin.message.Message;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Admin.class, name = "admin"),
  @JsonSubTypes.Type(value = StandardUser.class, name = "user")
})
@Getter
@Setter
@AllArgsConstructor
public abstract class User {

  @JsonProperty("username")
  private String username;

  @JsonProperty("password")
  private String password;

  @Setter(AccessLevel.NONE)
  @JsonProperty("role")
  private Role role;

  @JsonProperty("messages")
  private List<Message> messages;

  private int maxUnreadMessages;

  public enum Role {
    USER,
    ADMIN
  }

  public void addMessage(Message message) {
    messages.add(message);
  }

  public boolean isUserInboxFull() {
    long countUnread =
            messages.stream().filter(m -> m.getStatus().equals(Message.Status.UNREAD)).count();
    return countUnread > maxUnreadMessages;
  }
}
