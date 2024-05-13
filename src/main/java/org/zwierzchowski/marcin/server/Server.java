package org.zwierzchowski.marcin.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.zwierzchowski.marcin.message.MessageService;
import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.user.UserDTO;
import org.zwierzchowski.marcin.user.UserDataService;
import org.zwierzchowski.marcin.utils.CredentialsValidator;
import org.zwierzchowski.marcin.utils.MessageValidator;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Instant;

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
            log.error("Error creating server {}", e.getMessage());
            throw new RuntimeException("Failed to create server on port " + port,e);
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
        serverNetworkHandler.acceptConnection();
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
                case REGISTER -> response.registerUser(handleRegistration());
                case LOGIN -> response.printLoginStatus(handleUserLogin());
                case LOGOUT -> response.printText(handleUserLogout());
                case DELETE -> response.printText(handleUserDelete());
                case SEND -> response.printText(handleSendMessage());
                case READ -> null;
                case UPTIME -> response.calculateUptime(startTime);
                case HELP -> response.printServerCommands(serverData.getCommandInfo());
                case INFO -> response.printServerInfo(serverData.getServerInfo());
                case STOP -> "STOP_SERVER";
                case UNKNOWN -> response.printText("Command unknown");
            };
        } catch (IOException e) {
            log.error("Error in generating JSON response");
            return response.printError(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return response.printError(e.getMessage());
        }
    }

    private User handleRegistration() throws IOException, IllegalArgumentException {

        serverNetworkHandler.sendMessage(response.printText("Please provide username"));
        String username = serverNetworkHandler.receiveMessage();
        CredentialsValidator.validateUsername(username);

        serverNetworkHandler.sendMessage(response.printText("Please provide password "));
        String password = serverNetworkHandler.receiveMessage();
        CredentialsValidator.validatePassword(password);

        serverNetworkHandler.sendMessage(response.printText("Please provide user role"));
        String role = serverNetworkHandler.receiveMessage();
        CredentialsValidator.validateRole(role);

        return userDataService.addUser(username, password, role);
    }


    private boolean handleUserLogin() throws IOException, IllegalArgumentException {

        serverNetworkHandler.sendMessage(response.printText("Please provide username"));
        String username = serverNetworkHandler.receiveMessage();

        serverNetworkHandler.sendMessage(response.printText("Please provide password"));
        String password = serverNetworkHandler.receiveMessage();

        CredentialsValidator.validateUsername(username);
        CredentialsValidator.validatePassword(password);

        boolean loginSuccessful;

        if (userDataService.isValidCredentials(username, password)) {
            User user = userDataService.getUser(username);
            UserDTO userDTO = new UserDTO(user.getUsername(), user.getRole());
            session.setLoggedInUser(userDTO);
            loginSuccessful = true;
        } else {
            loginSuccessful = false;
        }
        return loginSuccessful;
    }

    private String handleUserLogout() {

        session.logoutUser();
        return "Logout successful";
    }

    private String handleUserDelete() throws IOException {

        serverNetworkHandler.sendMessage(response.printText("Please provide username"));
        String username = serverNetworkHandler.receiveMessage();

        String infoLog;
        if (userDataService.delete(username)) {
            infoLog = "User successfully deleted";
        } else {
            infoLog = "Failed to delete user or user does not exist";
        }
        return infoLog;
    }

    private String handleSendMessage() throws IOException {

        String infoLog;

        serverNetworkHandler.sendMessage(response.printText("Please provide recipient"));
        String recipient = serverNetworkHandler.receiveMessage();

        serverNetworkHandler.sendMessage(response.printText("Please provide  message"));
        String content = serverNetworkHandler.receiveMessage();
        if (userDataService.isUserExisting(recipient)) {
            MessageValidator.validateMessage(content);
            String sender = session.getLoggedInUser().username();
            infoLog = messageService.sendMessage(recipient, content, sender);
        } else {
            infoLog = "Recipient not existing";
        }
        return infoLog;
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
