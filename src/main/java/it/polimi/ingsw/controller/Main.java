package it.polimi.ingsw.controller;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.setProperty("java.security.policy", "AM06.policy");
        //System.setProperty("java.rmi.server.logCalls", "true");
        //System.setSecurityManager(null);

        List<String> flags = Arrays.asList(args);
        if(flags.contains("-c"))
            new CMain(false);
        else if(flags.contains("-g"))
            new CMain(true);
        else if(flags.contains("-s")) {
            if(flags.contains("-l"))
                new SMain("localhost");
            else
                new SMain(null);
        }
        else if(flags.contains("-ai")){
            new AIMain();
        }
        else {
            System.out.println("Flag errati");
            System.out.println("\t-s\tAvvia Server");
            System.out.println("\t-c\tAvvia CLI Client");
            System.out.println("\t-g\tAvvia GUI Client");
            System.out.println("\t-ai\tAvvia CLI AIClient");
        }
    }
}
