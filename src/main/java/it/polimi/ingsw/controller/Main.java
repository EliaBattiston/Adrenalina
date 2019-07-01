package it.polimi.ingsw.controller;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.setProperty("java.security.policy", "AM06.policy");

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
            new AIMain(false, false);
        }
        else if(flags.contains("-gai")){
            new AIMain(true, false);
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
