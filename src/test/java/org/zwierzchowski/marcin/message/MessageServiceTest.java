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
  User user;

  @Mock private UserDataService userDataService;

  @Mock private MessageRepository messageRepository;

  @InjectMocks private MessageService messageService;

  @BeforeEach
  void setUp() throws DatabaseConnectionException {

    MockitoAnnotations.openMocks(this);
    recipient = "recipient";
    content = "Test message";
    sender = "sender";
    user = new StandardUser(1, "john", "pass");
  }

  @Test
  @DisplayName("Send message success")
  void shouldSendMessageSuccessfully() throws UserNotFoundException, UserInboxIsFullException {

    List<Message> messages = Arrays.asList(new Message[3]);

    when(userDataService.getUser(recipient)).thenReturn(user);
    when(messageRepository.findMessagesByUserId(user.getId())).thenReturn(messages);

    messageService.sendMessage(recipient, content, sender);
    verify(messageRepository, times(1)).saveMessage(any(Message.class), eq(user.getId()));
  }

  @Test
  @DisplayName("Send message to not existed user should throw UserNotExistException")
  void shouldThrowUserNotFoundExceptionWhenRecipientNotExists() throws Exception {

    when(userDataService.getUser(recipient))
        .thenThrow(new UserNotFoundException("User not found", recipient));

    assertThrows(
        UserNotFoundException.class,
        () -> {
          messageService.sendMessage(recipient, content, sender);
        });
  }

  @Test
  void shouldThrowUserInboxIsFullExceptionWhenRecipientInboxIsFull() throws Exception {

    List<Message> messages = Arrays.asList(new Message[10]);

    when(userDataService.getUser(recipient)).thenReturn(user);
    when(messageRepository.findMessagesByUserId(user.getId())).thenReturn(messages);

    assertThrows(
        UserInboxIsFullException.class,
        () -> {
          messageService.sendMessage(recipient, content, sender);
        });
  }

  @Test
  @DisplayName("Get list of unread user messages")
  void shouldGetListOfUnreadUserMessagesWhenInboxIsNotEmpty() throws UserNotFoundException {

    Message message = new Message("Test message", "sender");
    List<Message> messages = Arrays.asList(message);

    when(userDataService.getUser(recipient)).thenReturn(user);
    when(messageRepository.findMessagesByUserId(user.getId())).thenReturn(messages);

    assertEquals(1, messageService.getUnreadMessages(recipient).size());
  }

  @Test
  @DisplayName("Get empty list when user has no unread messages")
  void shouldGetEmptyListWhenInboxIsEmpty() throws UserNotFoundException {

    List<Message> messages = new ArrayList<>();
    when(userDataService.getUser(recipient)).thenReturn(user);
    when(messageRepository.findMessagesByUserId(user.getId())).thenReturn(messages);

    assertEquals(0, messageService.getUnreadMessages(recipient).size());
  }
}
