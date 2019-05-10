package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.MatchView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketConn implements Connection {
    /**
     * Socket connection instance to referred player
     */
    private Socket playerSocket;

    private Gson gson;

    /**
     * Open socket reference to the player
     * @param socket Player's socket
     */
    SocketConn(Socket socket)
    {
        playerSocket = socket;

        GsonBuilder gsonBilder = new GsonBuilder();
        gsonBilder.registerTypeAdapter(Cell.class, new CellAdapter());
        gson = gsonBilder.create();
    }

    /**
     * Send the actual matchView to the client
     * @param matchView current match view
     */
    @Override
    public void updateGame(MatchView matchView) {
        Payload load = new Payload();
        load.type = Interaction.UPDATEVIEW;
        load.parameters = gson.toJson(matchView);
        send(gson.toJson(load));
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen action
     */
    public Action chooseAction(List<Action> available, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.CHOOSEACTION;
        load.parameters = gson.toJson(available);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Action> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Action>>(){}.getType());

        String lambdaID = ansParam.get(0).getLambdaID();
        for(Action a : available)
            if(a.getLambdaID().equals(lambdaID))
                return a;
        return null;
    }

    /**
     * Asks the user to choose between a set of his weapons
     * @param available List of available weapons
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen weapon
     */
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.CHOOSEWEAPON;
        load.parameters = gson.toJson(available);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Weapon>>(){}.getType());

        int id = ansParam.get(0).getId();
        for(Weapon w : available)
            if(w.getId() == id)
                return w;
        return null;
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     * @param grabbable List of weapons that can be picked up by the player
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen weapon
     */
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.GRABWEAPON;
        load.parameters = gson.toJson(grabbable);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Weapon>>(){}.getType());

        int id = ansParam.get(0).getId();
        for(Weapon w : grabbable)
            if(w.getId() == id)
                return w;
        return null;
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     * @param reloadable Weapons that are currently not loaded
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Weapon to be reloaded
     */
    public Weapon reload(List<Weapon> reloadable, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.RELOAD;
        load.parameters = gson.toJson(reloadable);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Weapon>>(){}.getType());

        int id = ansParam.get(0).getId();
        for(Weapon w : reloadable)
            if(w.getId() == id)
                return null; //TODO change to w after merge with fixed "reload"
        return null;
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen target
     */
    public Player chooseTarget(List<Player> targets, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.CHOOSETARGET;
        load.parameters = gson.toJson(targets);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Player> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Player>>(){}.getType());

        String nickChosen = ansParam.get(0).getNick();
        for(Player p : targets)
            if(p.getNick().equals(nickChosen))
                return p;
        return null;
    }

    /**
     * Asks the user where he wants to movePlayer
     * @param destinations Possible destinations for the user
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Point where the player will be when he's done moving
     */
    public Point movePlayer(List<Point> destinations, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.MOVEPLAYER;
        load.parameters = gson.toJson(destinations);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Point> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Point>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Point where the enemy will be after being moved
     */

    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.MOVEENEMY;
        load.parameters = gson.toJson(destinations);
        load.mustChoose = mustChoose;
        load.enemy = enemy;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Point> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Point>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Card to be discarded
     */
    public Power discardPower(List<Power> powers, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.DISCARDPOWER;
        load.parameters = gson.toJson(powers);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Power> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Power>>(){}.getType());

        int id = ansParam.get(0).getId();
        return powers.stream().filter(p->p.getId()==id).findFirst().get();
    }

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen room
     */
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.CHOOSEROOM;
        load.parameters = gson.toJson(rooms);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Integer> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Integer>>(){}.getType());

        return rooms.stream().filter( r -> r.equals(ansParam.get(0))).findFirst().get();
    }

    /**
     * Asks the player to choose a direction
     * @param possible Directions you can choose
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     */
    public Direction chooseDirection(List<Direction> possible, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.CHOOSEDIRECTION;
        load.parameters = gson.toJson(possible);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Direction> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Direction>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen position
     */
    public Point choosePosition(List<Point> positions, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.CHOOSEPOSITION;
        load.parameters = gson.toJson(positions);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Point> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Point>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user for the nickname
     * @return user's nickname
     */
    public String getNickname() {
        Payload load = new Payload();
        load.type = Interaction.GETNICKNAME;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<String> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<String>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     */
    public String getPhrase() {
        Payload load = new Payload();
        load.type = Interaction.GETPHRASE;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<String> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<String>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose)
    {
        Payload load = new Payload();
        load.type = Interaction.DISCARDWEAPON;
        load.parameters = gson.toJson(inHand);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Weapon>>(){}.getType());

        return inHand.stream().filter(w -> w.getId() == ansParam.get(0).getId()).findFirst().get();
    }


    /**
     * Asks the user for the fighter
     * @return user's fighter
     */
    public Fighter getFighter() {
        Payload load = new Payload();
        load.type = Interaction.GETFIGHTER;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Fighter> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Fighter>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     */
    public Integer getSkullNum() {
        Payload load = new Payload();
        load.type = Interaction.GETSKULLSNUM;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Integer> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Integer>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to choose which map he wants to use
     * @return Number of the chosen map
     */
    public Integer chooseMap() {
        Payload load = new Payload();
        load.type = Interaction.CHOOSEMAP;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Integer> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Integer>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     */
    public Boolean chooseFrenzy() {
        Payload load = new Payload();
        load.type = Interaction.CHOOSEFRENZY;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Boolean> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Boolean>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose) {
        Payload load = new Payload();
        load.type = Interaction.CHOOSEPOWER;
        load.parameters = gson.toJson(inHand);
        load.mustChoose = mustChoose;
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Power> ansParam = gson.fromJson(answer.parameters, new TypeToken<List<Power>>(){}.getType());

        return inHand.stream().filter(p -> p.getId() == ansParam.get(0).getId()).findFirst().get();
    }



    /**
     * Opens the writer and sends the message, then closes the writer
     * @param payload Content to be delivered to the client
     * @return If true, the payload was correctly sent
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
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
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
            Logger.getGlobal().log( Level.SEVERE, e.toString(), e );
            response = null;
        }
        return response;
    }

    /**
     * Deserialize a json representing the class
     * @param response the string containing the json representation of the class
     * @return the object made from the json
     */
    public Payload jsonDeserialize(String response)
    {
        return gson.fromJson(response, Payload.class);
    }

}
