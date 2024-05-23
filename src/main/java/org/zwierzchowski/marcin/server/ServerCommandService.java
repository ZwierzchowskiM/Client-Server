package org.zwierzchowski.marcin.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.zwierzchowski.marcin.exception.InvalidCredentialsException;
import org.zwierzchowski.marcin.exception.InvalidMessageException;
import org.zwierzchowski.marcin.exception.UserInboxIsFullException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.message.Message;
import org.zwierzchowski.marcin.message.MessageService;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.user.UserDataService;
import org.zwierzchowski.marcin.utils.CredentialsValidator;
import org.zwierzchowski.marcin.utils.MessageValidator;

import java.io.IOException;
import java.util.List;

import static org.zwierzchowski.marcin.server.ServerCommandService.Command.*;

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
        case DELETE -> handleUserDelete();
        case SEND -> handleSendMessage();
        case READ -> handleReadMessages();
        case STOP -> handleStopServer();
        case UPTIME -> response.calculateUptime(serverData.getStartTime());
        case HELP -> response.printServerCommands(serverData.getCommandsInfo());
        case INFO -> response.printServerInfo(serverData.getSeverInfo());
        case UNKNOWN -> response.printText("Command unknown");
      };
    } catch (JsonProcessingException e) {
      log.error("JSON processing error", e);
      return response.printError(e.getMessage());
    } catch (IOException e) {
      log.error("IO error", e);
      return response.printError(e.getMessage());
    } catch (InvalidCredentialsException e) {
      log.error("Invalid Credentials format", e);
      return response.printError(e.getMessage());
    }
  }

  private String handleRegistration() throws IOException, InvalidCredentialsException {
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

  private String handleUserLogin() throws IOException, InvalidCredentialsException {
    serverNetworkHandler.sendMessage(response.printText("Please provide username"));
    String username = serverNetworkHandler.receiveMessage();

    serverNetworkHandler.sendMessage(response.printText("Please provide password"));
    String password = serverNetworkHandler.receiveMessage();

    CredentialsValidator.validateUsername(username);
    CredentialsValidator.validatePassword(password);

    if (userDataService.isValidCredentials(username, password)) {
      User user = userDataService.getUser(username);
      session.setLoggedInUser(user);
      return response.printLoginStatus(true);
    } else {
      return response.printLoginStatus(false);
    }
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

  private String handleUserDelete() throws IOException {

    if (!session.isAdminLoggedIn()) {
      return response.printText("Admin privileges are required to delete a user.");
    }
    serverNetworkHandler.sendMessage(response.printText("Please provide username"));
    String username = serverNetworkHandler.receiveMessage();

    if (userDataService.delete(username)) {
      return response.printText("User successfully deleted");
    } else {
      return response.printText("Failed to delete user or user does not exist");
    }
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

  private String handleReadMessages() throws IOException {

    if (!session.isUserLoggedIn()) {
      return response.printText("User needs to log in");
    }
    User user = session.getLoggedInUser();
    List<Message> unreadMessages = messageService.getUnreadMessages(user.getUsername());
    if (unreadMessages.isEmpty()) {
      return response.printText("No unread messages");
    }
    return response.printUnreadMessages(unreadMessages);
  }

  public enum Command {
    REGISTER,
    LOGIN,
    LOGOUT,
    DELETE,
    SEND,
    READ,
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
