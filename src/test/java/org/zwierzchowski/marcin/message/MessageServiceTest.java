package org.zwierzchowski.marcin.message;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.zwierzchowski.marcin.user.StandardUser;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.utils.FileService;

class MessageServiceTest {

  private MessageService messageService;

  @BeforeEach
  void setUp() {
    messageService = new MessageService();
  }

  @Test
  public void sendMessage_Successful() {

    String recipient = "recipient";
    String content = "Test message";
    String sender = "sender";
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
}
