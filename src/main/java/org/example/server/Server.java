package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.user.User;
import org.example.user.UserDTO;
import org.example.user.UserDataService;
import org.example.utils.CredentialsValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

public class Server {

    private static Logger logger = LogManager.getLogger(Server.class);
    private ServerSocket serverSocket;
    private Instant startTime;
    private ServerData serverData;
    private UserDataService userDataService;
    private PrintWriter out;
    private BufferedReader in;
    Session session = new Session();
    ServerResponse response = new ServerResponse();

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Error creating server" + e.getMessage());
            throw new RuntimeException(e);
        }
        serverData = new ServerData();
        userDataService = new UserDataService();
        startTime = Instant.now();
        logger.info("Server started on port " + port);
    }

    public static void main(String[] args) {

        Server server = new Server(6666);
        try {
            server.start();
        } catch (RuntimeException e) {
            logger.error("Starting connection");
        }
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = acceptConnection();
                handleClient(clientSocket);
            } catch (IOException e) {
                logger.error("Error connecting client" + e.getMessage());
            } finally {
                closeResources();
            }
        }
    }

    private Socket acceptConnection() throws IOException {
        logger.info("Waiting for a client...");
        Socket clientSocket = serverSocket.accept();
        logger.info("Client connected");
        return clientSocket;
    }

    private void handleClient(Socket clientSocket) throws IOException {

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        try {
            String clientRequest;
            while ((clientRequest = in.readLine()) != null) {
                logger.info("Client request: " + clientRequest);
                String response = handleRequest(clientRequest);
                sendMessageClient(response);
            }
        } catch (IOException e) {
            logger.error("Error handling client: " + e.getMessage());
        }
    }

    public String handleRequest(String request) throws JsonProcessingException {
        try {
            return switch (request) {
                case "register" -> response.registerUser(handleRegistration());
                case "login" -> response.printLoginStatus(handleUserLogin());
                case "logout" -> response.printText(handleUserLogout());
                case "status" -> response.currentLoggedUser(session.getUser());
                case "delete" -> response.printText(handleUserDelete());
                case "uptime" -> response.calculateUptime(startTime);
                case "help" -> response.printServerCommands(serverData.getCommandInfo());
                case "info" -> response.printServerInfo(serverData.getServerInfo());
                case "stop" -> response.printText(stopServer());
                default -> response.printText("Command unknown");
            };
        } catch (IOException e) {
            logger.error("Error in generating JSON response");
            return response.printError(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return response.printError(e.getMessage());
        }
    }


    private User handleRegistration() throws IOException, IllegalArgumentException {

        sendMessageClient(response.printText("Please provide username, password and user role"));

        String username = in.readLine();
        String password = in.readLine();
        String role = in.readLine();

        CredentialsValidator.validateUsername(username);
        CredentialsValidator.validatePassword(password);
        CredentialsValidator.validateRole(role);

        return userDataService.addUser(username, password, role);
    }


    private boolean handleUserLogin() throws IOException, IllegalArgumentException {

        sendMessageClient(response.printText("Please provide username and password"));

        String username = in.readLine();
        String password = in.readLine();

        CredentialsValidator.validateUsername(username);
        CredentialsValidator.validatePassword(password);

        boolean loginSuccessful;
        if (userDataService.isValidCredentials(username, password)) {
            User user = userDataService.getUser(username);
            UserDTO userDTO = new UserDTO(user.getUsername(), user.getRole());
            session.setUser(userDTO);
            loginSuccessful = true;
        } else {
            loginSuccessful = false;
        }
        return loginSuccessful;
    }

    private String handleUserLogout() throws IOException, IllegalArgumentException {

        session.setUser(null);

        return "Logout successful";
    }

    private String handleUserDelete() throws IOException {

        sendMessageClient(response.printText("Please provide username"));

        String username = in.readLine();

        String infoLog;

        if (userDataService.delete(username)) {
            infoLog = "User successfully deleted";
        } else {
            infoLog = "Failed to delete user or user does not exist";
        }

        return infoLog;
    }

    private void sendMessageClient(String msg) {
        out.println(msg);
    }

    private String stopServer() {
        try {
            serverSocket.close();
            return "Server stopped";
        } catch (IOException e) {
            logger.error("Error closing server: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void closeResources() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            logger.error("Failed to close resources: {}", e.getMessage());
        }
    }
}
