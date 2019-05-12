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
import java.util.List;

public class Gui extends Application {
    Game game;

    private double backgroundWidth;
    private double backgroundHeight;

    private static String IMG_BACKGROUND = "file:images/background.png";
    private static String DIR_PLAYERBOARD = "file:images/playerBoard/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //A possible faster solution can be to add directly to a single canvas all the images or to at least a single Pane

        backgroundWidth = 1920;
        backgroundHeight = backgroundWidth*9/16;
        Pane masterPane = new Pane();

        primaryStage.setTitle("Adrenalina");

        game = Game.jsonDeserialize("resources/baseGame.json");

        //Settings for testing
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("fsgb", "!", Fighter.VIOLETTA));
        players.add(new Player("fsgb", "!", Fighter.DSTRUTTOR3));
        players.add(new Player("fsgb", "!", Fighter.SPROG));
        //players.add(new Player("fsgb", "!", Fighter.BANSHEE));
        //players.add(new Player("fsgb", "!", Fighter.DOZER));

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
        }));

        Map map = Map.jsonDeserialize(1);

        for(int i=0; i<3; i++){
            ((SpawnCell)map.getCell(2,0)).refillWeapon(game.getWeaponsDeck().draw());
            ((SpawnCell)map.getCell(0,1)).refillWeapon(game.getWeaponsDeck().draw());
            ((SpawnCell)map.getCell(3,2)).refillWeapon(game.getWeaponsDeck().draw());
        }

        ((RegularCell)map.getCell(0,0)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(1, 0)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(1, 1)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(1, 2)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(2, 1)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(2, 2)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));
        ((RegularCell)map.getCell(3, 1)).refillLoot(new Loot(new Color[]{Color.POWER, Color.YELLOW, Color.BLUE}));



        //END of settings for testing

        masterPane.getChildren().addAll(drawBackground(), drawMap(map), drawAllPlayersBoards(players,false),
                drawMyWeapons(me.getWeapons()), drawMyPowers(me.getPowers()), drawMyAmmo(me.getAmmo()));

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
        canvas.getGraphicsContext2D().drawImage( new Image( IMG_BACKGROUND ), 0, 0, backgroundWidth, backgroundHeight);
        return canvas;
    }

    private StackPane drawMap(Map map){
        double widthMult = 0.605;
        double heightMult = 0.815;
        double width = backgroundWidth*widthMult;
        double height = backgroundHeight*heightMult;
        double x = 18 * widthMult;
        double y = x;

        StackPane s = new StackPane();

        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage( new Image("file:images/map/map1.png"), x, y, width, height); //TODO print the right map

        //drawAmmoLootOnMap(gc, map);

        s.getChildren().addAll( canvas, drawWeaponsLoot(map), drawDecks());

        return s;
    }

    private StackPane drawWeaponsLoot(Map map){
        StackPane root = new StackPane();

        //dimensions are the same
        double widthMult = 0.056;
        double heightMult = 0.165;
        double xMult = 0.327;
        double yMult = 1/216;
        double width = backgroundWidth * widthMult;
        double height = backgroundHeight * heightMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double deltaX = backgroundWidth * 0.067;
        double deltaY = deltaX;

        //First the blue spawn
        SpawnCell c = (SpawnCell) map.getCell(2,0);
        for(Weapon w:c.getWeapons()){
            CardGui card = new CardGui(w, backgroundWidth, backgroundHeight, width, height, x, y, 0);
            root.getChildren().add(card);

            x += deltaX;
        }

        //set values for new position (red)
        xMult = 1/960;
        yMult = 0.31;
        x = backgroundWidth * xMult;
        y = backgroundHeight * yMult;
        //Red spawn
        c = (SpawnCell) map.getCell(0, 1);
        for(Weapon w:c.getWeapons()){
            CardGui card = new CardGui(w, backgroundWidth, backgroundHeight, width, height, x, y, -90);

            root.getChildren().add(card);

            y += deltaY;
        }

        //set values for new position (yellow)
        xMult = 0.525;
        yMult = 0.476;
        x = backgroundWidth * xMult;
        y = backgroundHeight * yMult;
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
        double widthMult = 0.04;
        double heightMult = 0.12;
        double xMult = 0.555;
        double yMult = 0.061;
        double width = backgroundWidth * widthMult;
        double height = backgroundHeight * heightMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;
        pow.getGraphicsContext2D().drawImage(new Image("file:images/power/powerBackPile.png"), x, y, width, height);

        //WeaponsDeck
        Canvas wea = new Canvas(backgroundWidth, backgroundHeight);
        widthMult = 0.06;
        heightMult = 0.175;
        xMult = 0.535;
        yMult = 0.233;
        width = backgroundWidth * widthMult;
        height = backgroundHeight * heightMult;
        x = backgroundWidth * xMult;
        y = backgroundHeight * yMult;
        wea.getGraphicsContext2D().drawImage(new Image("file:images/weapon/weaponBackPile.png"), x, y, width, height);

        root.getChildren().addAll(pow, wea);
        return root;
    }

    private void drawInsideCells (GraphicsContext mapGc, Map map){
        //dimensions are the same
        double sizeMult = 0.026;
        double xMult = 0.277;
        double yMult = 0.3;
        double size = backgroundWidth * sizeMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double deltaX = backgroundWidth * 0.053;

        for(int i=0; i<4; i++){
            for(int j=0; j<3; j++){
                if(map.getCell(i, j) != null){
                    //if(map.getCell(i, j).hasSpawn())TODO check if it's a spown or not

                    Image l = new Image("file:images/loot/" + ((RegularCell)map.getCell(i,j)).getLoot().getContentAsString() + ".png");
                    mapGc.drawImage(l, x, y, size, size);
                }
            }
            //CardGui card = new CardGui(p, backgroundWidth, backgroundHeight, width, height, x, y);
            //root.getChildren().add(card);
            x += deltaX;
        }


    }

    private StackPane drawAllPlayersBoards(List<Player> players, boolean adrenalineMode){
        StackPane root = new StackPane();

        //dimensions are the same for all the players
        double widthMult = 0.29;
        double heightMult = 0.124;
        double xMult = 0.6365;
        double yMult = 0.0685;
        double width = backgroundWidth * widthMult;
        double height = backgroundHeight * heightMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double delta_y = backgroundHeight * 0.1565;

        for(Player p : players){
            root.getChildren().add(drawPlayerBoard(p, adrenalineMode, width, height, x, y));
            y += delta_y;
        }

        return root;
    }

    private StackPane drawPlayerBoard(Player player, boolean adrenalineMode, double width, double height, double x, double y){
        //TODO write player name, draw marks, draw skulls
        StackPane s = new StackPane();

        Canvas canvas = new Canvas(backgroundWidth, backgroundHeight);
        s.getChildren().add( canvas );

        canvas.getGraphicsContext2D().drawImage( new Image(DIR_PLAYERBOARD + player.getCharacter().toString() + (adrenalineMode?"_A":"") + ".png"), x, y, width, height);

        return s;
    }

    private StackPane drawMyWeapons(List<Weapon> weapons){
        StackPane root = new StackPane();

        //dimensions are the same
        double widthMult = 0.0625;
        double heightMult = 0.188;
        double xMult = 0.024;
        double yMult = 0.8;
        double width = backgroundWidth * widthMult;
        double height = backgroundHeight * heightMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double deltaX = backgroundWidth * 0.0725;

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
        double widthMult = 0.048;
        double heightMult = 0.135;
        double xMult = 0.253;
        double yMult = 0.853;
        double width = backgroundWidth * widthMult;
        double height = backgroundHeight * heightMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double deltaX = backgroundWidth * 0.053;

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
        double widthMult = 0.018;
        double xMult = 0.435;
        double yMult = 0.887;
        double width = backgroundWidth * widthMult;
        double x = backgroundWidth * xMult;
        double y = backgroundHeight * yMult;

        //calculate distance from board to board
        double deltaX = backgroundWidth * 0.0224;
        double deltaY = backgroundHeight * 0.0333;

        Canvas ammoCanvas = new Canvas(backgroundWidth, backgroundHeight);
        GraphicsContext gc = ammoCanvas.getGraphicsContext2D();
        Image blue = new Image("file:images/loot/blue.png");
        Image red = new Image("file:images/loot/red.png");
        Image yellow = new Image("file:images/loot/yellow.png");

        for(int i=0; i<ammo.getBlue(); i++){
            gc.drawImage(blue, x, y, width, width);
            x += deltaX;
        }
        x = backgroundWidth * xMult; //reset x
        y += deltaY;

        for(int i=0; i<ammo.getRed(); i++){
            gc.drawImage(red, x, y, width, width);
            x += deltaX;
        }
        x = backgroundWidth * xMult; //reset x
        y += deltaY;

        for(int i=0; i<ammo.getYellow(); i++){
            gc.drawImage(yellow, x, y, width, width);
            x += deltaX;
        }

        root.getChildren().add(ammoCanvas);
        return root;
    }

    //TODO points
    public StackPane drawPoints(int p){
        return null;
    }
}
