package org.zwierzchowski.marcin.server;

import static org.zwierzchowski.marcin.server.ServerCommandService.Command.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.zwierzchowski.marcin.exception.*;
import org.zwierzchowski.marcin.message.Message;
import org.zwierzchowski.marcin.message.MessageService;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.user.UserDataService;
import org.zwierzchowski.marcin.utils.CredentialsValidator;
import org.zwierzchowski.marcin.utils.MessageValidator;

@Log4j2
public class ServerCommandService {

  private UserDataService userDataService = new UserDataService();
  private MessageService messageService = new MessageService();
  private ServerResponse response = new ServerResponse();
  private ServerNetworkHandler serverNetworkHandler;
  private Session session;
  private ServerData serverData;

  public ServerCommandService(
      ServerNetworkHandler serverNetworkHandler, Session session, ServerData serverData) {
    this.serverNetworkHandler = serverNetworkHandler;
    this.session = session;
    this.serverData = serverData;
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
        case STOP -> handleStopServer();
        case UPTIME -> response.calculateUptime(serverData.getStartTime());
        case HELP -> response.printServerCommands(serverData.getCommandsInfo());
        case INFO -> response.printServerInfo(serverData.getSeverInfo());
        case UNKNOWN -> response.printText("Command unknown");
      };
    } catch (JsonProcessingException e) {
      log.error("JSON processing error", e);
      return response.printError(e.getMessage());
    } catch (InvalidCredentialsFormatException e) {
      log.error("Invalid Credentials format", e);
      return response.printError(e.getMessage());
    } catch (UserNotFoundException | InvalidPasswordException e) {
      log.error("Invalid user credentials", e);
      return response.printError(e.getMessage());
    } catch (IOException e) {
      log.error("Error while communicating with server", e);
      return response.printText("Error while communicating with server: " + e.getMessage());
    }
  }

  private String handleRegistration() throws IOException, InvalidCredentialsFormatException {
    serverNetworkHandler.sendMessage(response.printText("Please provide username"));
    String username = serverNetworkHandler.receiveMessage();
    CredentialsValidator.validateUsername(username);

    serverNetworkHandler.sendMessage(response.printText("Please provide password"));
    String password = serverNetworkHandler.receiveMessage();
    CredentialsValidator.validatePassword(password);

    serverNetworkHandler.sendMessage(response.printText("Please provide user role"));
    String role = serverNetworkHandler.receiveMessage();
    CredentialsValidator.validateRole(role);

    User user = userDataService.addUser(username, password, role);

    return response.printText("User: " + user.getUsername() + " successfully registered");
  }

  private String handleUserLogin()
      throws IOException, UserNotFoundException, InvalidPasswordException {
    serverNetworkHandler.sendMessage(response.printText("Please provide username"));
    String username = serverNetworkHandler.receiveMessage();

    serverNetworkHandler.sendMessage(response.printText("Please provide password"));
    String password = serverNetworkHandler.receiveMessage();

    userDataService.isValidCredentials(username, password);
    User user = userDataService.getUser(username);
    session.setLoggedInUser(user);
    return response.printLoginStatus(true);
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


  private String handleSendMessage() throws JsonProcessingException {

    try {
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

    } catch (IOException e) {
      log.error("Error while communicating with server", e);
      return response.printText("Error while communicating with server: " + e.getMessage());
    } catch (InvalidMessageException e) {
      log.error("Invalid Message format", e);
      return response.printText("Invalid message: " + e.getMessage());
    } catch (UserNotFoundException e) {
      log.error("User not found", e);
      return response.printText("Recipient not found: " + e.getMessage());
    } catch (UserInboxIsFullException e) {
      log.error("User inbox is full", e);
      return response.printText("Message not send." + e.getMessage());
    }
  }

  private String handleReadAllMessages() throws IOException {
    try {
      if (!session.isUserLoggedIn()) {
        return response.printText("User needs to log in");
      }
      User user = session.getLoggedInUser();
      List<Message> messages = messageService.getAllMessages(user.getUsername());
      if (messages.isEmpty()) {
        return response.printText("No messages");
      }
      return response.printUnreadMessages(messages);

    } catch (UserNotFoundException e) {
      log.error("User not found", e);
      return response.printText("Recipient not found: " + e.getMessage());
    }
  }

  private String handleReadUnreadMessages() throws IOException {
    try {
      if (!session.isUserLoggedIn()) {
        return response.printText("User needs to log in");
      }
      User user = session.getLoggedInUser();
      List<Message> messages = messageService.getUnreadMessages(user.getUsername());
      if (messages.isEmpty()) {
        return response.printText("No messages");
      }
      return response.printUnreadMessages(messages);

    } catch (UserNotFoundException e) {
      log.error("User not found", e);
      return response.printText("Recipient not found: " + e.getMessage());
    }
  }


  public String printOptions() throws JsonProcessingException {
    if (session.isUserLoggedIn()) {
      return printUserOptions();
    } else if (session.isAdminLoggedIn()) {
      return printAdminOptions();
    } else return printWelcomeOptions();
  }

  private String printWelcomeOptions() throws JsonProcessingException {

    StringBuilder options = new StringBuilder();
    options.append("Type command: ").append("LOGIN,").append("REGISTER.");
    return response.printText(options.toString());
  }

  private String printAdminOptions() throws JsonProcessingException {
    StringBuilder options = new StringBuilder();
    options
        .append("Type command: ")
        .append("SEND, ")
        .append("READ_ALL, ")
        .append("READ_UNREAD, ")
        .append("UPDATE_USER, ")
        .append("DELETE_USER, ")
        .append("HELP, ")
        .append("LOGOUT.");
    return response.printText(options.toString());
  }

  private String printUserOptions() throws JsonProcessingException {
    StringBuilder options = new StringBuilder();
    options
        .append("Type command: ")
        .append("SEND, ")
        .append("READ_ALL, ")
        .append("READ_UNREAD, ")
        .append("HELP, ")
        .append("LOGOUT.");
    return response.printText(options.toString());
  }

  public enum Command {
    REGISTER,
    LOGIN,
    LOGOUT,
    DELETE_USER,
    SEND,
    READ_ALL,
    READ_UNREAD,
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
