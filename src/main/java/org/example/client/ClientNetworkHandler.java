package org.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientNetworkHandler {

    private static final Logger logger = LogManager.getLogger(ClientNetworkHandler.class);
    private   Socket socket;
    private  PrintWriter out;
    private  BufferedReader in;
    private final ObjectMapper mapper = new ObjectMapper();

    public void connectToServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            logger.error("connection error {}", e.getMessage());
        }
        logger.info("Connected to server at {} : {}", ip, port);
    }

    public void sendRequest(String request) {
        out.println(request);
    }

    public String receiveResponse() throws IOException {
        return in.readLine();
    }

    public void closeConnection() throws IOException {
        socket.close();
        closeResources();
        logger.info("Disconnected from server");
    }

    public void printServerResponse(String jsonResp) {
        try {
            JsonNode rootNode = mapper.readTree(jsonResp);
            String prettyString = rootNode.toPrettyString();
            logger.info(prettyString);
        } catch (IOException e) {
            logger.error("Error processing JSON response: {}", e.getMessage());
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

    public Socket getSocket() {
        return socket;
    }
}
