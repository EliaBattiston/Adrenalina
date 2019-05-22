package it.polimi.ingsw.controller;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RMI implementation of the server interface, it handles the RMI connections of all the game
 */
public class RMIServer extends UnicastRemoteObject implements Server, RMIConnHandler, Serializable
{
    /**
     * Local binding registry
     */
    private Registry registry;
    /**
     * List of new connections waiting to be handled by the game main thread
     */
    private ArrayList<RMIConn> newConn;

    /**
     * Instantiates the RMI server object, creating the main bind needed by clients to connect to the server
     * @throws RemoteException in case of binding errors
     */
    RMIServer() throws RemoteException {
        newConn = new ArrayList<>();

        try {
            registry = LocateRegistry.createRegistry(1099);
            registry.bind("AM06", this);
        }
        catch(Exception e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
        }


        Runtime.getRuntime().addShutdownHook(
        new Thread("app-shutdown-hook") {
            @Override
            public void run() {
                try {
                    UnicastRemoteObject.unexportObject(registry, true);
                }
                catch (NoSuchObjectException e) { ; }
            }
        });
    }

    /**
     * Remote method invoked by the clients to match with the server. Clients create a bind with their own remote interface, so the server can interact with.
     * @throws RemoteException in case of binding error
     * @throws AlreadyBoundException in case of already existing binding name
     * @throws NotBoundException If the RMI binding has been unsuccessful
     */
    public synchronized void newConnection(Client clientInterface) throws RemoteException, AlreadyBoundException, NotBoundException
    {
        RMIConn clientConn = new RMIConn(clientInterface);
        newConn.add(clientConn);
        notifyAll();
    }

    /**
     * Pops the first object of the waiting connections list, to be retrieved to the main thread
     * @return First available connection
     */
    public synchronized Connection getConnection()
    {
        try {
            while (newConn.isEmpty())
                wait();

            return newConn.remove(0);
        }
        catch (InterruptedException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
            Thread.currentThread().interrupt(); //sonarqube
            return null;
        }

    }

    public boolean ping() {
        return true;
    }
}
