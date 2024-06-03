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
  private ServerData serverData;
  private ServerCommandService serverCommandService;
  private Session session = new Session();
  private ServerResponse response = new ServerResponse();
  private ServerNetworkHandler serverNetworkHandler;

  public Server(int port) {
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      log.error("Error creating server", e);
      log.info("Application is shutting down...");
      System.exit(1);
    }
    serverData = new ServerData();
    serverNetworkHandler = new ServerNetworkHandler(serverSocket);
    serverCommandService = new ServerCommandService(serverNetworkHandler, session, serverData);
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
      serverNetworkHandler.sendMessage(serverCommandService.printOptions());

      while ((clientRequest = serverNetworkHandler.receiveMessage()) != null) {
        log.info("Client request: {}", clientRequest);

        String serverResponse = serverCommandService.handleRequest(clientRequest);
        if ("STOP_SERVER".equals(serverResponse)) {
          break;
        }
        serverNetworkHandler.sendMessage(serverResponse);
        serverNetworkHandler.sendMessage(serverCommandService.printOptions());
      }
    } catch (IOException e) {
      log.error("Error handling client", e);
    } finally {
      serverNetworkHandler.close();
    }
  }
}
