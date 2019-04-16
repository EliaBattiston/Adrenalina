package it.polimi.ingsw.controller;

import java.rmi.*;

public interface RMIConnHandler extends Remote
{
    void newConnection(String registryBind) throws RemoteException, AlreadyBoundException, NotBoundException;
}
