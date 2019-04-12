package it.polimi.ingsw.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient implements Client {
    /**
     * Socket connection instance to the game server
     */
    private Socket serverSocket;
    /**
     * SocketStream input scanner
     */
    private Scanner in;
    /**
     * SocketStream output printer
     */
    private PrintWriter out;

    /**
     * Open socket reference to the server
     */
    SocketClient(String ipAddr, int port) {
        try {
            serverSocket = new Socket(ipAddr, port);
        }
        catch (IOException e) {
            return;
        }
    }

    /**
     * Opens the writer and sends the message to the server, then closes the writer
     * @param payload Content to be delivered to the client
     * @return true on success, false in case of connection error
     */
    public boolean send(String payload) {
        boolean success = false;
        try {
            out = new PrintWriter(serverSocket.getOutputStream());
            out.println(payload);
            out.flush();
            success = true;
        }
        catch (IOException e) {
            success = false;
        }
        return success;
    }

    /**
     *returns the received string from server
     * @return received string (null in case of error)
     */
    public String receive() {
        String response;
        try {
            in = new Scanner(serverSocket.getInputStream());
            response = in.nextLine();
        }
        catch (IOException e) {
            response = null;
        }
        return response;
    }
}
