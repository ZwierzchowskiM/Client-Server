package org.zwierzchowski.marcin.message;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.zwierzchowski.marcin.exception.UserInboxIsFullException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.user.StandardUser;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.utils.FileService;

class MessageServiceTest {

  private MessageService messageService;
  String recipient;
  String content;
  String sender;

  @BeforeEach
  void setUp() {

    messageService = new MessageService();
    recipient = "recipient";
    content = "Test message";
    sender = "sender";
  }

  @Test
  @DisplayName("Send message to user and check if is placed in inbox")
  void shouldAddMessageToRecipientInbox() {

    User userRecipient = new StandardUser("john", "pass");
    User userSender = new StandardUser("tom", "pass");
    Map<String, User> users = new HashMap<>();
    users.put(recipient, userRecipient);
    users.put(sender, userSender);

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
      assertEquals(0, userRecipient.getMessages().size());
      assertDoesNotThrow(() -> messageService.sendMessage(recipient, content, sender));
      assertEquals(1, userRecipient.getMessages().size());
    }
  }

  @Test
  @DisplayName("Send message to not existed user should throw UserNotExistException")
  void shouldThrowUserNotFoundExceptionWhenRecipientNotExists() {

    Map<String, User> users = new HashMap<>();

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);

      assertThrows(
          UserNotFoundException.class,
          () -> messageService.sendMessage(recipient, content, sender));
    }
  }

  @Test
  @DisplayName("Send message to user which inbox is full should throw UserInboxIsFullException")
  void shouldThrowUserInboxIsFullExceptionWhenRecipientInboxIsFull() {

    User userRecipient = new StandardUser("john", "pass");
    User userSender = new StandardUser("tom", "pass");
    Map<String, User> users = new HashMap<>();
    users.put(recipient, userRecipient);
    users.put(sender, userSender);
    Message message1 = new Message("test", "testUser");
    Message message2 = new Message("test", "testUser");
    Message message3 = new Message("test", "testUser");
    Message message4 = new Message("test", "testUser");
    Message message5 = new Message("test", "testUser");

    userRecipient.addMessage(message1);
    userRecipient.addMessage(message2);
    userRecipient.addMessage(message3);
    userRecipient.addMessage(message4);
    userRecipient.addMessage(message5);

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
      assertThrows(
          UserInboxIsFullException.class,
          () -> messageService.sendMessage(recipient, content, sender));
    }
  }

  @Test
  @DisplayName("Get list of unread user messages")
  void shouldGetListOfUnreadUserMessagesWhenInboxIsNotEmpty()
      throws UserNotFoundException, IOException {

    User user = new StandardUser("john", "pass");
    Map<String, User> users = new HashMap<>();
    users.put(recipient, user);
    Message message1 = new Message("test", "testUser");
    Message message2 = new Message("test", "testUser");
    user.addMessage(message1);
    user.addMessage(message2);

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
      assertEquals(2, messageService.getUnreadMessages(recipient).size());
    }
  }

  @Test
  @DisplayName("Get empty list when user has no unread messages")
  void shouldGetEmptyListWhenInboxIsEmpty() throws IOException, UserNotFoundException {

    User user = new StandardUser("john", "pass");
    Map<String, User> users = new HashMap<>();
    users.put(recipient, user);

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
      assertEquals(0, messageService.getUnreadMessages(recipient).size());
    }
  }

  @Test
  @DisplayName("Get unread messages for non existing user should throw UserInboxIsFullException")
  void shouldThrowUserNotFoundExceptionWhenUserNotExist() throws IOException, UserNotFoundException {

    Map<String, User> users = new HashMap<>();

    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
      assertThrows(
              UserNotFoundException.class,
              () -> messageService.getUnreadMessages(recipient));
    }
  }
}
