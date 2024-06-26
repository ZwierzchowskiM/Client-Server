package org.zwierzchowski.marcin.user;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class User {



  private int id;
  private String username;
  private String password;
  private Role role;
  private  int maxUnreadMessages;

  protected User(String username, String password, Role role, int maxUnreadMessages) {
    this.username = username;
    this.password = password;
    this.role = role;
    this.maxUnreadMessages = maxUnreadMessages;
  }

  protected User(int id,String username, String password, Role role, int maxUnreadMessages) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
    this.maxUnreadMessages = maxUnreadMessages;
  }

  public enum Role {
    USER,
    ADMIN
  }

}
