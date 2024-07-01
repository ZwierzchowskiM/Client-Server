package org.zwierzchowski.marcin.message;

import java.util.ArrayList;
import java.util.List;
import org.zwierzchowski.marcin.exception.DatabaseConnectionException;
import org.zwierzchowski.marcin.exception.UserInboxIsFullException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.user.UserDataService;

public class MessageService {

  MessageRepository messageRepository;
  UserDataService userDataService;

  public MessageService(MessageRepository messageRepository, UserDataService userDataService) throws DatabaseConnectionException {
    this.messageRepository = messageRepository;
    this.userDataService = userDataService;
  }

  public void sendMessage(String recipient, String content, String sender)
      throws UserNotFoundException, UserInboxIsFullException {
    Message message = new Message(content, sender);

    User user = userDataService.getUser(recipient);
    List<Message> messages = messageRepository.findMessagesByUserId(user.getId());

    if (messages.size() >= user.getMaxUnreadMessages()){
      throw new UserInboxIsFullException("Recipient inbox is full", recipient);
    }
    messageRepository.saveMessage(message, user.getId());
  }

  public List<Message> getUnreadMessages(String username)
      throws UserNotFoundException {

    User user = userDataService.getUser(username);
    List<Message> messages =  messageRepository.findMessagesByUserId(user.getId());
    List<Message> unreadMessages = new ArrayList<>();
    if (!messages.isEmpty()) {
      for (Message m : messages) {
        if (m.getStatus().equals(Message.Status.UNREAD)) {
          unreadMessages.add(m);
          m.setStatus(Message.Status.READ);
          messageRepository.updateMessage(m.getId());
        }
      }
    }
    return unreadMessages;
  }

  public List<Message> getAllMessages(String username) throws UserNotFoundException {

    User user = userDataService.getUser(username);
    List<Message> messages =  messageRepository.findMessagesByUserId(user.getId());

    return messages;
  }

  public boolean deleteMessage(String username, int id) throws UserNotFoundException {

    User user = userDataService.getUser(username);
    List<Message> messages =  messageRepository.findMessagesByUserId(user.getId());

    for (Message m : messages) {
      if (m.getId() == id) {
        messageRepository.deleteMessage(id);
        return true;
      }
    }
    return false;
  }
}
