package it.polimi.ingsw.controller;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIConnHandler extends Remote
{
    void newConnection(String registryBind) throws RemoteException, AlreadyBoundException, NotBoundException;
}
