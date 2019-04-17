package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.WrongPointException;
import it.polimi.ingsw.model.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

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
     * Create a new SocketServer instance starting a server socket at the given port
     * @param port port on which the server is started
     */
    public SocketServer(int port) throws IOException
    {
        this.port = port;
        startServer();
    }

    /**
     * Starts the Socket server instance
     * @throws IOException in case of connection errors
     */
    private void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
    }

    /**
     * Accepts the first incoming Socket connection, then creates a new SocketConn object and returns it
     * @return SocketConn object refering the new incoming socket connection
     */
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

    /**
     * Stops the Socket server
     * @throws IOException in case of connection errors
     */
    public void stopServer() throws IOException {
        serverSocket.close();
    }
}
