package it.polimi.ingsw.controller;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> flags = Arrays.asList(args);
        if(flags.contains("-c")) {
            boolean gui = false;
            if(flags.contains("-g"))
                gui = true;

            new CMain(gui);
        }
        else if(flags.contains("-s"))
            new SMain();
        else {
            System.out.println("Flag errati");
            System.out.println("\t-s\tAvvia come Server");
            System.out.println("\t-c\tAvvia come Client");
            System.out.println("\t-g\tInterfaccia grafica per il Client, utilizzabile solo con flag -c");
        }
    }
}
