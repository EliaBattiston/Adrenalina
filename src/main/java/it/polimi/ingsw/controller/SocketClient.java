package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.exceptions.ServerDisconnectedException;
import it.polimi.ingsw.exceptions.ServerNotFoundException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.MatchView;
import it.polimi.ingsw.view.UserInterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketClient implements Client {
    /**
     * Socket connection instance to the game server
     */
    private Socket serverSocket;

    /**
     * User gui/cli interface
     */
    private UserInterface user;

    /**
     * Open socket reference to the server
     * @param ipAddr IP address of the server
     * @param port TCP port of the Server's socket
     * @param userint gui/cli interface instance
     */
    public SocketClient(String ipAddr, int port, UserInterface userint) throws ServerNotFoundException, ServerDisconnectedException {
        user = userint;
        boolean instanced = false;
        do {
            try {
                serverSocket = new Socket(ipAddr, port);
                instanced = true;
            }
            catch (UnknownHostException | ConnectException e) {
                throw new ServerNotFoundException();
            }
            catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, e.toString(), e);
            }
        } while(!instanced);

        try {
            while (true)
                receive();
        }
        catch (ServerDisconnectedException e) {
            throw new ServerDisconnectedException();
        }
    }

    /**
     * Receive the actual gameView
     * @param matchView current game view
     */
    public void updateGame(MatchView matchView)
    {
        user.updateGame(matchView);
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available, boolean mustChoose)
    {
        return user.chooseAction(available, mustChoose);
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @return Chosen weapon
     */
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose)
    {
        return user.chooseWeapon(available, mustChoose);
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose)
    {
        return user.grabWeapon(grabbable, mustChoose);
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @return Weapon to be reloaded
     */
    public Weapon reload(List<Weapon> reloadable, boolean mustChoose)
    {
        return user.reload(reloadable, mustChoose);
    }

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @return Point where the player will be when he's done moving
     */
    public Point movePlayer(List<Point> destinations, boolean mustChoose)
    {
        return user.movePlayer(destinations, mustChoose);
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets, boolean mustChoose)
    {
        return user.chooseTarget(targets, mustChoose);
    }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @return Point where the enemy will be after being moved
     */

    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose)
    {
        return user.moveEnemy(enemy, destinations, mustChoose);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) { return user.discardPower(powers, mustChoose); }

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen room
     */
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) { return user.chooseRoom(rooms, mustChoose); }

    /**
     * Asks the player to choose a direction
     * @param possible Directions you can choose
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     */
    public Direction chooseDirection(List<Direction> possible, boolean mustChoose) { return user.chooseDirection(possible, mustChoose); }

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen position
     */
    public Point choosePosition(List<Point> positions, boolean mustChoose) {return user.choosePosition(positions, mustChoose); }

    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose) { return user.discardWeapon(inHand, mustChoose); }


    /**
     * Asks the user for the nickname
     * @return user's nickname
     */
    public String getNickname() {

        return user.getNickname();
    }

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     */
    public String getPhrase() {
        return user.getPhrase();
    }

    /**
     * Asks the user fot the fighter
     * @param available List of available fighters
     * @return user's fighter
     */
    public Fighter getFighter(List<Fighter> available) {
        return user.getFighter(available);
    }

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     */
    public Integer getSkullNum() { return user.getSkullNum(); }

    /**
     * Asks the user to choose which map he wants to use
     * @return Number of the chosen map
     */
    public Integer chooseMap() {
        return user.chooseMap();
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     */
    public Boolean chooseFrenzy() {
        return user.chooseFrenzy();
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose) {
        return user.choosePower(inHand, mustChoose);
    }

    /**
     * Sends a general message to the user to be displayed
     * @param payload Message payload
     */
    public void sendMessage(String payload) {
        user.generalMessage(payload);
    }


    /**
     * Opens the writer and sends the message to the server, then closes the writer
     * @param payload Content to be delivered to the client
     * @return true on success, false in case of connection error
     */
    public boolean send(String payload) throws ServerDisconnectedException {
        boolean success = false;
        try {
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream());
            out.println(payload);
            out.flush();
            success = true;
        }
        catch(NoSuchElementException e) {
            throw new ServerDisconnectedException();
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
    public void receive() throws ServerDisconnectedException {
        try {
            Gson gson;

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Cell.class, new CellAdapter());
            gson = gsonBuilder.create();

            String response;
            Scanner in = new Scanner(serverSocket.getInputStream());
            response = in.nextLine();

            Payload message = gson.fromJson(response, Payload.class);
            Payload answer = new Payload();
            switch (message.getType()) {
                case CHOOSEACTION: {
                    ArrayList<Action> param = gson.fromJson(message.getParameters(), new TypeToken<List<Action>>() {
                    }.getType());
                    answer.setType(Interaction.CHOOSEACTION);
                    ArrayList<Action> ansParam = new ArrayList<>();
                    ansParam.add(chooseAction(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case CHOOSEWEAPON: {
                    ArrayList<Weapon> param = gson.fromJson(message.getParameters(), new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.setType(Interaction.CHOOSEWEAPON);
                    ArrayList<Weapon> ansParam = new ArrayList<>();
                    ansParam.add(chooseWeapon(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case GRABWEAPON: {
                    ArrayList<Weapon> param = gson.fromJson(message.getParameters(), new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.setType(Interaction.GRABWEAPON);
                    ArrayList<Weapon> ansParam = new ArrayList<>();
                    ansParam.add(grabWeapon(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case RELOAD: {
                    ArrayList<Weapon> param = gson.fromJson(message.getParameters(), new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.setType(Interaction.RELOAD);
                    ArrayList<Weapon> ansParam = new ArrayList<>();
                    ansParam.add(reload(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case MOVEPLAYER: {
                    ArrayList<Point> param = gson.fromJson(message.getParameters(), new TypeToken<List<Point>>() {
                    }.getType());
                    answer.setType(Interaction.MOVEPLAYER);
                    ArrayList<Point> ansParam = new ArrayList<>();
                    ansParam.add(movePlayer(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case CHOOSETARGET: {
                    ArrayList<Player> param = gson.fromJson(message.getParameters(), new TypeToken<List<Player>>() {
                    }.getType());
                    answer.setType(Interaction.CHOOSETARGET);
                    ArrayList<Player> ansParam = new ArrayList<>();
                    ansParam.add(chooseTarget(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case MOVEENEMY: {
                    ArrayList<Point> param = gson.fromJson(message.getParameters(), new TypeToken<List<Point>>() {
                    }.getType());
                    answer.setType(Interaction.MOVEENEMY);
                    ArrayList<Point> ansParam = new ArrayList<>();
                    ansParam.add(moveEnemy(message.getEnemy(), param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case DISCARDPOWER: {
                    ArrayList<Power> param = gson.fromJson(message.getParameters(), new TypeToken<List<Power>>() {
                    }.getType());
                    answer.setType(Interaction.DISCARDPOWER);
                    ArrayList<Power> ansParam = new ArrayList<>();
                    ansParam.add(discardPower(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case CHOOSEROOM: {
                    ArrayList<Integer> param = gson.fromJson(message.getParameters(), new TypeToken<List<Integer>>() {
                    }.getType());
                    answer.setType(Interaction.CHOOSEROOM);
                    ArrayList<Integer> ansParam = new ArrayList<>();
                    ansParam.add(chooseRoom(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case CHOOSEDIRECTION: {
                    ArrayList<Direction> param = gson.fromJson(message.getParameters(), new TypeToken<List<Direction>>() {
                    }.getType());
                    answer.setType(Interaction.CHOOSEDIRECTION);
                    ArrayList<Direction> ansParam = new ArrayList<>();
                    ansParam.add(chooseDirection(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case CHOOSEPOSITION: {
                    ArrayList<Point> param = gson.fromJson(message.getParameters(), new TypeToken<List<Point>>() {
                    }.getType());
                    answer.setType(Interaction.CHOOSEPOSITION);
                    ArrayList<Point> ansParam = new ArrayList<>();
                    ansParam.add(choosePosition(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case GETNICKNAME: {
                    answer.setType(Interaction.GETNICKNAME);
                    ArrayList<String> ansParam = new ArrayList<>();
                    ansParam.add(getNickname());
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case GETPHRASE: {
                    answer.setType(Interaction.GETPHRASE);
                    ArrayList<String> ansParam = new ArrayList<>();
                    ansParam.add(getPhrase());
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case GETFIGHTER: {
                    ArrayList<Fighter> param = gson.fromJson(message.getParameters(), new TypeToken<List<Fighter>>() {
                    }.getType());
                    answer.setType(Interaction.GETFIGHTER);
                    ArrayList<Fighter> ansParam = new ArrayList<>();
                    ansParam.add(getFighter(param));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case GETSKULLSNUM: {
                    answer.setType(Interaction.GETSKULLSNUM);
                    ArrayList<Integer> ansParam = new ArrayList<>();
                    ansParam.add(getSkullNum());
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case DISCARDWEAPON: {
                    ArrayList<Weapon> param = gson.fromJson(message.getParameters(), new TypeToken<List<Weapon>>() {
                    }.getType());
                    answer.setType(Interaction.DISCARDWEAPON);
                    ArrayList<Weapon> ansParam = new ArrayList<>();
                    ansParam.add(discardWeapon(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case CHOOSEMAP: {
                    answer.setType(Interaction.CHOOSEMAP);
                    ArrayList<Integer> ansParam = new ArrayList<>();
                    ansParam.add(chooseMap());
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case CHOOSEFRENZY: {
                    answer.setType(Interaction.CHOOSEFRENZY);
                    ArrayList<Boolean> ansParam = new ArrayList<>();
                    ansParam.add(chooseFrenzy());
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case CHOOSEPOWER: {
                    ArrayList<Power> param = gson.fromJson(message.getParameters(), new TypeToken<List<Power>>() {}.getType());
                    answer.setType(Interaction.CHOOSEPOWER);
                    ArrayList<Power> ansParam = new ArrayList<>();
                    ansParam.add(choosePower(param, message.isMustChoose()));
                    answer.setParameters(gson.toJson(ansParam));
                    break;
                }
                case UPDATEVIEW: {
                    MatchView param = gson.fromJson(message.getParameters(), MatchView.class);
                    updateGame(param);
                    break;
                }
                case MESSAGE: {
                    String param = gson.fromJson(message.getParameters(), String.class);
                    sendMessage(param);
                    break;
                }
                default:
                    answer.setType(null);
            }
            send(jsonSerialize(answer));
        }
        catch (NoSuchElementException e) {
            throw new ServerDisconnectedException();
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
