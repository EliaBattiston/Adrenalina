package it.polimi.ingsw.controller;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIConnHandler extends Remote
{
    void newConnection(Client clientInterface) throws RemoteException, AlreadyBoundException, NotBoundException;
}
