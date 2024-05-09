package org.example.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerNetworkHandler {

    private static final Logger logger = LogManager.getLogger(ServerNetworkHandler.class);
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private ServerSocket serverSocket;

    public ServerNetworkHandler(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void acceptConnection() throws IOException {

        logger.info("Waiting for a client...");

        clientSocket = serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        logger.info("Client connected");
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("Error closing resources: {}", e.getMessage());
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
