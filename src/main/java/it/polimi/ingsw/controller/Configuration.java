package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Singleton which contains the configuration of the current instance of the server.
 * If there is no config.json file, settings are set to default values
 */
public class Configuration
{
    private int startMatchSeconds;
    private int playerTurnSeconds;

    private static Configuration instance;

    public static Configuration getInstance()
    {
        if(instance == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            try
            {
                JsonReader file = new JsonReader(new FileReader("config.json"));
                instance = gson.fromJson(file, Configuration.class);
            }
            catch(FileNotFoundException e)
            {
                instance = new Configuration();
            }

        }
        return instance;
    }

    public Configuration()
    {
        startMatchSeconds = 60;
        playerTurnSeconds = 120;
    }

    public static void main(String[] args)
    {
        Configuration c = Configuration.getInstance();

        System.out.println(c.getStartMatchSeconds());
        System.out.println(c.getPlayerTurnSeconds());

    }

    public int getStartMatchSeconds()
    {
        return startMatchSeconds;
    }

    public int getPlayerTurnSeconds()
    {
        return playerTurnSeconds;
    }
}
