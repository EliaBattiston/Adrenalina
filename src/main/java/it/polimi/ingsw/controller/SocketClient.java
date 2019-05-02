package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketClient implements Client {
    /**
     * Socket connection instance to the game server
     */
    private Socket serverSocket;

    /**
     * Open socket reference to the server
     * @param ipAddr IP address of the server
     * @param port TCP port of the Server's socket
     */
    SocketClient(String ipAddr, int port) {
        try {
            serverSocket = new Socket(ipAddr, port);
        }
        catch (IOException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
        }
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available, boolean mustChoose)
    {
        return (Action)available.get(0);
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @return Chosen weapon
     */
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose)
    {
        return available.get(0);
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose)
    {
        return grabbable.get(0);
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @return Weapons to be reloaded
     */
    public List<Weapon> reload(List<Weapon> reloadable, boolean mustChoose)
    {
        return reloadable;
    }

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @return Point where the player will be when he's done moving
     */
    public Point movePlayer(List<Point> destinations, boolean mustChoose)
    {
        return destinations.get(0);
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets, boolean mustChoose)
    {
        return targets.get(0);
    }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @return Point where the enemy will be after being moved
     */

    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose)
    {
        System.out.println(enemy.getNick() + " -> (" + destinations.get(0).getX() + ", " + destinations.get(0).getY() + ")");
        return destinations.get(0);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) { return powers.get(0); }

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen room
     */
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) { return rooms.get(0); }

    /**
     * Asks the player to choose a direction
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen direction
     */
    public Direction chooseDirection(boolean mustChoose) { return Direction.NORTH; }

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen position
     */
    public Point choosePosition(List<Point> positions, boolean mustChoose) {return positions.get(0); }

    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose) { return inHand.get(0); }


    /**
     * Asks the user for the nickname
     * @return user's nickname
     */
    public String getNickname() {return "pippo"; }

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     */
    public String getPhrase() {return "Yodellah-iihh-oohh!"; }

    /**
     * Asks the user fot the fighter
     * @return user's fighter
     */
    public Fighter getFighter() {return Fighter.DSTRUTTOR3; }

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     */
    public Integer getSkullNum() { return 5; }

    /**
     * Asks the user to choose which map he wants to use
     * @param mapList List of possible maps
     * @return Chosen map
     */
    public Map chooseMap(List<Map> mapList) {
        return mapList.get(0);
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     */
    public Boolean chooseFrenzy() {
        return true;
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose) {
        return inHand.get(0);
    }


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
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
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
                    ansParam.add(chooseAction(param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case CHOOSEWEAPON: {
                    ArrayList<Weapon> param = gson.fromJson(message.parameters, new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.type = Interaction.CHOOSEWEAPON;
                    ArrayList<Weapon> ansParam = new ArrayList<>();
                    ansParam.add(chooseWeapon(param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case GRABWEAPON: {
                    ArrayList<Weapon> param = gson.fromJson(message.parameters, new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.type = Interaction.GRABWEAPON;
                    ArrayList<Weapon> ansParam = new ArrayList<>();
                    ansParam.add(grabWeapon(param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case RELOAD: {
                    ArrayList<Weapon> param = gson.fromJson(message.parameters, new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.type = Interaction.RELOAD;
                    answer.parameters = gson.toJson(reload(param, message.mustChoose));
                    break;
                }
                case MOVEPLAYER: {
                    ArrayList<Point> param = gson.fromJson(message.parameters, new TypeToken<List<Point>>() {
                    }.getType());
                    answer.type = Interaction.MOVEPLAYER;
                    ArrayList<Point> ansParam = new ArrayList<>();
                    ansParam.add(movePlayer(param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case CHOOSETARGET: {
                    ArrayList<Player> param = gson.fromJson(message.parameters, new TypeToken<List<Player>>() {
                    }.getType());
                    answer.type = Interaction.CHOOSETARGET;
                    ArrayList<Player> ansParam = new ArrayList<>();
                    ansParam.add(chooseTarget(param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case MOVEENEMY: {
                    ArrayList<Point> param = gson.fromJson(message.parameters, new TypeToken<List<Point>>() {
                    }.getType());
                    answer.type = Interaction.MOVEENEMY;
                    ArrayList<Point> ansParam = new ArrayList<>();
                    ansParam.add(moveEnemy(message.enemy, param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case DISCARDPOWER: {
                    ArrayList<Power> param = gson.fromJson(message.parameters, new TypeToken<List<Power>>() {
                    }.getType());
                    answer.type = Interaction.DISCARDPOWER;
                    ArrayList<Power> ansParam = new ArrayList<>();
                    ansParam.add(discardPower(param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case CHOOSEROOM: {
                    ArrayList<Integer> param = gson.fromJson(message.parameters, new TypeToken<List<Integer>>() {
                    }.getType());
                    answer.type = Interaction.CHOOSEROOM;
                    ArrayList<Integer> ansParam = new ArrayList<>();
                    ansParam.add(chooseRoom(param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case CHOOSEDIRECTION: {
                    answer.type = Interaction.CHOOSEDIRECTION;
                    ArrayList<Direction> ansParam = new ArrayList<>();
                    ansParam.add(chooseDirection(message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case CHOOSEPOSITION: {
                    ArrayList<Point> param = gson.fromJson(message.parameters, new TypeToken<List<Point>>() {
                    }.getType());
                    answer.type = Interaction.CHOOSEPOSITION;
                    ArrayList<Point> ansParam = new ArrayList<>();
                    ansParam.add(choosePosition(param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case GETNICKNAME: {
                    answer.type = Interaction.GETNICKNAME;
                    ArrayList<String> ansParam = new ArrayList<>();
                    ansParam.add(getNickname());
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case GETPHRASE: {
                    answer.type = Interaction.GETPHRASE;
                    ArrayList<String> ansParam = new ArrayList<>();
                    ansParam.add(getPhrase());
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case GETFIGHTER: {
                    answer.type = Interaction.GETFIGHTER;
                    ArrayList<Fighter> ansParam = new ArrayList<>();
                    ansParam.add(getFighter());
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case GETSKULLSNUM: {
                    answer.type = Interaction.GETSKULLSNUM;
                    ArrayList<Integer> ansParam = new ArrayList<>();
                    ansParam.add(getSkullNum());
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case DISCARDWEAPON: {
                    ArrayList<Weapon> param = gson.fromJson(message.parameters, new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.type = Interaction.DISCARDWEAPON;
                    ArrayList<Weapon> ansParam = new ArrayList<>();
                    ansParam.add(discardWeapon(param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case CHOOSEMAP: {
                    ArrayList<Map> param = gson.fromJson(message.parameters, new TypeToken<List<Map>>() {
                    }.getType());
                    answer.type = Interaction.CHOOSEMAP;
                    ArrayList<Map> ansParam = new ArrayList<>();
                    ansParam.add(chooseMap(param));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case CHOOSEFRENZY: {
                    answer.type = Interaction.CHOOSEFRENZY;
                    ArrayList<Boolean> ansParam = new ArrayList<>();
                    ansParam.add(chooseFrenzy());
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                case CHOOSEPOWER: {
                    ArrayList<Power> param = gson.fromJson(message.parameters, new TypeToken<List<Power>>() {
                    }.getType());
                    answer.type = Interaction.CHOOSEPOWER;
                    ArrayList<Power> ansParam = new ArrayList<>();
                    ansParam.add(choosePower(param, message.mustChoose));
                    answer.parameters = gson.toJson(ansParam);
                    break;
                }
                default:
                    answer = null;
            }
            send(jsonSerialize(answer));
        }
        catch (IOException e) {
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
        }
    }

    /**
     * Serializes the content of the class in json
     * @param payload Payload to serialize
     * @return Json serialization of the class
     */
    public String jsonSerialize(Payload payload)
    {
        Gson gson = new Gson();
        return gson.toJson(payload);
    }
}
