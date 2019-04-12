package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The class implements the Server for establishing the client-server connection.
 */
public class SocketServer implements Server {
    /**
     * port on which the communication is taken
     */
    private int port;
    /**
     * ServerSocket instance
     */
    private ServerSocket serverSocket;

    /**
     * Thread handler
     */
    ExecutorService executor;

    /**
     * Create a new SocketServer instance starting a server socket at the given port
     * @param port port on which the server is started
     */
    public SocketServer(int port)
    {
        this.port = port;
        try {
            startServer();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void startServer() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // Porta non disponibile
            return;
        }
        System.out.println("Server socket ready on port: " + port);

    }

    public Connection getConnection()
    {
        try {
            Socket connSocket = serverSocket.accept();
            return new SocketConn(connSocket);
        }
        catch (IOException e) {
            return null;
        }

    }

    public void StopServer() {
        executor.shutdown();
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
