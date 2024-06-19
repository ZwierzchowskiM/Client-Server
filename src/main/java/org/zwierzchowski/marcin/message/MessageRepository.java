package org.zwierzchowski.marcin.message;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
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
        .set(Messages.MESSAGES.STATUS, message.getStatus().toString())
        .execute();
  }

  public List<Message> getMessagesByUserId(int id) {

    Result<Record> messages =
        context.select().from(Messages.MESSAGES).where(Messages.MESSAGES.USER_ID.eq(id)).fetch();

    List<Message> messageList = new ArrayList<>();

    for (Record message : messages) {
      String content = message.getValue(Messages.MESSAGES.CONTENT, String.class);
      String sender = message.getValue(Messages.MESSAGES.SENDER, String.class);
      String status = message.getValue(Messages.MESSAGES.STATUS, String.class);
//      Date date = message.getValue(Messages.MESSAGES.DATE, Date.class);
      Message newMessage = new Message(content, sender, new Date(), Message.Status.valueOf(status));
      messageList.add(newMessage);
    }

    return messageList;
  }
}
