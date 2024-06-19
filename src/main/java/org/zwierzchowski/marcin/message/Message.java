package org.zwierzchowski.marcin.message;

import java.time.Instant;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

  private String content;
  private String sender;
  private Date createdDate;
  private Status status;

  public Message(String content, String sender) {
    this.content = content;
    this.sender = sender;
    this.createdDate = Date.from(Instant.now());
    this.status = Status.UNREAD;
  }

  public Message(String content, String sender, Date date, Status status) {
    this.content = content;
    this.sender = sender;
    this.createdDate = date;
    this.status = Status.UNREAD;
  }

  public enum Status {
    READ,
    UNREAD
  }
}
