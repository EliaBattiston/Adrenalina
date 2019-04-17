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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SocketClient implements Client {
    /**
     * Socket connection instance to the game server
     */
    private Socket serverSocket;

    /**
     * Open socket reference to the server
     */
    SocketClient(String ipAddr, int port) {
        try {
            serverSocket = new Socket(ipAddr, port);
        }
        catch (IOException e) { }
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available)
    {
        return (Action)available.get(0);
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @return Chosen weapon
     */
    public Weapon chooseWeapon(List<Weapon> available)
    {
        return available.get(0);
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable)
    {
        return grabbable.get(0);
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @return Weapons to be reloaded
     */
    public List<Weapon> reload(List<Weapon> reloadable)
    {
        return reloadable;
    }

    /**
     * Asks the user where he wants to move
     * @param destinations Possible destinations for the user
     * @return Point where the player will be when he's done moving
     */
    public Point move(List<Point> destinations)
    {
        return destinations.get(0);
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets)
    {
        return targets.get(0);
    }

    /**
     * Asks the user where to move an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @return Point where the enemy will be after being moved
     */

    public Point displace(Player enemy, List<Point> destinations)
    {
        System.out.println(enemy.getNick() + " -> (" + destinations.get(0).getX() + ", " + destinations.get(0).getY() + ")");
        return destinations.get(0);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers) { return powers.get(0); }

    /**
     * Opens the writer and sends the message to the server, then closes the writer
     * @param payload Content to be delivered to the client
     * @return true on success, false in case of connection error
     */
    public boolean send(String payload) {
        boolean success = false;
        try {
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream());
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
     *deserializes the received string from server and executes it
     */
    public void receive() {
        try {
            Gson gson = new Gson();

            String response;
            Scanner in = new Scanner(serverSocket.getInputStream());
            response = in.nextLine();

            Payload message = gson.fromJson(response, Payload.class);
            Payload answer = new Payload();
            switch (message.type) {
                case CHOOSEACTION: {
                    ArrayList<Action> param = gson.fromJson(message.parameters, new TypeToken<List<Action>>() {
                    }.getType());
                    answer.type = Interaction.CHOOSEACTION;
                    ArrayList<Action> ansParam = new ArrayList<>();
                    ansParam.add(chooseAction(param));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case CHOOSEWEAPON: {
                    ArrayList<Weapon> param = gson.fromJson(message.parameters, new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.type = Interaction.CHOOSEWEAPON;
                    ArrayList<Weapon> ansParam = new ArrayList<>();
                    ansParam.add(chooseWeapon(param));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case GRABWEAPON: {
                    ArrayList<Weapon> param = gson.fromJson(message.parameters, new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.type = Interaction.GRABWEAPON;
                    ArrayList<Weapon> ansParam = new ArrayList<>();
                    ansParam.add(grabWeapon(param));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case RELOAD: {
                    ArrayList<Weapon> param = gson.fromJson(message.parameters, new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.type = Interaction.RELOAD;
                    answer.parameters = gson.toJson(reload(param));
                    break;
                }
                case MOVE: {
                    ArrayList<Point> param = gson.fromJson(message.parameters, new TypeToken<List<Point>>() {
                    }.getType());
                    answer.type = Interaction.MOVE;
                    ArrayList<Point> ansParam = new ArrayList<>();
                    ansParam.add(move(param));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case CHOOSETARGET: {
                    ArrayList<Player> param = gson.fromJson(message.parameters, new TypeToken<List<Player>>() {
                    }.getType());
                    answer.type = Interaction.CHOOSETARGET;
                    ArrayList<Player> ansParam = new ArrayList<>();
                    ansParam.add(chooseTarget(param));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case DISPLACE: {
                    ArrayList<Point> param = gson.fromJson(message.parameters, new TypeToken<List<Point>>() {
                    }.getType());
                    answer.type = Interaction.DISPLACE;
                    ArrayList<Point> ansParam = new ArrayList<>();
                    ansParam.add(displace(message.enemy, param));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case DISCARDPOWER: {
                    ArrayList<Power> param = gson.fromJson(message.parameters, new TypeToken<List<Power>>() {
                    }.getType());
                    answer.type = Interaction.DISCARDPOWER;
                    ArrayList<Power> ansParam = new ArrayList<>();
                    ansParam.add(discardPower(param));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                default:
                    answer = null;
            }
            send(jsonSerialize(answer));
        }
        catch (IOException e) { }
    }

    /**
     * Serializes the content of the class in json
     * @return Json serialization of the class
     */
    public String jsonSerialize(Payload payload)
    {
        Gson gson = new Gson();
        return gson.toJson(payload);
    }
}
