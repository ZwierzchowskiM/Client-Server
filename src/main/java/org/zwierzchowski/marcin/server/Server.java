package org.zwierzchowski.marcin.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.zwierzchowski.marcin.message.Message;
import org.zwierzchowski.marcin.message.MessageService;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.user.UserDataService;
import org.zwierzchowski.marcin.utils.CredentialsValidator;
import org.zwierzchowski.marcin.utils.MessageValidator;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Instant;
import java.util.List;

@Log4j2
public class Server {

    private ServerSocket serverSocket;
    private Instant startTime;
    private ServerData serverData;
    private UserDataService userDataService;
    private Session session = new Session();
    private ServerResponse response = new ServerResponse();
    private ServerNetworkHandler serverNetworkHandler;
    private MessageService messageService = new MessageService();

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("Error creating server", e);
            log.info("Application is shutting down...");
            System.exit(1);
        }
        serverData = new ServerData();
        userDataService = new UserDataService();
        startTime = Instant.now();
        serverNetworkHandler = new ServerNetworkHandler(serverSocket);
        log.info("Server started on port {}", port);
    }

    public static void main(String[] args) {

        Server server = new Server(6666);
        server.start();
    }

    public void start() {

        try {
            serverNetworkHandler.acceptConnection();
        } catch (IOException e) {
            log.error("Failed to connect client", e);
            System.exit(1);
        }
        handleClient();
    }

    private void handleClient() {
        try {
            String clientRequest;
            serverNetworkHandler.sendMessage(response.printText("Type command"));

            while ((clientRequest = serverNetworkHandler.receiveMessage()) != null) {
                log.info("Client request: {}", clientRequest);

                Command command = Command.fromString(clientRequest);
                String serverResponse = handleRequest(command);
                if ("STOP_SERVER".equals(serverResponse)) {
                    serverNetworkHandler.sendMessage(response.printText("Shutting down server"));
                    break;
                }
                serverNetworkHandler.sendMessage(serverResponse);
                serverNetworkHandler.sendMessage(response.printText("Type command"));
            }
        } catch (IOException e) {
            log.error("Error handling client", e);
        } finally {
            serverNetworkHandler.close();
        }
    }

    public String handleRequest(Command command) throws JsonProcessingException {
        try {
            return switch (command) {
                case REGISTER -> handleRegistration();
                case LOGIN -> handleUserLogin();
                case LOGOUT -> handleUserLogout();
                case DELETE -> handleUserDelete();
                case SEND -> handleSendMessage();
                case READ -> handleReadMessages();
                case UPTIME -> response.calculateUptime(startTime);
                case HELP -> response.printServerCommands(serverData.getCommandInfo());
                case INFO -> response.printServerInfo(serverData.getServerInfo());
                case STOP -> "STOP_SERVER";
                case UNKNOWN -> response.printText("Command unknown");
            };
        } catch (JsonProcessingException e) {
            log.error("JSON processing error", e);
            return response.printError(e.getMessage());
        } catch (IOException e) {
            log.error("IO error", e);
            return response.printError(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("{}", e.getMessage());
            return response.printError(e.getMessage());
        }
    }

    private String handleRegistration() throws IOException {
        serverNetworkHandler.sendMessage(response.printText("Please provide username"));
        String username = serverNetworkHandler.receiveMessage();
        CredentialsValidator.validateUsername(username);

        serverNetworkHandler.sendMessage(response.printText("Please provide password"));
        String password = serverNetworkHandler.receiveMessage();
        CredentialsValidator.validatePassword(password);

        serverNetworkHandler.sendMessage(response.printText("Please provide user role"));
        String role = serverNetworkHandler.receiveMessage();
        CredentialsValidator.validateRole(role);

        User user =  userDataService.addUser(username, password, role);

        return response.printText("User: " +user.getUsername() +" successfully registered");
    }


    private String handleUserLogin() throws IOException {
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

    private String handleUserLogout() throws JsonProcessingException {
        if (!session.isUserLoggedIn()) {
            return response.printText("No user logged in");
        }
        session.logoutUser();
        return response.printText("Logout successful");
    }

    private String handleUserDelete() throws IOException {

        if (session.isAdminLoggedIn()) {
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

    private String handleSendMessage() throws IOException {

        if (!session.isUserLoggedIn()) {
            return response.printText("User needs to log in");
        }
        String infoLog;
        serverNetworkHandler.sendMessage(response.printText("Please provide recipient"));
        String recipient = serverNetworkHandler.receiveMessage();

        serverNetworkHandler.sendMessage(response.printText("Please provide  message"));
        String content = serverNetworkHandler.receiveMessage();

        MessageValidator.validateMessage(content);
        String sender = session.getLoggedInUser().getUsername();
        infoLog = messageService.sendMessage(recipient, content, sender);
        return response.printText(infoLog);
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
        REGISTER, LOGIN, LOGOUT, DELETE, SEND, READ, UPTIME, HELP, INFO, STOP, UNKNOWN;

        public static Command fromString(String command) {
            try {
                return Command.valueOf(command.toUpperCase());
            } catch (IllegalArgumentException e) {
                return UNKNOWN;
            }
        }
    }
}
