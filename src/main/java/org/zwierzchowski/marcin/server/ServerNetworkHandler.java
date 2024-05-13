package org.zwierzchowski.marcin.server;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Log4j2
public class ServerNetworkHandler {

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private ServerSocket serverSocket;

    public ServerNetworkHandler(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void acceptConnection() throws IOException {

        log.info("Waiting for a client...");

        clientSocket = serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        log.info("Client connected");
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
            log.error("Error closing resources: {}", e.getMessage());
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
            log.error("Failed to close resources: {}", e.getMessage());
        }
    }
}
