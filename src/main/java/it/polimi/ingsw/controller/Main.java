package it.polimi.ingsw.controller;

import java.util.Arrays;
import java.util.List;

/**
 * Main class of Adrenalina, it lets you choose if you want to start server/client/AI
 */
public class Main {

    private static final String LOCALHOST = "localhost";

    public static void main(String[] args) {

        System.setProperty("java.security.policy", "AM06.policy");

        List<String> flags = Arrays.asList(args);
        if(flags.contains("-c"))
            new CMain(false);
        else if(flags.contains("-g"))
            new CMain(true);
        else if(flags.contains("-s")) {
            if(flags.contains("-l"))
                new SMain(LOCALHOST);
            else
                new SMain(null);
        }
        else if(flags.contains("-ai")){
            new AIMain(LOCALHOST, false, false);
        }
        else if(flags.contains("-gai")){
            new AIMain(LOCALHOST, true, false);
        }
        else {
            println("Flag errati");
            println("\t-s\tAvvia Server");
            println("\t-c\tAvvia CLI Client");
            println("\t-g\tAvvia GUI Client");
            println("\t-ai\tAvvia CLI AIClient");
            println("\t-gai\tAvvia GUI AIClient");
        }
    }

    private static void println(String s) {
        System.out.println(s);
    }
}
