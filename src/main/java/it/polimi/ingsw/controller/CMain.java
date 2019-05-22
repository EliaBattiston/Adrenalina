package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ServerDisconnectedException;
import it.polimi.ingsw.exceptions.ServerNotFoundException;
import it.polimi.ingsw.view.CLInterface;
import it.polimi.ingsw.view.GuiInterface;
import it.polimi.ingsw.view.UserInterface;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for the client's executable
 */
public class CMain
{
    /**
     * Endpoint for interface control
     */
    private UserInterface ui;

    /**
     * Creates a new CMain
     * @param gui True to start the GUI interface, false for CLI
     */
    public CMain(boolean gui)
    {
        Client connection;

        String ip;
        boolean socket;

        if(!gui) {
            ui = new CLInterface();
        }
        else {
            //TODO Instance GUI Interface
            ui = new GuiInterface();
        }

        //RMI or Socket?
        socket = !ui.useRMI();

        boolean instanced = false;
        do {

            try {
                if (socket) {
                    ip = ui.getIPAddress();
                    connection = new SocketClient(ip, 1906, ui);
                }
                else {

                    List<String> addresses = new ArrayList<>();
                    String localIP;

                    Enumeration<NetworkInterface> nInterfaces = NetworkInterface.getNetworkInterfaces();
                    while (nInterfaces.hasMoreElements()) {
                        Enumeration<InetAddress> inetAddresses = nInterfaces
                                .nextElement().getInetAddresses();
                        while (inetAddresses.hasMoreElements()) {
                            String address = inetAddresses.nextElement()
                                    .getHostAddress();
                            if (address.contains(".")) {
                                String[] split = address.split("\\.");
                                if(!split[0].equals("127") && !split[0].equals("169"))
                                    addresses.add(address);
                            }
                        }
                    }

                    if(addresses.size() > 1) {
                        localIP = ui.getLocalAddress(addresses);
                    }
                    else {
                        localIP = addresses.get(0);
                    }

                    System.setProperty("java.rmi.server.hostname", localIP);

                    ip = ui.getIPAddress();

                    connection = new RMIClient(ip, ui);
                }
                instanced = true;
                ui.generalMessage("Connesso al server Adrenalina");
            }
            catch (SocketException e) {
                ui.generalMessage("Impossibile trovate interfacce di rete, riprova\n");
                return;
            }
            catch (ServerNotFoundException e) {
                ui.generalMessage("Server non trovato, riprova\n");
            }
            catch (ServerDisconnectedException e) {
                ui.generalMessage("Server disconnesso inaspettatamente, rilancia il client e riprova\n");
                return;
            }
            catch (RemoteException e) {
                Logger.getGlobal().log(Level.SEVERE, e.toString(), e);
            }

        }
        while (!instanced);
    }

}
