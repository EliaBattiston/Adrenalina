package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class SocketConn implements Connection {
    /**
     * Socket connection instance to referred player
     */
    private Socket playerSocket;

    /**
     * Open socket reference to the player
     */
    SocketConn(Socket socket)
    {
        playerSocket = socket;
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available)
    {
        Gson gson = new Gson();
        Payload load = new Payload();
        load.type = Interaction.CHOOSEACTION;
        load.parameters = gson.toJson(available);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Action> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Action>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @return Chosen weapon
     */
    public Weapon chooseWeapon(List<Weapon> available)
    {
        Gson gson = new Gson();
        Payload load = new Payload();
        load.type = Interaction.CHOOSEWEAPON;
        load.parameters = gson.toJson(available);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Weapon>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable)
    {
        Gson gson = new Gson();
        Payload load = new Payload();
        load.type = Interaction.GRABWEAPON;
        load.parameters = gson.toJson(grabbable);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Weapon>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @return Weapons to be reloaded
     */
    public List<Weapon> reload(List<Weapon> reloadable)
    {
        Gson gson = new Gson();
        Payload load = new Payload();
        load.type = Interaction.RELOAD;
        load.parameters = gson.toJson(reloadable);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Weapon>>(){}.getType());
        return ansParam;
    }

    /**
     * Asks the user where he wants to move
     * @param destinations Possible destinations for the user
     * @return Point where the player will be when he's done moving
     */
    public Point move(List<Point> destinations)
    {
        Gson gson = new Gson();
        Payload load = new Payload();
        load.type = Interaction.MOVE;
        load.parameters = gson.toJson(destinations);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Point> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Point>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets)
    {
        Gson gson = new Gson();
        Payload load = new Payload();
        load.type = Interaction.CHOOSETARGET;
        load.parameters = gson.toJson(targets);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Player> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Player>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user where to move an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @return Point where the enemy will be after being moved
     */

    public Point displace(Player enemy, List<Point> destinations)
    {
        Gson gson = new Gson();
        Payload load = new Payload();
        load.type = Interaction.DISPLACE;
        load.parameters = gson.toJson(destinations);
        load.enemy = enemy;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Point> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Point>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers)
    {
        Gson gson = new Gson();
        Payload load = new Payload();
        load.type = Interaction.DISCARDPOWER;
        load.parameters = gson.toJson(powers);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Power> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Power>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Opens the writer and sends the message, then closes the writer
     * @param payload Content to be delivered to the client
     */
    private boolean send(String payload) {
        boolean success;
        try {
            PrintWriter out = new PrintWriter(playerSocket.getOutputStream());
            out.println(payload);
            out.flush();
            success = true;
        }
        catch (IOException e) {
            success = false;
        }
        return success;
    }

    /**
     *returns the received string
     * @return received string (null in case of error)
     */
    private String receive() {
        String response;
        try {
            Scanner in = new Scanner(playerSocket.getInputStream());
            response = in.nextLine();
        }
        catch (IOException e) {
            response = null;
        }
        return response;
    }

    /**
     * Deserialize a json representing the class
     * @param response the string containing the json representation of the class
     * @return the object made from the json
     */
    public static Payload jsonDeserialize(String response)
    {
        Gson gson = new Gson();
        return gson.fromJson(response, Payload.class);
    }
}
