package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.ServerDisconnectedException;
import it.polimi.ingsw.exceptions.ServerNotFoundException;
import it.polimi.ingsw.view.AInterface;
import it.polimi.ingsw.view.GuiAInterface;
import it.polimi.ingsw.view.UserInterface;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Main class for the AInterface
 */
public class AIMain
{
    private static final int SOCKETPORT = 1906;
    private static final String LOCALHOST = "localhost";

    /**
     * Endpoint for interface control
     */
    private UserInterface ui;

    public static void main(String[] args) {
        System.setProperty("java.security.policy", "AM06.policy");
        List<String> flags = Arrays.asList(args);

        boolean rmi = false;
        if(flags.contains("-rmi"))
            rmi = true;

        String host = LOCALHOST;
        if(flags.contains("-ip")) {
            String ip = flags.get(flags.indexOf("-ip") + 1);
            if(checkIP(ip)) {
                host = ip;
            }
        }

        if(flags.contains("-g"))
            new AIMain(host, true, rmi);
        else
            new AIMain(host, false, rmi);
    }

    /**
     * Creates a new AIMain referring a specified IP address for the server
     * @param ip server IP
     * @param gui if true shows the GUI, otherwise the CLI
     * @param rmi if true uses RMI, otherwise SOCKETS
     */
    public AIMain(String ip, boolean gui, boolean rmi)
    {
        if(gui)
            ui = new GuiAInterface();
        else
            ui = new AInterface();

        try {
            if(rmi) {
                String localIP = "";
                List<String> addresses = new ArrayList<>();

                Enumeration<NetworkInterface> nInterfaces = NetworkInterface.getNetworkInterfaces();
                while (nInterfaces.hasMoreElements()) {
                    Enumeration<InetAddress> inetAddresses = nInterfaces
                            .nextElement().getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        String address = inetAddresses.nextElement()
                                .getHostAddress();
                        if (address.contains(".")) {
                            addresses.add(address);
                        }
                    }
                }

                if(addresses.size() > 1 && !ip.equals(LOCALHOST)) {
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
                }
                else {
                    localIP = addresses.get(0);
                }

                System.setProperty("java.rmi.server.hostname", localIP);

                new RMIClient(ip, ui);
            }
            else
                new SocketClient(ip, SOCKETPORT, ui);
        }
        catch (ServerNotFoundException | RemoteException e) {
            ui.generalMessage("Server non trovato, riprova\n");
        }
        catch (ServerDisconnectedException | SocketException e) {
            ui.generalMessage("Server disconnesso inaspettatamente, rilancia il client e riprova\n");
        }
    }

    private static boolean checkIP(String ip) {
        String[] pieces;
        pieces = ip.split("\\.");
        if(ip.equals(LOCALHOST))
            return true;
        else {
            if(pieces.length != 4)
                return false;
            for(String piece: pieces) {
                int n = Integer.parseInt(piece);
                if(n > 255)
                    return false;
            }
            return true;
        }
    }
}
