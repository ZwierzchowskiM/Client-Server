package org.zwierzchowski.marcin.server;

import static org.zwierzchowski.marcin.server.ServerCommandService.Command.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.zwierzchowski.marcin.exception.*;
import org.zwierzchowski.marcin.message.Message;
import org.zwierzchowski.marcin.message.MessageRepository;
import org.zwierzchowski.marcin.message.MessageService;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.user.UserDataService;
import org.zwierzchowski.marcin.user.UserRepository;
import org.zwierzchowski.marcin.user.dto.UserLoginDTO;
import org.zwierzchowski.marcin.user.dto.UserRegistrationDTO;
import org.zwierzchowski.marcin.utils.CredentialsValidator;
import org.zwierzchowski.marcin.utils.MessageValidator;

@Log4j2
public class ServerCommandService {

  private MessageService messageService;
  private UserDataService userDataService;
  private ServerResponse response;
  private ServerNetworkHandler serverNetworkHandler;
  private Session session;
  private ServerData serverData;
  private MessageRepository messageRepository = new MessageRepository();
  private UserRepository userRepository = new UserRepository();

  public ServerCommandService(
      ServerNetworkHandler serverNetworkHandler, Session session, ServerData serverData)
      throws DatabaseConnectionException {
    this.serverNetworkHandler = serverNetworkHandler;
    this.session = session;
    this.serverData = serverData;
    this.response = new ServerResponse();
    this.messageService = new MessageService(messageRepository, userDataService);
    this.userDataService = new UserDataService(userRepository);
  }

  public String handleRequest(String clientRequest) throws JsonProcessingException {

    Command command = fromString(clientRequest);
    try {
      return switch (command) {
        case REGISTER -> handleRegistration();
        case LOGIN -> handleUserLogin();
        case LOGOUT -> handleUserLogout();
        case DELETE_USER -> handleUserDelete();
        case SEND -> handleSendMessage();
        case READ_ALL -> handleReadAllMessages();
        case READ_UNREAD -> handleReadUnreadMessages();
        case DELETE_MESSAGE -> handleDeleteMessage();
        case STOP -> handleStopServer();
        case UPTIME -> response.calculateUptime(serverData.getStartTime());
        case HELP -> response.printServerCommands(serverData.getCommandsInfo());
        case INFO -> response.printServerInfo(serverData.getSeverInfo());
        case UNKNOWN -> response.printText("Command unknown");
      };
    } catch (Exception e) {
      return handleException(e);
    }
  }

  private String handleDeleteMessage() throws IOException, UserNotFoundException {
    if (!session.isUserLoggedIn()) {
      return response.printText("No user logged in");
    }

    serverNetworkHandler.sendMessage(response.printText("Please provide message id to delete"));
    String content = serverNetworkHandler.receiveMessage();

    String sender = session.getLoggedInUser().getUsername();
    boolean result = messageService.deleteMessage(sender, Integer.parseInt(content));
    if (result) {
      return response.printText("Message deleted successfully");
    } else {
      return response.printText("Message not found");
    }
  }

  private String handleRegistration() throws IOException, InvalidCredentialsFormatException {

    UserRegistrationDTO userDto = requestRegistrationData();
    validateUserRegistrationData(userDto);

    User user =
        userDataService.addUser(userDto.getUsername(), userDto.getPassword(), userDto.getRole());

    return response.printText("User: " + user.getUsername() + " successfully registered");
  }

  private UserRegistrationDTO requestRegistrationData() throws IOException {
    serverNetworkHandler.sendMessage(response.printText("Please provide username"));
    String username = serverNetworkHandler.receiveMessage();

    serverNetworkHandler.sendMessage(response.printText("Please provide password"));
    String password = serverNetworkHandler.receiveMessage();

    serverNetworkHandler.sendMessage(response.printText("Please provide user role"));
    String role = serverNetworkHandler.receiveMessage();

    return new UserRegistrationDTO(username, password, role);
  }

  private void validateUserRegistrationData(UserRegistrationDTO user)
      throws InvalidCredentialsFormatException {
    CredentialsValidator.validateUsername(user.getUsername());
    CredentialsValidator.validatePassword(user.getPassword());
    CredentialsValidator.validateRole(user.getRole());
  }

  private String handleUserLogin()
      throws IOException,
          UserNotFoundException,
          InvalidPasswordException,
          IllegalArgumentException {

    UserLoginDTO userDto = requestLoginData();
    userDataService.isValidCredentials(userDto.getUsername(), userDto.getPassword());
    User user = userDataService.getUser(userDto.getUsername());
    session.setLoggedInUser(user);
    return response.printLoginStatus(true);
  }

  private UserLoginDTO requestLoginData() throws IOException {
    serverNetworkHandler.sendMessage(response.printText("Please provide username"));
    String username = serverNetworkHandler.receiveMessage();

    serverNetworkHandler.sendMessage(response.printText("Please provide password"));
    String password = serverNetworkHandler.receiveMessage();

    return new UserLoginDTO(username, password);
  }

  private String handleStopServer() throws JsonProcessingException {
    serverNetworkHandler.sendMessage(response.printText("Shutting down server"));
    return "STOP_SERVER";
  }

  private String handleUserLogout() throws JsonProcessingException {
    if (!session.isUserLoggedIn()) {
      return response.printText("No user logged in");
    }
    session.logoutUser();
    return response.printText("Logout successful");
  }

  private String handleUserDelete() throws IOException, UserNotFoundException {

    if (!session.isAdminLoggedIn()) {
      return response.printText("Admin privileges are required to delete a user.");
    }
    serverNetworkHandler.sendMessage(response.printText("Please provide username"));
    String username = serverNetworkHandler.receiveMessage();

    userDataService.deleteUser(username);
    return response.printText("User successfully deleted");
  }

  private String handleSendMessage()
      throws IOException, InvalidMessageException, UserNotFoundException, UserInboxIsFullException {

    if (!session.isUserLoggedIn()) {
      return response.printText("User needs to log in");
    }
    serverNetworkHandler.sendMessage(response.printText("Please provide recipient"));
    String recipient = serverNetworkHandler.receiveMessage();

    serverNetworkHandler.sendMessage(response.printText("Please provide  message"));
    String content = serverNetworkHandler.receiveMessage();

    MessageValidator.validateMessage(content);
    String sender = session.getLoggedInUser().getUsername();
    messageService.sendMessage(recipient, content, sender);
    return response.printText("Message to send successfully");
  }

  private String handleReadAllMessages() throws IOException, UserNotFoundException {

    if (!session.isUserLoggedIn()) {
      return response.printText("User needs to log in");
    }
    User user = session.getLoggedInUser();
    List<Message> messages = messageService.getAllMessages(user.getUsername());
    if (messages.isEmpty()) {
      return response.printText("No messages");
    }
    return response.printUnreadMessages(messages);
  }

  private String handleReadUnreadMessages() throws IOException, UserNotFoundException {

    if (!session.isUserLoggedIn()) {
      return response.printText("User needs to log in");
    }
    User user = session.getLoggedInUser();
    List<Message> messages = messageService.getUnreadMessages(user.getUsername());
    if (messages.isEmpty()) {
      return response.printText("No unread messages");
    }
    return response.printUnreadMessages(messages);
  }

  public String printOptions() throws JsonProcessingException {
    if (session.isUserLoggedIn() && !session.isAdminLoggedIn()) {
      return response.printText(serverData.printUserOptions());
    } else if (session.isAdminLoggedIn()) {
      return response.printText(serverData.printAdminOptions());
    } else return response.printText(serverData.printGuestOptions());
  }

  private String handleException(Exception e) throws JsonProcessingException {
    if (e instanceof JsonProcessingException) {
      log.error("JSON processing error", e);
      return response.printError(e.getMessage());
    } else if (e instanceof InvalidCredentialsFormatException) {
      log.error("Invalid Credentials format", e);
      return response.printError(e.getMessage());
    } else if (e instanceof UserNotFoundException || e instanceof InvalidPasswordException) {
      log.error("Invalid user credentials", e);
      return response.printError(e.getMessage());
    } else if (e instanceof UserInboxIsFullException) {
      log.error("User inbox is full", e);
      return response.printText("Message not send." + e.getMessage());
    } else if (e instanceof InvalidMessageException) {
      log.error("Invalid Message format", e);
      return response.printText("Invalid message: " + e.getMessage());
    } else if (e instanceof IOException) {
      log.error("Error while communicating with server", e);
      return response.printText("Error while communicating with server: " + e.getMessage());
    } else {
      log.error("Unexpected error", e);
      return response.printText("Unexpected error: " + e.getMessage());
    }
  }

  public enum Command {
    REGISTER,
    LOGIN,
    LOGOUT,
    DELETE_USER,
    SEND,
    READ_ALL,
    READ_UNREAD,
    DELETE_MESSAGE,
    UPTIME,
    HELP,
    INFO,
    STOP,
    UNKNOWN;

    public static Command fromString(String command) {
      try {
        return Command.valueOf(command.toUpperCase());
      } catch (IllegalArgumentException e) {
        return UNKNOWN;
      }
    }
  }
}
