package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.RecursiveTask;

public class Gui extends Application {
    Game game;

    private double backgroundWidth;
    private double backgroundHeight;
    private double dimMult;

    private static String imgBackground = "file:images/background.png";
    private static String dirPlayerboard = "file:images/playerBoard/";
    private static String dirLoot = "file:images/loot/";
    private static String dirDrops = "file:images/drops/";
    private static String dirPawns = "file:images/playerPawn/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //A possible faster solution can be to add directly to a single canvas all the images or to at least a single Pane

        backgroundWidth = 1600;
        backgroundHeight = backgroundWidth*9/16;

        dimMult = backgroundWidth/1920;

        Pane masterPane = new Pane();

        primaryStage.setTitle("Adrenalina");

        game = Game.jsonDeserialize("resources/baseGame.json");

        //Settings for testing
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

            //ammo = new Ammunitions();
            ammo.add(Color.YELLOW, 2);
            ammo.add(Color.BLUE, 1);

            damage[0] = "p2";
            damage[1] = "p2";
            damage[2] = "p3";

            marks.addAll(Arrays.asList("p2", "p3", "p3"));
            marks.addAll(Arrays.asList("p4", "p5", "p4"));
        }));

        Map map = Map.jsonDeserialize(1);

        for(int i=0; i<3; i++){
            ((SpawnCell)map.getCell(2,0)).refillWeapon(game.getWeaponsDeck().draw());
            ((SpawnCell)map.getCell(0,1)).refillWeapon(game.getWeaponsDeck().draw());
            ((SpawnCell)map.getCell(3,2)).refillWeapon(game.getWeaponsDeck().draw());
        }

        ((RegularCell)map.getCell(0, 0)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(0, 2)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(1, 0)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(1, 1)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(1, 2)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(2, 1)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(2, 2)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(3, 1)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));

        //it's just for test
        for(Player p:players){
            int x, y;
            do {
                x = new Random().nextInt(4);
                y = new Random().nextInt(3);
            }while(map.getCell(x, y) == null);
            //x=1;
            //y=1;

            map.getCell(x, y).addPawn(p);
        }

        //END of settings for testing

        masterPane.getChildren().addAll(drawBackground(), drawMap(1), drawPawnsOnMap(map), drawLootOnMap(map), drawDecks(), drawAllPlayersBoards(players,false),
                drawMyWeapons(me.getWeapons()), drawMyPowers(me.getPowers()), drawMyAmmo(me.getAmmo()), drawPoints(5), drawWeaponsLoot(map));

        primaryStage.setScene(new Scene(masterPane));
        primaryStage.setResizable(false);
        primaryStage.show();

        //Event handlers
        /*primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            backgroundWidth = newVal.doubleValue();
            rearrangeItems();
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            backgroundHeight = newVal.doubleValue();
            rearrangeItems();
        });*/
    }

    private Canvas drawBackground(){
        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);
        canvas.getGraphicsContext2D().drawImage( new Image(imgBackground), 0, 0, backgroundWidth, backgroundHeight);
        return canvas;
    }

    private Canvas drawMap(int mapNum){
        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);

        double width = 1142 * dimMult;
        double height = 866 * dimMult;
        double x = 18 * dimMult;
        double y = x;

        canvas.getGraphicsContext2D().drawImage( new Image("file:images/map/map" + mapNum + ".png"), x, y, width, height);

        return canvas;
    }

    private Canvas drawLootOnMap (Map map){
        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //dimensions are the same
        double size = 55 * dimMult;
        double x;
        double y;

        //Manually add for each cell because they haven't the same dim. Create row by row from the top one
        if(map.getCell(0, 0) != null) {
            x = 310 * dimMult;
            y = 325 * dimMult;
            gc.drawImage(new Image( dirLoot + ((RegularCell)map.getCell(0, 0)).getLoot().getContentAsString() +".png"), x, y, size, size);
        }
        if(map.getCell(1, 0) != null) {
            x = 526 * dimMult;
            y = 325 * dimMult;
            gc.drawImage(new Image(dirLoot + ((RegularCell)map.getCell(1, 0)).getLoot().getContentAsString() +".png"), x, y, size, size);
        }
        if(map.getCell(3, 0) != null) {
            x = 900 * dimMult;
            y = 900 * dimMult;
            gc.drawImage(new Image(dirLoot + ((RegularCell)map.getCell(3, 0)).getLoot().getContentAsString() +".png"), x, y, size, size);
        }
        if(map.getCell(1, 1) != null) {
            x = 526 * dimMult;
            y = 530 * dimMult;
            gc.drawImage(new Image(dirLoot + ((RegularCell)map.getCell(1, 1)).getLoot().getContentAsString() +".png"), x, y, size, size);
        }
        if(map.getCell(2, 1) != null) {
            x = 725 * dimMult;
            y = 530 * dimMult;
            gc.drawImage(new Image(dirLoot + ((RegularCell)map.getCell(2, 1)).getLoot().getContentAsString() +".png"), x, y, size, size);
        }
        if(map.getCell(3, 1) != null) {
            x = 900 * dimMult;
            y = 530 * dimMult;
            gc.drawImage(new Image(dirLoot + ((RegularCell)map.getCell(3, 1)).getLoot().getContentAsString() +".png"), x, y, size, size);
        }
        if(map.getCell(0, 2) != null) {
            x = 335 * dimMult;
            y = 730 * dimMult;
            gc.drawImage(new Image(dirLoot + ((RegularCell)map.getCell(0, 2)).getLoot().getContentAsString() +".png"), x, y, size, size);
        }
        if(map.getCell(1, 2) != null) {
            x = 526 * dimMult;
            y = 730 * dimMult;
            gc.drawImage(new Image(dirLoot + ((RegularCell)map.getCell(1, 2)).getLoot().getContentAsString() +".png"), x, y, size, size);
        }
        if(map.getCell(2, 2) != null) {
            x = 725 * dimMult;
            y = 730 * dimMult;
            gc.drawImage(new Image(dirLoot + ((RegularCell)map.getCell(2, 2)).getLoot().getContentAsString() +".png"), x, y, size, size);
        }

        return canvas;
    }

    private Canvas drawPawnsOnMap (Map map){
        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

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

        return canvas;
    }

    private StackPane drawWeaponsLoot(Map map){
        StackPane root = new StackPane();

        //dimensions are the same
        double width = 104 * dimMult;
        double height = 174 * dimMult;
        double x = 624 * dimMult;
        double y = 4 * dimMult;

        //calculate distance from board to board
        double deltaX = 126 * dimMult;
        double deltaY = deltaX;

        //First the blue spawn
        SpawnCell c = (SpawnCell) map.getCell(2,0);
        for(Weapon w:c.getWeapons()){
            CardGui card = new CardGui(w, backgroundWidth, backgroundHeight, width, height, x, y, 0);
            root.getChildren().add(card);

            x += deltaX;
        }

        //set values for new position (red)
        x = 4 * dimMult;
        y = 335 * dimMult;
        //Red spawn
        c = (SpawnCell) map.getCell(0, 1);
        for(Weapon w:c.getWeapons()){
            CardGui card = new CardGui(w, backgroundWidth, backgroundHeight, width, height, x, y, -90);

            root.getChildren().add(card);

            y += deltaY;
        }

        //set values for new position (yellow)
        x = 1006 * dimMult;
        y = 510 * dimMult;
        //Yellow spawn
        c = (SpawnCell) map.getCell(3, 2);
        for(Weapon w:c.getWeapons()){
            CardGui card = new CardGui(w, backgroundWidth, backgroundHeight, width, height, x, y, +90);

            root.getChildren().add(card);

            y += deltaY;
        }

        return root;
    }

    private StackPane drawDecks(){
        StackPane root = new StackPane();

        //PowersDeck
        Canvas pow = new Canvas(backgroundWidth, backgroundHeight);
        double width = 80 * dimMult;
        double height = 109 * dimMult;
        double x = 1044 * dimMult;
        double y = 65 * dimMult;
        pow.getGraphicsContext2D().drawImage(new Image("file:images/power/powerBackPile.png"), x, y, width, height);

        //WeaponsDeck
        Canvas wea = new Canvas(backgroundWidth, backgroundHeight);
        width = 100 * dimMult;
        height = 174 * dimMult;
        x = 1018 * dimMult;
        y = 252 * dimMult;
        wea.getGraphicsContext2D().drawImage(new Image("file:images/weapon/weaponBackPile.png"), x, y, width, height);

        root.getChildren().addAll(pow, wea);
        return root;
    }

    private StackPane drawAllPlayersBoards(List<Player> players, boolean adrenalineMode){
        StackPane root = new StackPane();

        //dimensions are the same for all the players
        double width = 560 * dimMult;
        double height = 134 * dimMult;
        double x = 1222 * dimMult;
        double y = 74 * dimMult;

        //calculate distance from board to board
        double deltaY = 169 * dimMult;

        for(Player p : players){
            root.getChildren().add(drawPlayerBoard(p, players, adrenalineMode, width, height, x, y));
            y += deltaY;
        }

        return root;
    }

    private StackPane drawPlayerBoard(Player player, List<Player> players, boolean adrenalineMode, double width, double height, double x, double y){
        StackPane pane = new StackPane();
        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

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

        pane.getChildren().add(canvas);
        return pane;
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
            CardGui card = new CardGui(w, backgroundWidth, backgroundHeight, width, height, x, y, 0);
            root.getChildren().add(card);
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
            CardGui card = new CardGui(p, backgroundWidth, backgroundHeight, width, height, x, y);
            root.getChildren().add(card);
            x += deltaX;
        }

        return root;
    }

    private StackPane drawMyAmmo(Ammunitions ammo){
        StackPane root = new StackPane();

        //dimensions are the same
        double width = 35 * dimMult;
        double x = 837 * dimMult;
        double y = 958 * dimMult;

        //calculate distance from board to board
        double deltaX = 43 * dimMult;
        double deltaY = 36 * dimMult;

        Canvas ammoCanvas = new Canvas(backgroundWidth, backgroundHeight);
        GraphicsContext gc = ammoCanvas.getGraphicsContext2D();
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

        root.getChildren().add(ammoCanvas);
        return root;
    }

    private StackPane drawPoints(int points){

        StackPane s = new StackPane();
        double x = 1055 * dimMult;
        double y = 970 * dimMult;

        //FIXME show points @ the right pos like players' names
        //s.getChildren().add(new Text(x, y, Integer.toString(points)));

        return s;
    }
}
