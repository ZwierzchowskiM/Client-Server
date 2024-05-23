package org.zwierzchowski.marcin.message;

import org.zwierzchowski.marcin.exception.UserInboxIsFullException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.utils.FileService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageService {

  private static final int MAX_UNREAD_MESSAGES = 0;

  public void sendMessage(String recipient, String content, String sender)
      throws UserNotFoundException, UserInboxIsFullException, IOException {
    Message message = new Message(content, sender);
    Map<String, User> users = FileService.loadDataBase();

    if (!users.containsKey(recipient)) {
      throw new UserNotFoundException("Recipient not found", recipient);
    }

    User user = users.get(recipient);

    if (checkUserInboxIsFull(user.getMessages())) {
      throw new UserInboxIsFullException("Recipient inbox is full", recipient);
    }

    user.addMessage(message);
    FileService.saveDataBase(users);
  }

  public List<Message> getUnreadMessages(String username) throws IOException {

    Map<String, User> users = FileService.loadDataBase();
    User user = users.get(username);
    List<Message> messages = user.getMessages();
    List<Message> unreadMessages = new ArrayList<>();
    if (!messages.isEmpty()) {
      for (Message m : messages) {
        if (m.getStatus().equals(Message.Status.UNREAD)) {
          unreadMessages.add(m);
          m.setStatus(Message.Status.READ);
        }
      }
    }

    FileService.saveDataBase(users);
    return unreadMessages;
  }

  private boolean checkUserInboxIsFull(List<Message> messages) {
    int countUnread = 0;
    for (Message m : messages) {
      if (m.getStatus().equals(Message.Status.UNREAD)) {
        countUnread++;
      }
    }
    return countUnread > MAX_UNREAD_MESSAGES;
  }
}
