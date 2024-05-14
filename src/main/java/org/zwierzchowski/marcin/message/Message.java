package org.zwierzchowski.marcin.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class Message {

    @JsonProperty("content")
    private String content;
    @JsonProperty("sender")
    private String sender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private Date createdDate;
    @JsonProperty("status")
    private Status status;

    @JsonCreator
    public Message(@JsonProperty("content") String content, @JsonProperty("sender") String sender) {
        this.content = content;
        this.sender = sender;
        this.createdDate = Date.from(Instant.now());
        this.status = Status.UNREAD;
    }

    public enum Status {
        READ,
        UNREAD
    }
}
