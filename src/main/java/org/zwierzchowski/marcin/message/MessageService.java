package org.zwierzchowski.marcin.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.zwierzchowski.marcin.exception.UserInboxIsFullException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.user.UserDataService;
import org.zwierzchowski.marcin.utils.FileService;

public class MessageService {

  MessageRepository messageRepository;
  UserDataService userDataService;

  public MessageService() {
    messageRepository = new MessageRepository();
    userDataService = new UserDataService();
  }

  public void sendMessage(String recipient, String content, String sender)
      throws UserNotFoundException, UserInboxIsFullException {
    Message message = new Message(content, sender);

    User user = userDataService.getUser(recipient);

    if (user.isUserInboxFull()) {
      throw new UserInboxIsFullException("Recipient inbox is full", recipient);
    }
    messageRepository.saveMessage(message, user.getId());
  }

  public List<Message> getUnreadMessages(String username)
      throws IOException, UserNotFoundException {

    Map<String, User> users = FileService.loadDataBase();
    if (!users.containsKey(username)) {
      throw new UserNotFoundException("Recipient not found", username);
    }
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

  public List<Message> getAllMessages(String username) throws UserNotFoundException{

    User user = userDataService.getUser(username);
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

    return unreadMessages;
  }
}
