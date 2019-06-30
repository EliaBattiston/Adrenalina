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
    private final static int defaultStartMatch = 60;
    private final static int defaultTurn = 180;
    private final static int defaultMinPlayers = 3;

    private final int startMatchSeconds;
    private final int playerTurnSeconds;
    private final int minPlayers;

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
        startMatchSeconds = defaultStartMatch;
        playerTurnSeconds = defaultTurn;
        minPlayers = defaultMinPlayers;
    }

    public int getStartMatchSeconds()
    {
        return startMatchSeconds;
    }

    public int getPlayerTurnSeconds()
    {
        return playerTurnSeconds;
    }

    public int getMinPlayers() {
        return minPlayers;
    }
}
