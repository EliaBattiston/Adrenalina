package it.polimi.ingsw.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SocketConn implements Connection {
    /**
     * Socket connection instance to referred player
     */
    private Socket playerSocket;
    /**
     * SocketStream input scanner
     */
    private Scanner in;
    /**
     * SocketStream output printer
     */
    private PrintWriter out;

    /**
     * Open socket reference to the player
     */
    SocketConn(Socket socket) {
        playerSocket = socket;
    }

    /**
     * Opens the writer and sends the message, then closes the writer
     * @param payload Content to be delivered to the client
     */
    public boolean send(String payload) {
        boolean success;
        try {
            out = new PrintWriter(playerSocket.getOutputStream());
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
     *returns the received string
     * @return received string (null in case of error)
     */
    public String receive() {
        String response;
        try {
            in = new Scanner(playerSocket.getInputStream());
            response = in.nextLine();
        }
        catch (IOException e) {
            response = null;
        }
        return response;
    }
}
