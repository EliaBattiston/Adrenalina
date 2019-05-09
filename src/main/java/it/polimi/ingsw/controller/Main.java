package it.polimi.ingsw.controller;

import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> flags = Arrays.asList(args);
        if(flags.contains("-c")) {
            boolean gui = false;
            if(flags.contains("-g"))
                gui = true;

            CMain base = new CMain(gui);
        }
        else if(flags.contains("-s")) {
            SMain server = new SMain();

        }
        else {
            System.out.println("Flag errati");
            System.out.println("\t-s\tAvvia come Server");
            System.out.println("\t-c\tAvvia come Client");
            System.out.println("\t-g\tInterfaccia grafica per il Client, utilizzabile solo con flag -c");
        }
    }
}
