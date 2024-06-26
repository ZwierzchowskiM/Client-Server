package org.zwierzchowski.marcin.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

  private int id;
  @JsonIgnore
  private int userID;
  private String content;
  private String sender;
  private LocalDateTime createdDate;
  @JsonIgnore private Status status;

  public Message(String content, String sender) {
    this.content = content;
    this.sender = sender;
    this.createdDate = LocalDateTime.now();
    this.status = Status.UNREAD;
  }

  public Message(
      int id, int userId, String content, String sender, LocalDateTime date, Status status) {
    this.id = id;
    this.userID = userId;
    this.content = content;
    this.sender = sender;
    this.createdDate = date;
    this.status = status;
  }

  public enum Status {
    READ,
    UNREAD
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  public LocalDateTime getCreatedDate() {
    return createdDate;
  }
}
