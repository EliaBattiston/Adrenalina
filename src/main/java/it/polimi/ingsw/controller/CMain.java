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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
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

    public static void main(String[] args) {
        System.setProperty("java.security.policy", "AM06.policy");
        List<String> flags = Arrays.asList(args);
        if(flags.contains("-g"))
            new CMain(true);
        else
            new CMain(false);
    }

    /**
     * Creates a new CMain
     * @param gui True to start the GUI interface, false for CLI
     */
    public CMain(boolean gui)
    {
        Client connection;

        String ip, localIP;
        boolean socket;

        localIP = null;

        if(!gui) {
            ui = new CLInterface();
        }
        else {
            ui = new GuiInterface();
        }

        //RMI or Socket?
        socket = !ui.useRMI();

        boolean instanced = false;
        do {
            ip = ui.getIPAddress();

            try {
                if (socket) {
                    connection = new SocketClient(ip, 1906, ui);
                }
                else {

                    List<String> addresses = new ArrayList<>();

                    Enumeration<NetworkInterface> nInterfaces = NetworkInterface.getNetworkInterfaces();
                    while (nInterfaces.hasMoreElements()) {
                        Enumeration<InetAddress> inetAddresses = nInterfaces
                                .nextElement().getInetAddresses();
                        while (inetAddresses.hasMoreElements()) {
                            String address = inetAddresses.nextElement()
                                    .getHostAddress();
                            if (address.contains(".")) {
                                String[] split = address.split("\\.");
                                //if(!split[0].samePoint("127") && !split[0].samePoint("169") && !split[0].samePoint("172"))
                                addresses.add(address);
                            }
                        }
                    }

                    if(addresses.size() > 1 && !ip.equals("localhost")) {
                        int maxeq = 0;
                        for(String address: addresses) {
                            int i;
                            for(i = 0; i < address.length() && address.charAt(i) == ip.charAt(i); i++);
                            if(i > maxeq) {
                                localIP = address;
                                maxeq = i;
                            }
                        }

                        if(maxeq == 0) {
                            List<String> reducedAddresses = new ArrayList<>();
                            reducedAddresses.addAll(addresses);

                            for(String address: addresses) {
                                String[] split = address.split("\\.");
                                if(split[0].equals("127") || split[0].equals("169") || split[0].equals("172"))
                                    reducedAddresses.remove(address);
                            }
                            localIP = reducedAddresses.get(0);
                        }

                        //localIP = ui.getLocalAddress(addresses);
                    }
                    else {
                        localIP = addresses.get(0);
                    }

                    System.setProperty("java.rmi.server.hostname", localIP);

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
                //the EXACT same string is used to check if this message has been sent inside the GUI, change it here and change it there if you need
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
