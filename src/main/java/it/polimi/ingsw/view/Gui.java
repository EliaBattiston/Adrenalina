package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class Gui extends Application implements UserInterface{
    private double backgroundWidth;
    private double backgroundHeight;
    private double dimMult;
    private static double positionFix = 35; //position fix for drawing the weapons on the map

    private static String imgBackground = "file:images/background.png";
    private static String dirPlayerboard = "file:images/playerBoard/";
    private static String dirLoot = "file:images/loot/";
    private static String dirDrops = "file:images/drops/";
    private static String dirPawns = "file:images/playerPawn/";

    private GraphicsContext gc;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Game game;

        backgroundWidth = 1920;
        backgroundHeight = backgroundWidth*9/16;

        dimMult = backgroundWidth/1920;

        //Settings for testing
        game = Game.jsonDeserialize("resources/baseGame.json");
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("p1", "!", Fighter.VIOLETTA));
        players.add(new Player("p2", "!", Fighter.DSTRUTTOR3));
        players.add(new Player("p3", "!", Fighter.SPROG));
        players.add(new Player("p4", "!", Fighter.BANSHEE));
        players.add(new Player("p5", "!", Fighter.DOZER));

        Player me = players.get(0);

        me.applyEffects(((damage, marks, position, weapons, powers, ammo) -> {
            game.getWeaponsDeck().shuffle();
            weapons[0] = game.getWeaponsDeck().draw();
            weapons[1] = game.getWeaponsDeck().draw();

            game.getPowersDeck().shuffle();
            powers[0] = game.getPowersDeck().draw();
            powers[1] = game.getPowersDeck().draw();

            ammo.add(Color.YELLOW, 2);
            ammo.add(Color.BLUE, 1);

            damage[0] = "p2";
            damage[1] = "p2";
            damage[2] = "p3";

            marks.addAll(Arrays.asList("p2", "p3", "p3"));
            marks.addAll(Arrays.asList("p4", "p5", "p4"));
        }));

        game.loadMap(1);

        for(int x = 0; x < 4; x++)
            for(int y = 0; y < 3; y++)
                if(game.getMap().getCell(x, y) != null)
                    game.getMap().getCell(x, y).refill(game);

        //it's just for test
        for(Player p:players){
            int x, y;
            do {
                x = new Random().nextInt(4);
                y = new Random().nextInt(3);
            }while(game.getMap().getCell(x, y) == null);

            game.getMap().getCell(x, y).addPawn(p);
        }

        //END of settings for testing

        primaryStage.setTitle("Adrenalina");
        primaryStage.setScene(new Scene(drawGame(game.getMap(), me, players)));
        primaryStage.setResizable(true);
        //primaryStage.setFullScreen(true);
        primaryStage.show();

        //Event handlers

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            if(abs(newVal.doubleValue() - backgroundWidth) > 15) {
                backgroundWidth = newVal.doubleValue();
                backgroundHeight = backgroundWidth * 9 / 16;

                dimMult = backgroundWidth / 1920;
                primaryStage.setScene(new Scene(drawGame(game.getMap(), me, players)));
            }
        });

        // primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> { });
    }

    private Pane drawGame(Map map, Player me, List<Player> players){
        Pane masterPane;
        Canvas canvas;
        StackPane a, b, c, d;

        masterPane = new Pane();
        canvas = new Canvas(backgroundWidth, backgroundHeight);
        gc = canvas.getGraphicsContext2D();

        drawBackground();
        drawMap(1);
        drawPawnsOnMap(map);

        drawDecks();
        drawAllPlayersBoards(players,false);
        drawMyAmmo(me.getAmmo());

        a = drawMyWeapons(me.getWeapons());
        b = drawMyPowers(me.getPowers());
        c = drawWeaponsLoot(map);
        d = drawLootOnMap(map);

        a.setPickOnBounds(false);
        b.setPickOnBounds(false);
        c.setPickOnBounds(false);
        d.setPickOnBounds(false);
        masterPane.setPickOnBounds(false);
        masterPane.getChildren().addAll(canvas,  a, b, c, d);

        return masterPane;
    }

    private void drawBackground(){
        gc.drawImage( new Image(imgBackground), 0, 0, backgroundWidth, backgroundHeight);
    }

    private void drawMap(int mapNum){
        double width = 1142 * dimMult;
        double height = 866 * dimMult;
        double x = 18 * dimMult;
        double y = x;

        gc.drawImage( new Image("file:images/map/map" + mapNum + ".png"), x, y, width, height);
    }

    private StackPane drawLootOnMap (Map map){
        StackPane root = new StackPane();

        //dimensions are the same
        double size = 55 * dimMult;
        double x;
        double y;

        //Manually add for each cell because they haven't the same dim. Create row by row from the top one
        if(map.getCell(0, 0) != null) {
            x = 310 * dimMult;
            y = 325 * dimMult;
            //gc.drawImage(new Image( dirLoot + ((RegularCell)map.getCell(0, 0)).getLoot().getContentAsString() +".png"), x, y, size, size);
            GuiCard card = new GuiCard(((RegularCell)map.getCell(0, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 0) != null) {
            x = 526 * dimMult;
            y = 325 * dimMult;
            GuiCard card = new GuiCard(((RegularCell)map.getCell(1, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(3, 0) != null) {
            x = 900 * dimMult;
            y = 900 * dimMult;
            GuiCard card = new GuiCard(((RegularCell)map.getCell(3, 0)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 1) != null) {
            x = 526 * dimMult;
            y = 530 * dimMult;
            GuiCard card = new GuiCard(((RegularCell)map.getCell(1, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(2, 1) != null) {
            x = 725 * dimMult;
            y = 530 * dimMult;
            GuiCard card = new GuiCard(((RegularCell)map.getCell(2, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(3, 1) != null) {
            x = 900 * dimMult;
            y = 530 * dimMult;
            GuiCard card = new GuiCard(((RegularCell)map.getCell(3, 1)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(0, 2) != null) {
            x = 335 * dimMult;
            y = 730 * dimMult;
            GuiCard card = new GuiCard(((RegularCell)map.getCell(0, 2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(1, 2) != null) {
            x = 526 * dimMult;
            y = 730 * dimMult;
            GuiCard card = new GuiCard(((RegularCell)map.getCell(1, 2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }
        if(map.getCell(2, 2) != null) {
            x = 725 * dimMult;
            y = 730 * dimMult;
            GuiCard card = new GuiCard(((RegularCell)map.getCell(2,2)).getLoot(), size);
            card.setPosition(x, y);
            root.getChildren().add(card);
        }

        return root;
    }

    private void drawPawnsOnMap (Map map){
        double size = 50 * dimMult;
        double baseX = 224 * dimMult;
        double baseY = 224 * dimMult;
        double x;
        double y;
        double deltaCellX = 186 * dimMult;
        double deltaCellY = 200 * dimMult;
        boolean xNotY;

        //FIXME actually it print a max of 3 players in a single cell because of the wrong managing of the xNotY, find a better way
        for(int j=0; j<3; j++){
            for(int i=0; i<4; i++){
                xNotY = true;
                x = baseX + i * (deltaCellX + (i==3?10:0)); //the last column is farther
                y = baseY + j * deltaCellY;
                if(map.getCell(i, j) != null) { //here X and Y are pointing at the top-left corner of the cell
                    for (Player p : map.getCell(i, j).getPawns()) {
                        gc.drawImage(new Image(dirPawns + p.getCharacter().toString() + ".png"), x, y, size, size);
                        if (xNotY) {
                            x += size;
                            y = baseY + j * deltaCellY;
                        } else {
                            y += size;
                            x = baseX + i * (deltaCellX +(i==3?10:0));
                        }

                        xNotY = !xNotY;
                    }
                }
            }
        }
    }

    private StackPane drawWeaponsLoot(Map map){
        StackPane root = new StackPane();

        //dimensions are the same
        double width = 104 * dimMult;
        double height = 174 * dimMult;
        double x = (624 - positionFix)* dimMult;
        double y = 4 * dimMult;

        //calculate distance from board to board
        double deltaX = 126 * dimMult;
        double deltaY = deltaX;

        //First the blue spawn
        SpawnCell c = (SpawnCell) map.getCell(2,0);
        for(Weapon w:c.getWeapons()){
            GuiCard card = new GuiCard(w, width, height, 0);

            card.setPickOnBounds(false);
            card.setTranslateX(x);
            card.setTranslateY(y);

            root.getChildren().add(card);
            x += deltaX;
        }

        //set values for new position (red)
        x = 4 * dimMult;
        y = (335 - positionFix) * dimMult;
        //Red spawn
        c = (SpawnCell) map.getCell(0, 1);
        for(Weapon w:c.getWeapons()){
            GuiCard card = new GuiCard(w, width, height, -90);

            card.setPickOnBounds(false);
            card.setTranslateX(x);
            card.setTranslateY(y);
            root.getChildren().add(card);

            y += deltaY;
        }

        //set values for new position (yellow)
        x = 1006 * dimMult;
        y = (510 - positionFix) * dimMult;
        //Yellow spawn
        c = (SpawnCell) map.getCell(3, 2);
        for(Weapon w:c.getWeapons()){
            GuiCard card = new GuiCard(w, width, height, +90);
            root.getChildren().add(card);

            card.setPickOnBounds(false);
            card.setTranslateX(x);
            card.setTranslateY(y);

            y += deltaY;
        }
        return root;
    }

    private void drawDecks(){
        //PowersDeck
        double width = 80 * dimMult;
        double height = 109 * dimMult;
        double x = 1044 * dimMult;
        double y = 65 * dimMult;
        gc.drawImage(new Image("file:images/power/powerBackPile.png"), x, y, width, height);

        //WeaponsDeck
        width = 100 * dimMult;
        height = 174 * dimMult;
        x = 1018 * dimMult;
        y = 252 * dimMult;
        gc.drawImage(new Image("file:images/weapon/weaponBackPile.png"), x, y, width, height);
    }

    private void drawAllPlayersBoards(List<Player> players, boolean adrenalineMode){
        //dimensions are the same for all the players
        double width = 560 * dimMult;
        double height = 134 * dimMult;
        double x = 1222 * dimMult;
        double y = 74 * dimMult;

        //calculate distance from board to board
        double deltaY = 169 * dimMult;

        for(Player p : players){
            drawPlayerBoard(p, players, adrenalineMode, width, height, x, y);
            y += deltaY;
        }
    }

    private void drawPlayerBoard(Player player, List<Player> players, boolean adrenalineMode, double width, double height, double x, double y){
        double pbMult = width/1123; //(dimMult * width@1080p)/textureWidth -> internal reference based on the card
        double xDrop = (adrenalineMode?130:116) * pbMult + x;
        double yDrop = 116 * pbMult + y;
        double widthDrop = 30 * pbMult;
        double heightDrop = 45 * pbMult;

        double deltaX = (adrenalineMode?61:63) * pbMult;

        //FIXME it stays in the middle of the window, find a way to move it
        //y - 30 -> write the name of the player
        /*Text t = new Text(backgroundWidth, backgroundHeight, player.getNick() + " - " + player.getCharacter().toString());
        t.setX(x);
        t.setY(y);
        t.setFont(Font.font ("Verdana", 20));
        t.setFill(javafx.scene.paint.Color.WHITE);
        pane.getChildren().add(t);*/

        gc.drawImage( new Image(dirPlayerboard + player.getCharacter().toString() + (adrenalineMode?"_A":"") + ".png"), x, y, width, height);

        //damages
        for(int i=0; i<12; i++){
            if(player.getReceivedDamage()[i] != null)
                gc.drawImage( new Image(dirDrops + Player.fighterFromNick(players, player.getReceivedDamage()[i]) + ".png"), xDrop, yDrop, widthDrop, heightDrop);

            xDrop += deltaX;
        }

        //marks
        xDrop = 537 * pbMult + x;
        yDrop = 4 * pbMult + y;
        deltaX = widthDrop * 1.1; //put just a little bit of space, we don't know how many marks a player will get
        for(String p: player.getReceivedMarks()){
            if(p != null)
                gc.drawImage( new Image(dirDrops + Player.fighterFromNick(players, p) + ".png"), xDrop, yDrop, widthDrop, heightDrop);

            xDrop += deltaX;
        }
    }

    private StackPane drawMyWeapons(List<Weapon> weapons){
        StackPane root = new StackPane();

        //dimensions are the same
        double width = 120 * dimMult;
        double height = 203 * dimMult;
        double x = 46 * dimMult;
        double y = 865 * dimMult;

        //calculate distance from board to board
        double deltaX = 136 * dimMult;

        for(Weapon w : weapons){
            GuiCard card = new GuiCard(w, width, height, 0);
            root.getChildren().add(card);

            card.setPickOnBounds(false);

            card.setTranslateX(x);
            card.setTranslateY(y);
            x += deltaX;
        }

        return root;
    }

    private StackPane drawMyPowers(List<Power> powers){
        StackPane root = new StackPane();

        //dimensions are the same
        double width = 92 * dimMult;
        double height = 146 * dimMult;
        double x = 484 * dimMult;
        double y = 920 * dimMult;

        //calculate distance from board to board
        double deltaX = 102 * dimMult;

        for(Power p : powers){
            GuiCard card = new GuiCard(p, width, height);
            card.setPosition(x, y);
            root.getChildren().add(card);
            x += deltaX;
        }

        return root;
    }

    private void drawMyAmmo(Ammunitions ammo){
        //dimensions are the same
        double width = 35 * dimMult;
        double x = 837 * dimMult;
        double y = 958 * dimMult;

        //calculate distance from board to board
        double deltaX = 43 * dimMult;
        double deltaY = 36 * dimMult;

        Image blue = new Image("file:images/loot/blue.png");
        Image red = new Image("file:images/loot/red.png");
        Image yellow = new Image("file:images/loot/yellow.png");

        for(int i=0; i<ammo.getBlue(); i++){
            gc.drawImage(blue, x, y, width, width);
            x += deltaX;
        }
        x = 837 * dimMult; //reset x
        y += deltaY;

        for(int i=0; i<ammo.getRed(); i++){
            gc.drawImage(red, x, y, width, width);
            x += deltaX;
        }
        x = 837 * dimMult; //reset x
        y += deltaY;

        for(int i=0; i<ammo.getYellow(); i++){
            gc.drawImage(yellow, x, y, width, width);
            x += deltaX;
        }
    }

    private StackPane drawPoints(int points){

        StackPane s = new StackPane();
        double x = 1055 * dimMult;
        double y = 970 * dimMult;

        //FIXME show points @ the right pos like players' names
        //s.getChildren().add(new Text(x, y, Integer.toString(points)));

        return s;
    }

    /**
     * Update the actual gameView to the client
     *
     * @param matchView current game view
     */
    @Override
    public void updateGame(MatchView matchView) {

    }

    /**
     * Asks the user to choose between a set of actions he can use
     *
     * @param available  List of available actions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen action
     */
    @Override
    public Action chooseAction(List<Action> available, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user to choose between a set of his weapons
     *
     * @param available  List of available weapons
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    @Override
    public Weapon chooseWeapon(List<Weapon> available, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user to choose which weapon he wants to buy from the SpawnCell
     *
     * @param grabbable  List of weapons that can be picked up by the player
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    @Override
    public Weapon grabWeapon(List<Weapon> grabbable, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user which unloaded weapons located in his hand he wants to reload
     *
     * @param reloadable Weapons that are currently not loaded
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Weapon to be reloaded
     */
    @Override
    public Weapon reload(List<Weapon> reloadable, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user where he wants to movePlayer
     *
     * @param destinations Possible destinations for the user
     * @param mustChoose   If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the player will be when he's done moving
     */
    @Override
    public Point movePlayer(List<Point> destinations, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user which enemy he wants to target with an effect between a list of possible enemies
     *
     * @param targets    List of player that can be targeted
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen target
     */
    @Override
    public Player chooseTarget(List<Player> targets, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user where to movePlayer an enemy
     *
     * @param enemy        Enemy to be moved by the player
     * @param destinations Possible destinations for the enemy
     * @param mustChoose   If false, the user can choose not to choose. In this case the function returns null
     * @return Point where the enemy will be after being moved
     */
    @Override
    public Point moveEnemy(Player enemy, List<Point> destinations, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user to discard one power card
     *
     * @param powers     List of power cards in player's hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Card to be discarded
     */
    @Override
    public Power discardPower(List<Power> powers, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user to choose a room
     *
     * @param rooms      list of possible rooms
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen room
     */
    @Override
    public Integer chooseRoom(List<Integer> rooms, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the player to choose a direction
     *
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen direction
     */
    @Override
    public Direction chooseDirection(boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user to choose a precise position on the map
     *
     * @param positions  list of possible positions
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return chosen position
     */
    @Override
    public Point choosePosition(List<Point> positions, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user for the nickname
     *
     * @return user's nickname
     */
    @Override
    public String getNickname() {
        return null;
    }

    /**
     * Asks the user for the effect phrase
     *
     * @return user's effect phrase
     */
    @Override
    public String getPhrase() {
        return null;
    }

    /**
     * Asks the user fot the fighter
     *
     * @return user's fighter
     */
    @Override
    public Fighter getFighter() {
        return null;
    }

    /**
     * Asks the user how many skulls he wants in the play
     *
     * @return skulls number
     */
    @Override
    public Integer getSkullNum() {
        return null;
    }

    /**
     * Asks the user to choose which weapon to discard
     *
     * @param inHand     List of weapons in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen weapon
     */
    @Override
    public Weapon discardWeapon(List<Weapon> inHand, boolean mustChoose) {
        return null;
    }

    /**
     * Asks the user to choose which map he wants to use
     *
     * @return Number of the chosen map
     */
    @Override
    public Integer chooseMap() {
        return null;
    }

    /**
     * Asks the user about the Frenzy mode for the starting match
     *
     * @return True for final Frenzy mode, false elsewhere
     */
    @Override
    public Boolean chooseFrenzy() {
        return null;
    }

    /**
     * Asks the user to choose a power to use
     *
     * @param inHand     List of powers in hand
     * @param mustChoose If false, the user can choose not to choose. In this case the function returns null
     * @return Chosen power
     */
    @Override
    public Power choosePower(List<Power> inHand, boolean mustChoose) {
        return null;
    }
}