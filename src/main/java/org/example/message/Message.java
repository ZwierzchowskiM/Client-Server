package org.example.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class Message {

    @JsonProperty("content")
    private String content;
    @JsonProperty("sender")
    private String sender;
    //    @JsonProperty ("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    Date createdDate;

    @JsonCreator
    public Message(@JsonProperty("content") String content, @JsonProperty("sender") String sender) {
        this.content = content;
        this.sender = sender;
        this.createdDate = Date.from(Instant.now());
    }
}
