package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final Scanner scanner = new Scanner(System.in);
    private static final String CLIENT_IP = "127.0.0.1";
    private static final int CLIENT_PORT = 6666;


    public static void main(String[] args) throws IOException {

        Client client = new Client();
        client.startConnection(CLIENT_IP,CLIENT_PORT);

    }

    public void startConnection(String ip, int port) throws IOException {

        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.println("Client started");

        while (clientSocket.isConnected()){

            String input = scanner.nextLine();

            switch (input) {
                case "uptime", "info","help" -> sendMessage(input);
                case "stop" -> {
                    sendMessage("stop");
                    stopConnection();
                }
                default -> System.out.println("unknown");
            };
        }
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        System.out.println(resp);
        return resp;
    }

    public void stopConnection() throws IOException {
        System.out.println("stopping connection");
        in.close();
        out.close();
        clientSocket.close();
    }
}