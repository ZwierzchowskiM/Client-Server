package org.zwierzchowski.marcin.user;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.zwierzchowski.marcin.message.Message;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Admin.class, name = "admin"),
  @JsonSubTypes.Type(value = StandardUser.class, name = "user")
})
@Getter
@Setter
@AllArgsConstructor
public abstract class User {

  private int id;
  private String username;
  private String password;
  private Role role;
  private List<Message> messages;

  protected User(String username, String password, Role role, List<Message> messages) {
    this.username = username;
    this.password = password;
    this.role = role;
    this.messages = messages;
  }

  public void addMessage(Message message) {
    messages.add(message);
  }

  public abstract boolean isUserInboxFull();

  public enum Role {
    USER,
    ADMIN
  }

  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", role=" + role +
            ", messages=" + messages +
            '}';
  }
}
