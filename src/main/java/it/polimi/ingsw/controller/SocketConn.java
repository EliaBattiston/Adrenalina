package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.exceptions.ClientDisconnectedException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.MatchView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.NoSuchElementException;
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
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    @Override
    public void updateGame(MatchView matchView) throws ClientDisconnectedException {
        Payload load = new Payload();
        load.setType(Interaction.UPDATEVIEW);
        load.setParameters(gson.toJson(matchView));
        send(gson.toJson(load));
    }

    /**
     * Asks the user to choose between a set of actions he can use
     * @param available List of available actions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen action
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Action chooseAction(List<Action> available, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.CHOOSEACTION);
        load.setParameters(gson.toJson(available));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Action> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Action>>(){}.getType());

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
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.CHOOSEWEAPON);
        load.setParameters(gson.toJson(available));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Weapon>>(){}.getType());

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
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.GRABWEAPON);
        load.setParameters(gson.toJson(grabbable));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Weapon>>(){}.getType());

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
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Weapon reload(List<Weapon> reloadable, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.RELOAD);
        load.setParameters(gson.toJson(reloadable));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Weapon>>(){}.getType());

        int id = ansParam.get(0).getId();
        for(Weapon w : reloadable)
            if(w.getId() == id)
                return w;
        return null;
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     * @param targets List of player that can be targeted
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Chosen target
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Player chooseTarget(List<Player> targets, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.CHOOSETARGET);
        load.setParameters(gson.toJson(targets));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Player> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Player>>(){}.getType());

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
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Point movePlayer(List<Point> destinations, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.MOVEPLAYER);
        load.setParameters(gson.toJson(destinations));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Point> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Point>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user where to movePlayer an enemy
     * @param enemy Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Point where the enemy will be after being moved
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */

    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.MOVEENEMY);
        load.setParameters(gson.toJson(destinations));
        load.setMustChoose(mustChoose);
        load.setEnemy(enemy);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Point> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Point>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to discard one power card
     * @param powers List of power cards in player's hand
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return Card to be discarded
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Power discardPower(List<Power> powers, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.DISCARDPOWER);
        load.setParameters(gson.toJson(powers));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Power> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Power>>(){}.getType());

        int id = ansParam.get(0).getId();
        return powers.stream().filter(p->p.getId()==id).findFirst().orElse(null);
    }

    /**
     * Asks the user to choose a room
     * @param rooms list of possible rooms
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen room
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.CHOOSEROOM);
        load.setParameters(gson.toJson(rooms));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Integer> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Integer>>(){}.getType());

        return rooms.stream().filter( r -> r.equals(ansParam.get(0))).findFirst().orElse(null);
    }

    /**
     * Asks the player to choose a direction
     * @param possible Directions you can choose
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Direction chooseDirection(List<Direction> possible, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.CHOOSEDIRECTION);
        load.setParameters(gson.toJson(possible));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Direction> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Direction>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to choose a precise position on the map
     * @param positions list of possible positions
     * @param mustChoose boolean indicating if the player can choose NOT to answer (true: must choose, false: can avoid to choose)
     * @return chosen position
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Point choosePosition(List<Point> positions, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.CHOOSEPOSITION);
        load.setParameters(gson.toJson(positions));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Point> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Point>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user for the nickname
     * @return user's nickname
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public String getNickname() throws ClientDisconnectedException {
        Payload load = new Payload();
        load.setType(Interaction.GETNICKNAME);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<String> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<String>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user for the effect phrase
     * @return user's effect phrase
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public String getPhrase() throws ClientDisconnectedException {
        Payload load = new Payload();
        load.setType(Interaction.GETPHRASE);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<String> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<String>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to choose which weapon to discard
     * @param inHand List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose) throws ClientDisconnectedException
    {
        Payload load = new Payload();
        load.setType(Interaction.DISCARDWEAPON);
        load.setParameters(gson.toJson(inHand));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Weapon> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Weapon>>(){}.getType());

        return inHand.stream().filter(w -> w.getId() == ansParam.get(0).getId()).findFirst().orElse(null);
    }


    /**
     * Asks the user for the fighter
     * @return user's fighter
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Fighter getFighter() throws ClientDisconnectedException {
        Payload load = new Payload();
        load.setType(Interaction.GETFIGHTER);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Fighter> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Fighter>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user how many skulls he wants in the play
     * @return skulls number
     */
    public Integer getSkullNum() throws ClientDisconnectedException {
        Payload load = new Payload();
        load.setType(Interaction.GETSKULLSNUM);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Integer> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Integer>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to choose which map he wants to use
     * @return Number of the chosen map
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Integer chooseMap() throws ClientDisconnectedException {
        Payload load = new Payload();
        load.setType(Interaction.CHOOSEMAP);
        send(gson.toJson(load));
        String debug = receive();
        System.out.println(debug);
        Payload answer = jsonDeserialize(debug);
        System.out.println(answer.getParameters());
        List<Integer> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Integer>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     * @return True for final Frenzy mode, false elsewhere
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Boolean chooseFrenzy() throws ClientDisconnectedException {
        Payload load = new Payload();
        load.setType(Interaction.CHOOSEFRENZY);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Boolean> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Boolean>>(){}.getType());
        return ansParam.get(0);
    }

    /**
     * Asks the user to choose a power to use
     * @param inHand List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    public Power choosePower(List<Power> inHand, boolean mustChoose) throws ClientDisconnectedException {
        Payload load = new Payload();
        load.setType(Interaction.CHOOSEPOWER);
        load.setParameters(gson.toJson(inHand));
        load.setMustChoose(mustChoose);
        send(gson.toJson(load));
        Payload answer = jsonDeserialize(receive());
        List<Power> ansParam = gson.fromJson(answer.getParameters(), new TypeToken<List<Power>>(){}.getType());

        return inHand.stream().filter(p -> p.getId() == ansParam.get(0).getId()).findFirst().orElse(null);
    }

    /**
     * Sends a general message to the user to be displayed
     * @param payload Message payload
     * @throws ClientDisconnectedException
     */
    public void sendMessage(String payload) throws ClientDisconnectedException {
        Payload load = new Payload();
        load.setType(Interaction.MESSAGE);
        load.setParameters(gson.toJson(payload));
        send(gson.toJson(load));
    }



    /**
     * Opens the writer and sends the message, then closes the writer
     * @param payload Content to be delivered to the client
     * @return If true, the payload was correctly sent
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    private boolean send(String payload) throws ClientDisconnectedException {
        boolean success;
        try {
            PrintWriter out = new PrintWriter(playerSocket.getOutputStream());
            out.println(payload);
            out.flush();
            success = true;
        }
        catch (NoSuchElementException e) {
            throw new ClientDisconnectedException();
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
     * @throws ClientDisconnectedException in case of client unexpected disconnection
     */
    private String receive() throws ClientDisconnectedException {
        String response;
        try {
            Scanner in = new Scanner(playerSocket.getInputStream());
            response = in.nextLine();
        }
        catch (NoSuchElementException e) {
            throw new ClientDisconnectedException();
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
