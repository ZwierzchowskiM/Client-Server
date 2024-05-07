package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
            logger.error("starting connection");
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
                case "register" -> handleRegistration();
                case "login" -> response.printText(handleUserLogin());
                case "status" -> response.loggedUser(session.getUser());
                case "uptime" -> response.calculateUptime(startTime);
                case "help" -> response.printServerCommands(serverData.getCommandInfo());
                case "info" -> response.printServerInfo(serverData.getServerInfo());
                case "stop" -> stopServer();
                default -> (response.printText("Command unknown"));
            };
        } catch (IOException e) {
            logger.error("Error in generating JSON response");
            return "{\"error\": \"Internal server error\"}";
        }
    }

    private void sendMessageClient(String msg) {
        out.println(msg);
    }

    private String handleRegistration() throws IOException {

        String infoReg = "Please provide username, password and user role";
        sendMessageClient(response.printText(infoReg));

        String username = in.readLine();
        String password = in.readLine();
        String role = in.readLine();

        User registeredUser = userDataService.addUser(username, password, role);

        return response.registerUser(registeredUser);
    }

    private String handleUserLogin() throws IOException {

        String infoReg = "{\"request\": \"Please provide username and password\"}";
        sendMessageClient(response.printText(infoReg));

        String username = in.readLine();
        String password = in.readLine();

        String infoLog;
        if (userDataService.isValidCredentials(username, password)) {
            User user = userDataService.getUser(username);
            UserDTO userDTO = new UserDTO(user.getUsername(), user.getRole());
            session.setUser(userDTO);
            infoLog = "User successfully logged in";

        } else {
            infoLog = "User not logged in";
        }

        return infoLog;
    }

    private String stopServer() {
        try {
            serverSocket.close();
            return "{\"info\": \"server stopped\"}";
        } catch (IOException e) {
            logger.error("Error closing server" + e.getMessage());
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
            logger.error("Failed to close resources: " + e.getMessage());
        }
    }
}
