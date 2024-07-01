package org.zwierzchowski.marcin.message;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.zwierzchowski.marcin.exception.DatabaseConnectionException;
import org.zwierzchowski.marcin.exception.UserInboxIsFullException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.user.StandardUser;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.user.UserDataService;

class MessageServiceTest {

  private String recipient;
  private String content;
  private String sender;
  private User userRecipient;
  private User userSender;
  private List<User> users;

  @Mock
  private UserDataService userDataService;

  @Mock
  private MessageRepository messageRepository;

  @InjectMocks
  private MessageService messageService;

  @BeforeEach
  void setUp() throws DatabaseConnectionException {

    MockitoAnnotations.openMocks(this);
    recipient = "recipient";
    content = "Test message";
    sender = "sender";
    userRecipient = new StandardUser("john", "pass");
    userSender = new StandardUser("tom", "pass");
    users = new ArrayList<>();
  }

  @Test
  @DisplayName("Send message success")
  void shouldSendMessageSuccessfully() throws UserNotFoundException, UserInboxIsFullException {

    User user = new StandardUser(1, "john", "pass");
    List<Message> messages = Arrays.asList(new Message[3]);

    when(userDataService.getUser(recipient)).thenReturn(user);
    when(messageRepository.findMessagesByUserId(user.getId())).thenReturn(messages);

    messageService.sendMessage(recipient, content, sender);
    verify(messageRepository, times(1)).saveMessage(any(Message.class), eq(user.getId()));
  }

  @Test
  @DisplayName("Send message to not existed user should throw UserNotExistException")
  public void shouldThrowUserNotFoundExceptionWhenRecipientNotExists() throws Exception {

    when(userDataService.getUser(recipient)).thenThrow(new UserNotFoundException( "User not found", recipient));

    assertThrows(UserNotFoundException.class, () -> {
      messageService.sendMessage(recipient, content, sender);
    });
  }

  @Test
  public void shouldThrowUserInboxIsFullExceptionWhenRecipientInboxIsFull() throws Exception {


    User user = new StandardUser(1, "john", "pass");

    List<Message> messages = Arrays.asList(new Message[10]); // Mock 10 messages to fill the inbox

    when(userDataService.getUser(recipient)).thenReturn(user);
    when(messageRepository.findMessagesByUserId(user.getId())).thenReturn(messages);

    assertThrows(UserInboxIsFullException.class, () -> {
      messageService.sendMessage(recipient, content, sender);
    });
  }



//  @Test
//  @DisplayName("Send message to not existed user should throw UserNotExistException")
//  void shouldThrowUserNotFoundExceptionWhenRecipientNotExists() {
//
//    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
//      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
//
//      assertThrows(
//          UserNotFoundException.class,
//          () -> messageService.sendMessage(recipient, content, sender));
//    }
//  }

//  @Test
//  @DisplayName("Send message to user which inbox is full should throw UserInboxIsFullException")
//  void shouldThrowUserInboxIsFullExceptionWhenRecipientInboxIsFull() {
//
//    users.put(recipient, userRecipient);
//    users.put(sender, userSender);
//    Message message1 = new Message("test", "testUser");
//    Message message2 = new Message("test", "testUser");
//    Message message3 = new Message("test", "testUser");
//    Message message4 = new Message("test", "testUser");
//    Message message5 = new Message("test", "testUser");
//
//    userRecipient.addMessage(message1);
//    userRecipient.addMessage(message2);
//    userRecipient.addMessage(message3);
//    userRecipient.addMessage(message4);
//    userRecipient.addMessage(message5);
//
//    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
//      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
//      assertThrows(
//          UserInboxIsFullException.class,
//          () -> messageService.sendMessage(recipient, content, sender));
//    }
//  }

//  @Test
//  @DisplayName("Get list of unread user messages")
//  void shouldGetListOfUnreadUserMessagesWhenInboxIsNotEmpty()
//      throws UserNotFoundException, IOException {
//
//    users.put(recipient, userRecipient);
//    Message message1 = new Message("test", "testUser");
//    Message message2 = new Message("test", "testUser");
//    userRecipient.addMessage(message1);
//    userRecipient.addMessage(message2);
//
//    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
//      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
//      assertEquals(2, messageService.getUnreadMessages(recipient).size());
//    }
//  }

//  @Test
//  @DisplayName("Get empty list when user has no unread messages")
//  void shouldGetEmptyListWhenInboxIsEmpty() throws IOException, UserNotFoundException {
//
//    users.put(recipient, userRecipient);
//
//    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
//      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
//      assertEquals(0, messageService.getUnreadMessages(recipient).size());
//    }
//  }
//
//  @Test
//  @DisplayName("Get unread messages for non existing user should throw UserInboxIsFullException")
//  void shouldThrowUserNotFoundExceptionWhenUserNotExist()
//      throws IOException, UserNotFoundException {
//
//    try (MockedStatic<FileService> fileServiceMock = Mockito.mockStatic(FileService.class)) {
//      fileServiceMock.when(FileService::loadDataBase).thenReturn(users);
//      assertThrows(UserNotFoundException.class, () -> messageService.getUnreadMessages(recipient));
//    }
//  }

}