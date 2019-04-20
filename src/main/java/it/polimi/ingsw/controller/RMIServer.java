package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.WrongPointException;
import it.polimi.ingsw.model.*;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RMI implementation of the server interface, it handles the RMI connections of all the game
 */
public class RMIServer extends UnicastRemoteObject implements Server, RMIConnHandler
{
    /**
     * Local binding registry
     */
    Registry registry;
    /**
     * List of new connections waiting to be handled by the game main thread
     */
    ArrayList<RMIConn> newConn = new ArrayList<>();

    /**
     * Instantiates the RMI server object, creating the main bind needed by clients to connect to the server
     * @throws RemoteException in case of binding errors
     */
    RMIServer() throws RemoteException {
        try {
            registry = LocateRegistry.createRegistry(1099);
            registry.bind("AM06", this);
        }
        catch(AlreadyBoundException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
        }
    }

    /**
     * Remote method invoked by the clients to match with the server. Clients create a bind with their own remote interface, so the server can interact with.
     * @param registryBind registry name of the client remote interface
     * @throws RemoteException in case of binding error
     * @throws AlreadyBoundException in case of already existing binding name
     * @throws NotBoundException If the RMI binding has been unsuccessful
     */
    public void newConnection(String registryBind) throws RemoteException, AlreadyBoundException, NotBoundException
    {
        Client clientInterface = (Client)registry.lookup(registryBind);
        RMIConn clientConn = new RMIConn(clientInterface, registryBind);
        newConn.add(clientConn);
        notifyAll();
    }

    /**
     * Pops the first object of the waiting connections list, to be retrieved to the main thread
     * @return First available connection
     */
    public Connection getConnection()
    {
        try {
            while (newConn.isEmpty()) wait();
            return newConn.remove(0);
        }
        catch (InterruptedException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
            return null;
        }

    }
}
