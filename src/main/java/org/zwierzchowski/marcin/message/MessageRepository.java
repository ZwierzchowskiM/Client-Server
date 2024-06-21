package org.zwierzchowski.marcin.message;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.zwierzchowski.marcin.db.tables.Messages;
import org.zwierzchowski.marcin.utils.DataBaseManager;

public class MessageRepository {

  DSLContext context;
  DataBaseManager dataBaseManager;

  public MessageRepository() {
    dataBaseManager = new DataBaseManager();
    Connection conn = dataBaseManager.getConnection();
    context = DSL.using(conn, SQLDialect.POSTGRES);
  }

  public void saveMessage(Message message, int userId) {
    context
        .insertInto(Messages.MESSAGES)
        .set(Messages.MESSAGES.CONTENT, message.getContent())
        .set(Messages.MESSAGES.SENDER, message.getSender())
        .set(Messages.MESSAGES.USER_ID, userId)
        .set(Messages.MESSAGES.DATE, message.getCreatedDate())
        .set(Messages.MESSAGES.STATUS, message.getStatus().toString())
        .execute();
  }

  public List<Message> findMessagesByUserId(int id) {

    Result<Record> messages =
        context.select().from(Messages.MESSAGES).where(Messages.MESSAGES.USER_ID.eq(id)).fetch();

    List<Message> messageList = new ArrayList<>();

    for (Record message : messages) {
      int messageId = message.getValue(Messages.MESSAGES.ID, Integer.class);
      String content = message.getValue(Messages.MESSAGES.CONTENT, String.class);
      String sender = message.getValue(Messages.MESSAGES.SENDER, String.class);
      String status = message.getValue(Messages.MESSAGES.STATUS, String.class);
      LocalDateTime date = message.getValue(Messages.MESSAGES.DATE, LocalDateTime.class);
      Message newMessage = new Message(messageId, content, sender, date, Message.Status.valueOf(status));
      messageList.add(newMessage);
    }
    return messageList;
  }

  public void updateMessage(int id) {

    context.update(Messages.MESSAGES)
        .set(Messages.MESSAGES.STATUS, Message.Status.READ.toString())
        .where(Messages.MESSAGES.ID.eq(id))
        .execute();
  }
}
